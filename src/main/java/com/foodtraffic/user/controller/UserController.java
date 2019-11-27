package com.foodtraffic.user.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.user.entity.User;
import com.foodtraffic.user.service.UserService;

import io.swagger.annotations.Api;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/users")
@Api(tags = "User")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/check-user")
    public boolean checkUserExists(@RequestParam(required = false) String username, @RequestParam(required = false) Long id){
        return userService.userExists(username, id);
    }

    @GetMapping("/token")
    public UserDto checkAccessHeader(@RequestHeader(value = "Cookie") String accessToken) {
        return userService.checkToken(accessToken);
    }

    @PostMapping("/token")
    public UserDto checkAccess(@CookieValue(value = "_gid", defaultValue="") String accessToken) {
        return userService.checkToken(accessToken);
    }

    @PostMapping("/login")
    public UserDto loginUser(@RequestHeader HttpHeaders headers, HttpServletResponse response, @CookieValue(value = "_gid", defaultValue="") String accessToken) {
        return userService.loginUser(headers, accessToken, response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody User user, HttpServletResponse response) {
        return userService.createUser(user, response);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }
}
