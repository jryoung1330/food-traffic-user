package com.foodtraffic.user.service;

import com.foodtraffic.common.AuthService;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.model.response.AuthUser;
import com.foodtraffic.user.entity.Favorite;
import com.foodtraffic.user.entity.User;
import com.foodtraffic.user.entity.UserStatus;
import com.foodtraffic.user.repository.FavoriteRepository;
import com.foodtraffic.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepo;

    @Mock
    FavoriteRepository favoriteRepo;

    @Spy
    ModelMapper modelMapper;

    @Mock(lenient = true)
    AuthService authService;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        when(authService.getUserProfile("accessToken")).thenReturn(mockAuthUser());
    }

    @Test
    public void givenValidIdAndToken_whenGetUserById_thenReturnUser() {
        when(userRepo.findUserById(1L)).thenReturn(Optional.of(mockUser()));
        UserDto returnedUser = userService.getUserById("accessToken", 1L);
        assertEquals(mockUserDto(), returnedUser);
    }

    @Test
    public void givenInvalidId_whenGetUserById_thenThrowException() {
        when(userRepo.findUserById(1L)).thenReturn(Optional.empty());
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, () -> userService.getUserById("accessToken", 1L));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatus());
    }

    @Test
    public void givenTokenForDifferentUser_whenGetUserById_thenThrowException() {
        Optional<User> option = Optional.of(mockUser());
        option.get().setEmail("test1@test.com");
        when(userRepo.findUserById(1L)).thenReturn(option);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, () -> userService.getUserById("accessToken", 1L));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatus());
    }

    @Test
    public void givenUniqueEmail_whenCreateUser_thenReturnNewUser() {
        User user = mockUser();
        when(userRepo.existsByEmailIgnoreCase("test@test.com")).thenReturn(false);
        when(userRepo.saveAndFlush(any())).thenReturn(user);
        UserDto newUser = userService.createUser("accessToken", user);
        assertEquals(mockUserDto(), newUser);
    }

    @Test
    public void givenEmailExists_whenCreateUser_thenThrowException() {
        User user = mockUser();
        when(userRepo.existsByEmailIgnoreCase("test@test.com")).thenReturn(true);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, () -> userService.createUser("accessToken", user));
        assertEquals(HttpStatus.BAD_REQUEST, rse.getStatus());
    }

    @Test
    public void givenValidAccessToken_whenCheckToken_thenReturnUser() {
        when(userRepo.findUserByEmail("test@test.com")).thenReturn(Optional.of(mockUser()));
        UserDto returnedUser = userService.checkToken("accessToken");
        assertEquals(mockUserDto(), returnedUser);
    }

    @Test
    public void givenInvalidAccessToken_whenCheckToken_thenThrowException() {
        when(userRepo.findUserByEmail("test@test.com")).thenReturn(Optional.empty());
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, () -> userService.checkToken("accessToken"));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatus());
    }

    @Test
    public void givenValidRequest_whenUpdateUser_thenReturnUpdatedUser() {
        User updates = new User();
        updates.setId(1L);
        updates.setLastName("Changed");
        User user = mockUser();
        when(userRepo.findUserById(1L)).thenReturn(Optional.of(user));
        User mergedUser = mockUser();
        mergedUser.setLastName("Changed");
        when(userRepo.save(any())).thenReturn(mergedUser);
        UserDto updatedUser = userService.updateUser("accessToken", 1L, updates);
        assertEquals("Changed", updatedUser.getLastName());
    }

    @Test
    public void givenTokenForDifferentUser_whenUpdateUser_thenThrowException() {
        User updates = new User();
        updates.setId(1L);
        updates.setLastName("Changed");
        User existingUser = mockUser();
        existingUser.setEmail("test1@test.com");
        when(userRepo.findUserById(1L)).thenReturn(Optional.of(existingUser));
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, () -> userService.updateUser("accessToken", 1L, updates));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatus());
    }

    @Test
    public void givenInvalidRequest_whenUpdateUser_thenThrowException() {
        User updates = new User();
        updates.setId(1L);
        updates.setEmail("test1@test.com");
        when(userRepo.findUserById(1L)).thenReturn(Optional.empty());
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, () -> userService.updateUser("accessToken", 1L, updates));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatus());
    }

    @Test
    public void givenFav_whenToggleFavorite_thenFavIsDeleted() {
        when(userRepo.existsByEmailIgnoreCaseAndId("test@test.com", 1L)).thenReturn(true);
        when(userRepo.getById(1L)).thenReturn(mockUser());
        when(favoriteRepo.findByUserIdAndVendorId(1L, 100L)).thenReturn(Optional.of(mockFav()));
        userService.toggleFavorite("accessToken", 1L, 100L);
        verify(favoriteRepo, times(1)).delete(any());
    }

    @Test
    public void givenNotFav_whenToggleFavorite_thenFavIsCreated() {
        when(userRepo.existsByEmailIgnoreCaseAndId("test@test.com", 1L)).thenReturn(true);
        when(userRepo.getById(1L)).thenReturn(mockUser());
        when(favoriteRepo.findByUserIdAndVendorId(1L, 100L)).thenReturn(Optional.empty());
        userService.toggleFavorite("accessToken", 1L, 100L);
        verify(favoriteRepo, times(1)).saveAndFlush(any());
    }

    @Test
    public void givenTokenForDifferentUser_whenToggleFavorite_thenThrowException() {
        when(userRepo.existsByEmailIgnoreCaseAndId("test@test.com", 1L)).thenReturn(false);
        ResponseStatusException rse =
                assertThrows(ResponseStatusException.class, () ->  userService.toggleFavorite("accessToken", 1L, 100L));
        assertEquals(HttpStatus.NOT_FOUND, rse.getStatus());
    }

    @Test
    public void givenVendorIsFav_whenIsVendorFavorite_thenReturnTrue() {
        when(favoriteRepo.findByUserIdAndVendorId(1L, 100L)).thenReturn(Optional.of(mockFav()));
        assertTrue(userService.isVendorFavorite(1L, 100L));
    }

    @Test
    public void givenVendorIsNotFav_whenIsVendorFavorite_thenReturnFalse() {
        when(favoriteRepo.findByUserIdAndVendorId(1L, 100L)).thenReturn(Optional.empty());
        assertFalse(userService.isVendorFavorite(1L, 100L));
    }

    private User mockUser() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("Tony");
        mockUser.setLastName("Stark");
        mockUser.setUsername("test");
        mockUser.setEmail("test@test.com");
        mockUser.setPasswordHash("742d2d94a64b9e155ad08540786eed509ccbfadda3f3e898f222000f4578048e");
        mockUser.setPasswordSalt("01234567abcdefgh");
        mockUser.setStatus(UserStatus.ACTIVE.name());
        mockUser.setJoinDate(ZonedDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.of(12, 1), ZoneId.of("UTC")));
        mockUser.setEmailVerified(false);
        return mockUser;
    }

    private UserDto mockUserDto() {
        UserDto mockUser = new UserDto();
        mockUser.setId(1L);
        mockUser.setFirstName("Tony");
        mockUser.setLastName("Stark");
        mockUser.setUsername("test");
        mockUser.setEmail("test@test.com");
        mockUser.setFavorites(null);
        return mockUser;
    }

    private AuthUser mockAuthUser() {
        AuthUser authUser = new AuthUser();
        authUser.setName("Test");
        authUser.setEmail("test@test.com");
        return authUser;
    }

    private Favorite mockFav() {
        Favorite favorite = new Favorite();
        favorite.setId(1L);
        favorite.setUserId(1L);
        favorite.setVendorId(100L);
        return favorite;
    }
}
