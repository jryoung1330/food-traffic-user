package com.foodtraffic.user.service;

import com.foodtraffic.user.model.dto.UserDto;
import com.foodtraffic.user.model.entity.User;
import com.foodtraffic.user.repository.TokenRepo;
import com.foodtraffic.user.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.time.*;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyObject;

@SpringBootTest
public class UserServiceCreateUserTest {

    @Mock
    UserRepo userRepo;

    @Mock
    TokenRepo tokenRepo;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserServiceImpl userService;

    User user;

    HttpServletResponse response = new MockHttpServletResponse();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setUsername("test");
        user.setEmail("test@test.com");
        user.setPasswordHash("cGFzc3dvcmQ=");
        when(userRepo.existsByUsernameIgnoreCase("test")).thenReturn(false);
        when(userRepo.existsByEmailIgnoreCase("test@test.com")).thenReturn(false);
        when(userRepo.saveAndFlush(anyObject())).thenReturn(mockUser());
        when(modelMapper.map(anyObject(), anyObject())).thenReturn(mockUserDto());
    }

    /**
     * User name validation tests
     */

    @Test
    public void givenNullUsername_whenCreateUser_throwExceptionWith400Status() {
        user.setUsername(null);
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    @Test
    public void givenUsernameWithInvalidCharacters_whenCreateUser_throwExceptionWith400Status() {
        user.setUsername("test**");
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    @Test
    public void givenUsernameLessThanLengthOf4_whenCreateUser_throwExceptionWith400Status() {
        user.setUsername("tst");
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    @Test
    public void givenUsernameGreaterThanLengthOf25_whenCreateUser_throwExceptionWith400Status() {
        user.setUsername("thisusernameislongerthan25characters");
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    @Test
    public void givenUsernameAlreadyTaken_whenCreateUser_throwExceptionWith400Status() {
        when(userRepo.existsByUsernameIgnoreCase("test")).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    /**
     * Email validation tests
     */

    @Test
    public void givenNullEmail_whenCreateUser_throwExceptionWith400Status() {
        user.setEmail(null);
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    @Test
    public void givenInvalidEmail_whenCreateUser_throwExceptionWith400Status() {
        user.setEmail("testtest.com");
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    @Test
    public void givenEmailAlreadyTaken_whenCreateUser_throwExceptionWith400Status() {
        when(userRepo.existsByEmailIgnoreCase("test@test.com")).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    /**
     * Password validation tests
     */

    @Test
    public void givenNullPassword_whenCreateUser_throwExceptionWith400Status() {
        user.setPasswordHash(null);
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    @Test
    public void givenPasswordOfLengthLessThan8_whenCreateUser_throwExceptionWith400Status() {
        user.setPasswordHash("cGFzc");
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    @Test
    public void givenInvalidCharactersInPassword_whenCreateUser_throwExceptionWith400Status() {
        user.setPasswordHash("dGVzdGluZzEyMyY="); // decoded value = testing123&
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user, response));
    }

    /**
     * All parameters are valid
     */

    @Test
    public void givenValidParameters_whenCreateUser_returnNewUser() {
        assertEquals(userService.createUser(user, response), mockUserDto());
    }


    private User mockUser() {
        User mockUser = new User();
        mockUser.setId(0);
        mockUser.setUsername("test");
        mockUser.setEmail("test@test.com");
        mockUser.setPasswordHash("742d2d94a64b9e155ad08540786eed509ccbfadda3f3e898f222000f4578048e");
        mockUser.setPasswordSalt("01234567abcdefgh");
        mockUser.setStatus(0);
        mockUser.setJoinDate(ZonedDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.of(12, 1), ZoneId.of("UTC")));
        mockUser.setEmailVerified(false);
        return mockUser;
    }

    private UserDto mockUserDto() {
        UserDto mockUser = new UserDto();
        mockUser.setUsername("test");
        mockUser.setEmail("test@test.com");
        mockUser.setFavorites(null);
        return mockUser;
    }


}
