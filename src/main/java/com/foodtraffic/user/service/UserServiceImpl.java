package com.foodtraffic.user.service;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.user.entity.Favorite;
import com.foodtraffic.user.entity.Token;
import com.foodtraffic.user.entity.User;
import com.foodtraffic.user.entity.UserStatus;
import com.foodtraffic.user.repository.FavoriteRepository;
import com.foodtraffic.user.repository.TokenRepository;
import com.foodtraffic.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TokenRepository tokenRepo;
    
    @Autowired
    private FavoriteRepository favoriteRepo;
    
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto getUserById(final long id) {
        Optional<User> user = userRepo.findUserById(id);
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
        }
    }

    @Override
    public boolean userExists(final String username) {
        if (username != null) {
            return userRepo.existsByUsernameIgnoreCase(username);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
    }

    @Override
    public UserDto createUser(final String authHeader, User user, HttpServletResponse response) {

        setUserCredentialsFromHeader(authHeader, user);

        if (isRequestValid(user)) {

            // create user
            hashPassword(user);
            user.setEmail(user.getEmail().toLowerCase());
            user.setUsername(user.getUsername().toLowerCase());
            user.setJoinDate(ZonedDateTime.now());
            user.setStatus(UserStatus.ACTIVE.name());
            user.setVerificationCode(generateRandomToken(16));  // generate verification code for email verification
            user = userRepo.saveAndFlush(user);

            // create user access token
            createUserToken(user.getId(), 8, response);

            return modelMapper.map(user, UserDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
        }
    }

    @Override
    public UserDto loginUser(final String authHeader, final String accessToken, HttpServletResponse response) {
        return !accessToken.isEmpty() ? checkToken(accessToken) : checkCredentials(authHeader, response);
    }

    @Override
    public UserDto checkToken(final String accessToken) {
        Optional<Token> token = tokenRepo.findByTokenCode(accessToken);
        String message;

        if (token.isPresent()) {
            User user = userRepo.getOne(token.get().getUserId());
            if (UserStatus.ACTIVE.name() != user.getStatus() && !user.isEmailVerified()) {
                message = "Email verification is required";
            } else {
                user.setLastLogin(ZonedDateTime.now());
                userRepo.save(user);
                return modelMapper.map(user, UserDto.class);
            }
        } else {
            message = "User is not authorized";
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
    }

    @Override
    public UserDto updateUser(final long id, User request, String verificationCode) {
        Optional<User> existingUser = userRepo.findUserById(id);
        if (existingUser.isPresent()){
            User user = existingUser.get();

            if (verificationCode != null && verificationCode.equals(user.getVerificationCode())) {
                user.setVerificationCode(null);
                user.setEmailVerified(true);
                userRepo.save(user);
            } else {
                request = mergeUser(request, user);
                if (!request.equals(user)) {
                    user = userRepo.save(request);
                }
            }

            return modelMapper.map(user, UserDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource does not exist");
        }
    }
    
    @Override
    public UserDto toggleFavorite(final long userId, final long vendorId) {
    	Optional<Favorite> favorite = favoriteRepo.findByUserIdAndVendorId(userId, vendorId);
    	
    	if(favorite.isPresent()) {
    		favoriteRepo.delete(favorite.get());
    	} else {
    		Favorite fav = new Favorite();
			fav.setVendorId(vendorId);
    		fav.setUserId(userId);
        	favoriteRepo.saveAndFlush(fav);
    	}
    	
    	return getUserById(userId);
    }
    
    /*
     * Helper methods
     */

    // decode the authorization header and set the credentials on the user
    private void setUserCredentialsFromHeader(String authHeader, User user) {
        if (authHeader != null) {
            String[] credentials = getCredentialsFromHeader(authHeader);
            user.setUsername(credentials[0]);
            user.setPasswordHash(credentials[1]);
        }
    }

    // get credentials from authorization header
    private String[] getCredentialsFromHeader(String authHeader) {
        if (authHeader != null) {
            byte[] decodedHeaderBytes = Base64.getDecoder().decode(authHeader);
            String decodedHeader = new String(decodedHeaderBytes);
            return decodedHeader.split(":");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization header is missing");
        }
    }

    // salt and hash the password
    private void hashPassword(User user) {
        // generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        String saltStr = Hex.encodeHexString(salt);

        // set salt and hashed password
        user.setPasswordSalt(saltStr);
        user.setPasswordHash(DigestUtils.sha256Hex(saltStr + user.getPasswordHash()));
    }

    // create a token
    private void createUserToken(long id, int tokenSize, HttpServletResponse response) {
        Token token = new Token();
        token.setUserId(id);
        token.setTokenCode(generateRandomToken(tokenSize));
        tokenRepo.save(token);
        addCookie(response, token.getTokenCode());
    }

    // adds cookie to the response
    private void addCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("_gid", accessToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    // generate a random token
    private String generateRandomToken(int size) {
        SecureRandom random = new SecureRandom();
        byte[] tokenValue = new byte[size];
        random.nextBytes(tokenValue);
        return Hex.encodeHexString(tokenValue);
    }

    // check user credentials
    private UserDto checkCredentials(String authHeader, HttpServletResponse response) {
        // get credentials from authorization header
        String[] credentials = getCredentialsFromHeader(authHeader);

        // get user to compare with
        User user =  userRepo.getUserByUsernameIgnoreCaseOrEmailIgnoreCase(credentials[0], credentials[0]);

        String message;

        if (user == null) {
            message = "User does not exist";
        } else if (UserStatus.ACTIVE.name() != user.getStatus() && !user.isEmailVerified()) {
            message = "Email verification is required";
        } else if (!testPassword(credentials[1], user.getPasswordSalt(), user.getPasswordHash())) {
            message = "Password is incorrect";
        } else {
            // if user already had a token, delete it and create a new one
            Token token = tokenRepo.getByUserId(user.getId());
            if (token != null) {
                tokenRepo.delete(token);
            }
            createUserToken(user.getId(), 8, response);
            return modelMapper.map(user, UserDto.class);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
    }

    // check password
    private static boolean testPassword(String test, String salt, String hash) {
        // hash password
        String testDigest = DigestUtils.sha256Hex(salt + test);

        // verify that they are the same
        return testDigest.equals(hash);
    }

    // merge the existing user and the updated user
    private User mergeUser(User updatedUser, User existingUser) {
        User mergedUser = modelMapper.map(existingUser, User.class);
        try {
            // loop through all fields in the updated user
            for (Field f : updatedUser.getClass().getDeclaredFields()) {
                f.setAccessible(true);

                if ("username".equals(f.getName()) && f.get(updatedUser) != null) {
                    // username is to be updated, validate it first
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


    /*
     * Validation helper methods
     */

    private boolean isRequestValid(User user) {
        return isUsernameValid(user.getUsername())
                && isEmailValid(user.getEmail())
                && isPasswordValid(user.getPasswordHash());
    }

    private boolean isUsernameValid(@NotNull String username) {
        return username.matches("[a-zA-Z0-9_]+")
                && username.length() >= 4
                && username.length() <= 25
                && !userRepo.existsByUsernameIgnoreCase(username);
    }

    private boolean isEmailValid(@NotNull String email) {
        return email.matches(EMAIL_REGEX) && !userRepo.existsByEmailIgnoreCase(email);
    }

    private boolean isPasswordValid(@NotNull String password) {
        return password.length() >= 8 && password.matches("[a-zA-Z0-9!@]+");
    }

}
