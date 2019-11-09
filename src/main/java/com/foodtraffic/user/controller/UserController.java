package com.foodtraffic.user.controller;

import com.foodtraffic.user.model.dto.UserDto;
import com.foodtraffic.user.model.entity.User;
import com.foodtraffic.user.service.UserServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@Api(tags = "User", description = " ")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @ApiIgnore
    @GetMapping("/check-user")
    public boolean checkForUser(@RequestParam String username){
        return userService.isUsernameClaimed(username);
    }

    @PostMapping("/login")
    public UserDto loginUser(@RequestHeader HttpHeaders headers, HttpServletResponse response, @CookieValue(value = "LOCO-USER", defaultValue="LOCO-USER") String accessToken) {
        return userService.loginUser(headers, accessToken, response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

}
