package com.foodtraffic.user.service;

import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.user.entity.Token;
import com.foodtraffic.user.entity.User;
import com.foodtraffic.user.repository.TokenRepository;
import com.foodtraffic.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
public class UserServiceLoginUserTest {

    @Mock
    ModelMapper modelMapper;

    @Mock
    UserRepository userRepo;

    @Mock
    TokenRepository tokenRepo;

    @InjectMocks
    UserServiceImpl userService;

    HttpHeaders headers = new HttpHeaders();

    HttpServletResponse response = new MockHttpServletResponse();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(userRepo.getUserByUsernameIgnoreCase("test")).thenReturn(mockUser());
        when(modelMapper.map(mockUser(), UserDto.class)).thenReturn(mockUserDto());
        when(tokenRepo.findByTokenCode("1a2b3c4d5e6f7g8h")).thenReturn(Optional.of(mockToken()));
        when(userRepo.getOne(0L)).thenReturn(mockUser());
        when(userRepo.getUserByUsernameIgnoreCaseOrEmailIgnoreCase("test", "test")).thenReturn(mockUser());
    }

    @Test
    public void givenValidCredentialsAndNoTokenInDatabase_whenUserLogin_createNewTokenAndReturnUser() {
        headers.set("Authorization", "Basic dGVzdDpwYXNzd29yZA==");
        assertEquals(userService.loginUser(headers, "", response), mockUserDto());
    }

    @Test
    public void givenValidCredentialsAndTokenInDatabase_whenUserLogin_updateTokenAndReturnUser() {
        when(tokenRepo.getByUserId(0L)).thenReturn(mockToken());
        headers.set("Authorization", "Basic dGVzdDpwYXNzd29yZA==");
        assertEquals(userService.loginUser(headers, "", response), mockUserDto());
    }

    @Test
    public void givenInvalidPassword_whenUserLogin_throwException() {
        headers.set("Authorization", "Basic dGVzdDpwYXNzd29yZDEyMw==");
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(headers, "", response));
    }

    @Test
    public void givenUserDoesNotExist_whenUserLogin_throwException() {
        when(userRepo.getUserByUsernameIgnoreCaseOrEmailIgnoreCase("test", "test")).thenReturn(null);
        headers.set("Authorization", "Basic dGVzdDpwYXNzd29yZA==");

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(headers, "", response));
    }

    @Test
    public void givenValidAccessToken_whenUserLogin_returnUser() {
        assertEquals(userService.loginUser(headers, "1a2b3c4d5e6f7g8h", response), mockUserDto());
    }

    @Test
    public void givenInvalidAccessToken_whenUserLogin_throwException() {
        when(tokenRepo.findByTokenCode("1a2b3c4d5e6f7g8h")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(headers, "1a2b3c4d5e6f7g8h", response));
    }

    @Test
    public void givenValidCredentialsButUserIsInactive_whenUserLogin_throwException() {
        headers.set("Authorization", "Basic dGVzdDpwYXNzd29yZA==");
        User mockUser = mockUser();
        mockUser.setStatus(2);
        mockUser.setEmailVerified(false);
        when(userRepo.getUserByUsernameIgnoreCaseOrEmailIgnoreCase("test", "test")).thenReturn(mockUser);

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(headers, "", response));
    }

    @Test
    public void givenValidAccessTokenButUserIsInactive_whenUserLogin_throwException() {
        User mockUser = mockUser();
        mockUser.setStatus(2);
        mockUser.setEmailVerified(false);
        when(userRepo.getOne(0L)).thenReturn(mockUser);

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(headers, "1a2b3c4d5e6f7g8h", response));
    }

    @Test
    public void givenNoAuthHeaderAndNoAccessToken_whenUserLogin_throwException() {
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(headers, "", response));
    }

    private User mockUser() {
        User mockUser = new User();
        mockUser.setId(0L);
        mockUser.setUsername("test");
        mockUser.setEmail("test@test.com");
        mockUser.setPasswordHash("835b456635e1ac11d3ee651b4c7d88276b730e79d76d9ccd7fa9637faf87791e");
        mockUser.setPasswordSalt("1e1845243ce6bb70");
        mockUser.setStatus(0);
        mockUser.setJoinDate(ZonedDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.of(12, 1), ZoneId.of("UTC")));
        mockUser.setEmailVerified(true);
        return mockUser;
    }

    private UserDto mockUserDto() {
        UserDto mockUser = new UserDto();
        mockUser.setUsername("test");
        mockUser.setEmail("test@test.com");
        mockUser.setFavorites(null);
        return mockUser;
    }

    private Token mockToken() {
        Token token = new Token();
        token.setId(0);
        token.setTokenCode("1a2b3c4d5e6f7g8h");
        token.setUserId(0L);
        return token;
    }

}
