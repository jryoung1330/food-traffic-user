package com.lococator.service;

import com.lococator.entity.Token;
import com.lococator.entity.User;
import com.lococator.repository.TokenRepo;
import com.lococator.repository.UserRepo;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Date;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    UserRepo userRepo;
    TokenRepo tokenRepo;

    @Autowired
    public UserService(UserRepo userRepo, TokenRepo tokenRepo) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
    }

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    public User getUserById(Long id) {
        return userRepo.getOne(id);
    }

    public User getUserByUsername(String username) {
        return userRepo.findUserByUsername(username);
    }

    public boolean isUsernameClaimed(String username) {
        return userRepo.existsByUsername(username);
    }

    public User createUser(User user) {

        if(user != null
                && user.getUsername().matches("[a-zA-Z0-9_]+")
                && user.getUsername().length() >= 4 && user.getUsername().length() <= 25
                && !userRepo.existsByEmail(user.getEmail())
                && user.getPasswordHash().length() >= 8
                /*&& user.getPasswordHash().matches("[a-zA-Z0-9!@]+")*/) {
            stringDigest(user);
            user.setEmail(user.getEmail().toLowerCase());
            user.setUsername(user.getUsername());
            user.setJoinDate(new Date(System.currentTimeMillis()));
//            System.out.println(user);
            userRepo.saveAndFlush(user);

//            Token token = new Token();
//            token.setTokenCode(generateRandomToken());
//            token.setUserId(user.getId());
//            tokenRepo.save(token);
//            user.setPasswordHash(token.getTokenCode());
//            user.setPasswordSalt(null);
        }

        return user;
    }

    public User updateUser(User user) {
        return userRepo.saveAndFlush(user);
    }

    /*
     * Helper methods
     */

    // helper method for salting and hashing a password
    private static void stringDigest(User user) {
        byte[] decodedBytes = Base64.getDecoder().decode(user.getPasswordHash());
        String decodedString = new String(decodedBytes);
        user.setPasswordHash(decodedString);

        // generate a random 64-bit salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        String saltStr = Hex.encodeHexString(salt);
        user.setPasswordSalt(saltStr); // store that salt in the returning array

        // prepend salt to the password and hash it
        String digest = DigestUtils.sha256Hex(saltStr + user.getPasswordHash()); // returns a byte-encoded string of length
        // 64
        user.setPasswordHash(digest); // store the hash
    }

    // helper method to check a password(data) against a salt and hash
    private static boolean verifyPassword(String data, String salt, String hash) {
        // prepend salt to password and hash
        String testDigest = DigestUtils.sha256Hex(salt + data);

        // verify that they are the same
        return testDigest.equals(hash);
    }

    public User loginUser(User userToAuth) {
        String password = userToAuth.getPasswordHash();
        String username = userToAuth.getUsername();
        User user =  getUserByUsername(username);

        // decode password
        byte[] decodedBytes = Base64.getDecoder().decode(password);
        password = new String(decodedBytes);

        // verify and generate token
        if(user != null && verifyPassword(password, user.getPasswordSalt(), user.getPasswordHash())) {
            Token token = new Token();
            token.setTokenCode(generateRandomToken());
            token.setUserId(user.getId());
            tokenRepo.save(token);
            user.setPasswordHash(token.getTokenCode());
            // user.setPasswordSalt(null);
        }else {
            user = new User();
            user.setId(-1);
        }

        return user;
    }

    private static String generateRandomToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenValue = new byte[4];
        random.nextBytes(tokenValue);
        String token = Hex.encodeHexString(tokenValue);
        return token;
    }

    public User checkToken(String token) {
        Token tk = tokenRepo.findByTokenCode(token);
        if(tk == null) {
            User user = new User();
            return user;
        }
        else {
            Optional<User> userOpt = userRepo.findById(tk.getUserId());
            User user = userOpt.get();
            return user;
        }

    }
}
