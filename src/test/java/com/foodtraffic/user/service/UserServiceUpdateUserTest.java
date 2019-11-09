package com.foodtraffic.user.service;

import com.foodtraffic.user.model.dto.UserDto;
import com.foodtraffic.user.model.entity.User;
import com.foodtraffic.user.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.Optional;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceUpdateUserTest {

    @Mock
    UserRepo userRepo;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserServiceImpl userService;

    User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        user = new User();
        user.setUsername(null);
        user.setEmail(null);
        user.setPasswordHash(null);
        when(userRepo.findUserById(anyLong())).thenReturn(Optional.of(mockUser()));
        when(modelMapper.map(mockUser(), User.class)).thenReturn(mockUser());
    }

    @Test
    public void givenNullUsernameEmailAndPassword_whenUpdateUser_userIsNotChanged() {
        when(userRepo.saveAndFlush(anyObject())).thenReturn(mockUser());
        when(modelMapper.map(mockUser(), UserDto.class)).thenReturn(mockUserDto());

        assertEquals(userService.updateUser(0L, user), mockUserDto());
    }

    @Test
    public void givenValidUsername_whenUpdateUser_usernameIsUpdated() {
        when(userRepo.existsByUsernameIgnoreCase("test1")).thenReturn(false);
        user.setUsername("test1");

        User mockUser = mockUser();
        mockUser.setUsername("test1");
        when(userRepo.saveAndFlush(mockUser)).thenReturn(mockUser);

        UserDto mockUserDto = mockUserDto();
        mockUserDto.setUsername("test1");
        when(modelMapper.map(mockUser, UserDto.class)).thenReturn(mockUserDto);

        assertEquals("test1", userService.updateUser(0L, user).getUsername());
    }

    @Test
    public void givenInvalidUsername_whenUpdateUser_throwsException() {
        user.setUsername("test1*");
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(0L, user));
    }

    @Test
    public void givenValidEmail_whenUpdateUser_emailIsUpdated() {
        when(userRepo.existsByEmailIgnoreCase("test1@test.com")).thenReturn(false);
        user.setEmail("test1@test.com");

        User mockUser = mockUser();
        mockUser.setEmail("test1@test.com");
        when(userRepo.saveAndFlush(mockUser)).thenReturn(mockUser);

        UserDto mockUserDto = mockUserDto();
        mockUserDto.setEmail("test1@test.com");
        when(modelMapper.map(mockUser, UserDto.class)).thenReturn(mockUserDto);

        assertEquals("test1@test.com", userService.updateUser(0L, user).getEmail());
    }

    @Test
    public void givenInvalidEmail_whenUpdateUser_throwsException() {
        user.setEmail("test1@testcom");
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(0L, user));
    }

    @Test
    public void givenValidPassword_whenUpdateUser_passwordIsUpdated() {
        user.setPasswordHash("cGFzc3dvcmQxMjM="); // = password123

        User mockUser = mockUser();
        mockUser.setPasswordHash("12345678901122335ad08540786eed509ccbfadda3f3e898f222000f4578048e");
        when(userRepo.saveAndFlush(mockUser)).thenReturn(mockUser);
        when(modelMapper.map(mockUser, UserDto.class)).thenReturn(mockUserDto());

        assertNull(userService.updateUser(0L, user));
    }

    @Test
    public void givenInvalidPassword_whenUpdateUser_throwsException() {
        user.setPasswordHash("cGFzc3dvcmQxMjMq"); // = password123*
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(0L, user));
    }

    @Test
    public void givenUserNotFound_whenUpdateUser_returnNull() {
        when(userRepo.findUserById(anyLong())).thenReturn(Optional.empty());
        assertNull(userService.updateUser(100000L, user));
    }

    private User mockUser() {
        User mockUser = new User();
        mockUser.setId(0);
        mockUser.setUsername("test");
        mockUser.setEmail("test@test.com");
        mockUser.setPasswordHash("742d2d94a64b9e155ad08540786eed509ccbfadda3f3e898f222000f4578048e");
        mockUser.setPasswordSalt("01234567abcdefgh");
        mockUser.setStatus("ACTIVE");
        mockUser.setJoinDate(ZonedDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.of(12, 1), ZoneId.of("UTC")));
        mockUser.setIsEmailVerified(false);
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
