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
        Optional<User> user = userRepo.getUserById(id);

        if(!user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
        } else {
            return user.get();
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepo.findUserByUsernameIgnoreCase(username.toLowerCase());
    }

    @Override
    public boolean isUsernameClaimed(String username) {
        return userRepo.existsByUsernameIgnoreCase(username.toLowerCase());
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
        User user;
        if (accessToken == null) {
            user = checkCredentials(headers, response);
        } else {
            user = checkToken(accessToken);
        }

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User checkToken(String accessToken) {
        Token token = tokenRepo.findByTokenCode(accessToken);
        if(token != null) {
            Optional<User> userOpt = userRepo.findById(token.getUserId());
            return userOpt.get();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public UserDto updateUser(Long id, User request) {
        Optional<User> existingUser = userRepo.getUserById(id);
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

    // salting and hashing a password
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

    // check a password(data) against a salt and hash
    private static boolean verifyPassword(String data, String salt, String hash) {
        // prepend salt to password and hash
        String testDigest = DigestUtils.sha256Hex(salt + data);

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
                    if (isUsernameValid((String)f.get(updatedUser))) {
                        f.set(mergedUser, f.get(updatedUser));
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
                    }
                } else if ("email".equals(f.getName()) && f.get(updatedUser) != null) {
                    if (isEmailValid((String)f.get(updatedUser))) {
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
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return mergedUser;
    }

    private String[] getCredentialsFromHeader(String authHeader) {
        byte[] decodedHeaderBytes = Base64.getDecoder().decode(authHeader.substring("Basic".length()).trim());
        String decodedHeader = new String(decodedHeaderBytes);
        return decodedHeader.split(":");
    }

    private void addCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("LOCO-USER", accessToken);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private User checkCredentials(HttpHeaders headers, HttpServletResponse response) {
        if(headers.get(HttpHeaders.AUTHORIZATION) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // get credentials from authorization header
        String[] credentials = getCredentialsFromHeader(headers.get(HttpHeaders.AUTHORIZATION).get(0));
        String username = credentials[0];
        String password = credentials[1];

        // get user to compare with
        User user =  getUserByUsername(username);

        // verify and generate accessToken
        if (user != null && verifyPassword(password, user.getPasswordSalt(), user.getPasswordHash())) {
            Token token = tokenRepo.getByUserId(user.getId());
            // if token does not exist
            if(token == null) {
                token = new Token();
                token.setUserId(user.getId());
            }
            token.setTokenCode(generateRandomToken());
            tokenRepo.save(token);
            addCookie(response, token.getTokenCode());
            return user;

        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }


    /*
     * Validation helper methods
     */

    private boolean isRequestValid(String username, String email, String encodedPassword) {
        try {
            if (isUsernameValid(username)
                    && isEmailValid(email)
                    && isPasswordValid(encodedPassword)) {
                return true;
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            // if any of the above fields are null, catch exception
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
