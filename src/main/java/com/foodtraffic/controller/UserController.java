package com.foodtraffic.controller;

import com.foodtraffic.entity.User;
import com.foodtraffic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/check-user")
    public boolean checkForUser(@RequestParam String username){
        return userService.isUsernameClaimed(username);
    }

    @PostMapping("/login")
    public User loginUser(@RequestBody User userToAuth, HttpServletResponse response, @CookieValue(value = "LOCO-USER", defaultValue="LOCO-USER") String token) {
        User user = userService.loginUser(userToAuth);
        addCookie(response, user);
        user.setPasswordHash(null);
        return user;
    }

    @PostMapping("")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    private void addCookie(HttpServletResponse response, User user) {
        Cookie cookie = new Cookie("LOCO-USER", user.getPasswordHash());
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}