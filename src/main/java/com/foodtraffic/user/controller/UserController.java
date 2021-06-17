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

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"}, allowCredentials="true")
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
    public boolean checkUserExists(@RequestParam(name="username") String username) {
        return userService.userExists(username);
    }

    @GetMapping("/token")
    public UserDto checkAccessHeader(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return userService.checkToken(accessToken);
    }

    @GetMapping("/{id}/favorites/{vendorId}")
    public boolean isVendorFavorite(@PathVariable Long id, @PathVariable Long vendorId) {
        return userService.isVendorFavorite(id, vendorId);
    }
    
    @PostMapping("/token")
    public UserDto checkAccess(@CookieValue(value = "_gid", defaultValue="") String accessToken) {
        return userService.checkToken(accessToken);
    }

    @PostMapping("/login")
    public UserDto loginUser(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authHeader,
                             HttpServletResponse response) {
        return userService.loginUser(authHeader, response);
    }

    @PostMapping("/{id}/logout")
    public UserDto checkAccess(@CookieValue(value = "_gid", defaultValue="") String accessToken,
                               HttpServletResponse response) {
        return userService.logoutUser(accessToken, response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authHeader,
                              @RequestBody User user,
                              HttpServletResponse response) {
        return userService.createUser(authHeader, user, response);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @RequestBody User user,
                              @RequestParam(name = "verify") String verificationCode) {
        return userService.updateUser(id, user, verificationCode);
    }
    
    @PutMapping("/{id}/favorites/{vendorId}")
    public UserDto toggleFavorite(@PathVariable Long id, @PathVariable Long vendorId) {
    	return userService.toggleFavorite(id, vendorId);
    }
}
