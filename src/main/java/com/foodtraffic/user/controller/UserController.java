package com.foodtraffic.user.controller;

import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.user.entity.User;
import com.foodtraffic.user.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"}, allowCredentials="true")
@RestController
@RequestMapping("/users")
@Api(tags = "User")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken,
                               @PathVariable Long id) {
        return userService.getUserById(accessToken, id);
    }

    @GetMapping("/token")
    public UserDto checkAccessHeader(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return userService.checkToken(accessToken);
    }

    @GetMapping("/{id}/favorites/{vendorId}")
    public boolean isVendorFavorite(@PathVariable Long id, @PathVariable Long vendorId) {
        return userService.isVendorFavorite(id, vendorId);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken,
                              @RequestBody User user) {
        return userService.createUser(accessToken, user);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                              @PathVariable Long id,
                              @RequestBody User user) {
        return userService.updateUser(accessToken, id, user);
    }
    
    @PutMapping("/{id}/favorites/{vendorId}")
    public UserDto toggleFavorite(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                  @PathVariable Long id,
                                  @PathVariable Long vendorId) {
    	return userService.toggleFavorite(accessToken, id, vendorId);
    }
}
