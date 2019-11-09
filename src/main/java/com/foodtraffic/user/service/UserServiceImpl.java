package com.foodtraffic.user.service;

import com.foodtraffic.user.model.dto.UserDto;
import com.foodtraffic.user.model.entity.Token;
import com.foodtraffic.user.model.entity.User;
import com.foodtraffic.user.repository.TokenRepo;
import com.foodtraffic.user.repository.UserRepo;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    TokenRepo tokenRepo;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepo.findUserById(id);
        if(user.isPresent()) {
            return user.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepo.getUserByUsernameIgnoreCase(username);
    }

    @Override
    public boolean isUsernameClaimed(String username) {
        return userRepo.existsByUsernameIgnoreCase(username);
    }

    @Override
    public UserDto createUser(User user) {
        if(isRequestValid(user.getUsername(), user.getEmail(), user.getPasswordHash())) {
            hashPassword(user);
            user.setEmail(user.getEmail());
            user.setUsername(user.getUsername());
            user.setJoinDate(ZonedDateTime.now());
            user.setStatus("ACTIVE");
            user = userRepo.saveAndFlush(user);
            return modelMapper.map(user, UserDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
        }
    }

    @Override
    public UserDto loginUser(HttpHeaders headers, String accessToken, HttpServletResponse response) {
        User user = (accessToken.isEmpty() ? checkCredentials(headers, response) : checkToken(accessToken));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User checkToken(String accessToken) {
        Optional<Token> token = tokenRepo.findByTokenCode(accessToken);
        if(token.isPresent()) {
            return userRepo.getOne(token.get().getUserId());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public UserDto updateUser(Long id, User request) {
        Optional<User> existingUser = userRepo.findUserById(id);
        if(existingUser.isPresent()){
            request = mergeUser(request, existingUser.get());
            UserDto userToReturn = modelMapper.map(request, UserDto.class);
            if (!request.equals(existingUser.get())) {
                userRepo.saveAndFlush(request);
            }
            return userToReturn;
        } else {
            return null;
        }
    }


    /*
     * Helper methods
     */

    // salt and hash the password
    private void hashPassword(User user) {
        // decode password
        user.setPasswordHash(new String(Base64.getDecoder().decode(user.getPasswordHash())));

        // generate a random 64-bit salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        String saltStr = Hex.encodeHexString(salt);

        // set salt and hashed password
        user.setPasswordSalt(saltStr);
        user.setPasswordHash(DigestUtils.sha256Hex(saltStr + user.getPasswordHash()));
    }

    // check password
    private static boolean verifyPassword(String test, String salt, String hash) {
        // hash password
        String testDigest = DigestUtils.sha256Hex(salt + test);

        // verify that they are the same
        return testDigest.equals(hash);
    }

    // generate a random token
    private static String generateRandomToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenValue = new byte[8];
        random.nextBytes(tokenValue);
        return Hex.encodeHexString(tokenValue);
    }

    // merge the existing user and the updated user
    private User mergeUser(User updatedUser, User existingUser) {
        User mergedUser = modelMapper.map(existingUser, User.class);
        try {
            // loop through all fields in the updated user
            for (Field f : updatedUser.getClass().getDeclaredFields()) {
                f.setAccessible(true);

                if ("username".equals(f.getName()) && f.get(updatedUser) != null) {
                    // username is being updated, validate it
                    if (isUsernameValid((String)f.get(updatedUser))) {
                        // update it
                        f.set(mergedUser, f.get(updatedUser));
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
                    }
                } else if ("email".equals(f.getName()) && f.get(updatedUser) != null) {
                    // email is being updated, validate it
                    if (isEmailValid((String)f.get(updatedUser))) {
                        // update it
                        f.set(mergedUser, f.get(updatedUser));
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
                    }
                } else if ("passwordHash".equals(f.getName()) && f.get(updatedUser) != null) {
                    // if it's the password, re-hash it
                    if (isPasswordValid((String)f.get(updatedUser))) {
                        hashPassword(mergedUser);
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return mergedUser;
    }

    // get credentials from authorization header
    private String[] getCredentialsFromHeader(List<String> authHeaders) {
        if(authHeaders != null) {
            byte[] decodedHeaderBytes = Base64.getDecoder().decode(authHeaders.get(0).substring("Basic".length()).trim());
            String decodedHeader = new String(decodedHeaderBytes);
            return decodedHeader.split(":");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    // adds cookie to the response
    private void addCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("LOCO-USER", accessToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private User checkCredentials(HttpHeaders headers, HttpServletResponse response) {
        // get credentials from authorization header
        String[] credentials = getCredentialsFromHeader(headers.get(HttpHeaders.AUTHORIZATION));

        // get user to compare with
        User user =  userRepo.getUserByUsernameIgnoreCaseOrEmailIgnoreCase(credentials[0], credentials[0]);

        String message;

        if (user == null) {
            message = "User does not exist";
        } else if (!"ACTIVE".equals(user.getStatus()) && !user.getIsEmailVerified()) {
            message = "Email verification is required";
        } else if (!verifyPassword(credentials[1], user.getPasswordSalt(), user.getPasswordHash())) {
            message = "Password is incorrect";
        } else {
            Token token = tokenRepo.getByUserId(user.getId());
            if(token == null) {
                // token does not exist, create a new one
                token = new Token();
                token.setUserId(user.getId());
            }
            token.setTokenCode(generateRandomToken());
            tokenRepo.save(token);
            addCookie(response, token.getTokenCode());
            return user;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
    }


    /*
     * Validation helper methods
     */

    private boolean isRequestValid(String username, String email, String encodedPassword) {
        try {
            return isUsernameValid(username) && isEmailValid(email) && isPasswordValid(encodedPassword);
        } catch (NullPointerException | IllegalArgumentException e) {
            // if any of the parameters are null, catch exception
            // if password hash is not long enough, decode will throw Illegal Argument exception, catch that too
            // will return 400 later
        }
        return false;
    }

    private boolean isUsernameValid(String username) {
        return username.matches("[a-zA-Z0-9_]+")
                && username.length() >= 4
                && username.length() <= 25
                && !userRepo.existsByUsernameIgnoreCase(username);
    }

    private boolean isEmailValid(String email) {
        return email.matches(emailRegex) && !userRepo.existsByEmailIgnoreCase(email);
    }

    private boolean isPasswordValid(String encodedPassword) {
        String password = new String(Base64.getDecoder().decode(encodedPassword));
        return password.length() >= 8 && password.matches("[a-zA-Z0-9!@]+");
    }

}
