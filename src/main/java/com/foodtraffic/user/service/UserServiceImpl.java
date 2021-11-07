package com.foodtraffic.user.service;

import com.foodtraffic.common.AuthService;
import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.user.entity.Favorite;
import com.foodtraffic.user.entity.User;
import com.foodtraffic.user.entity.UserStatus;
import com.foodtraffic.user.repository.FavoriteRepository;
import com.foodtraffic.user.repository.UserRepository;
import com.foodtraffic.util.AppUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private FavoriteRepository favoriteRepo;
    
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthService authService;

    @Override
    public UserDto getUserById(final String accessToken, final long id) {
        String email = authService.getUserProfile(accessToken).getEmail();
        Optional<User> user = userRepo.findUserById(id);
        if (user.isPresent() && user.get().getEmail().equals(email)) {
            return modelMapper.map(user.get(), UserDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
        }
    }

    @Override
    public UserDto createUser(final String accessToken, User user) {
        String email = authService.getUserProfile(accessToken).getEmail();
        user.setEmail(email);

        if (!userRepo.existsByEmailIgnoreCase(email)) {

            // create user
            user.setEmail(user.getEmail().toLowerCase());
            user.setUsername(user.getUsername().toLowerCase());
            user.setJoinDate(ZonedDateTime.now());
            user.setStatus(UserStatus.ACTIVE.name());
            user = userRepo.saveAndFlush(user);
            return modelMapper.map(user, UserDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");
        }
    }

    @Override
    public UserDto checkToken(final String accessToken) {
        String email = authService.getUserProfile(accessToken).getEmail();
        Optional<User> user = userRepo.findUserByEmail(email);
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource does not exist.");
        }
    }

    @Override
    public UserDto updateUser(final String accessToken, final long id, User request) {
        String email = authService.getUserProfile(accessToken).getEmail();
        Optional<User> existingUser = userRepo.findUserById(id);
        if (existingUser.isPresent() && existingUser.get().getEmail().equals(email)) {
            User user = (User) AppUtil.mergeObject(request, existingUser.get());
            user = userRepo.save(user);
            return modelMapper.map(user, UserDto.class);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource does not exist");
        }
    }
    
    @Override
    public UserDto toggleFavorite(final String accessToken, final long userId, final long vendorId) {
        String email = authService.getUserProfile(accessToken).getEmail();

        if(userRepo.existsByEmailIgnoreCaseAndId(email, userId)) {
            Optional<Favorite> favorite = favoriteRepo.findByUserIdAndVendorId(userId, vendorId);

            if(favorite.isPresent()) {
                favoriteRepo.delete(favorite.get());
            } else {
                Favorite fav = new Favorite();
                fav.setVendorId(vendorId);
                fav.setUserId(userId);
                favoriteRepo.saveAndFlush(fav);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Request");
        }

    	return getUserAsDto(userId);
    }

    @Override
    public boolean isVendorFavorite(Long id, Long vendorId) {
        Optional<Favorite> favorite = favoriteRepo.findByUserIdAndVendorId(id, vendorId);
        return favorite.isPresent();
    }

    private UserDto getUserAsDto(long id) {
        User user = userRepo.getById(id);
        return modelMapper.map(user, UserDto.class);
    }
}
