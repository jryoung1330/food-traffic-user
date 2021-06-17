package com.foodtraffic.user.service;

import com.foodtraffic.model.dto.UserDto;
import com.foodtraffic.user.entity.User;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;

public interface UserService {

	String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*"
			+ "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\"
			+ "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+"
			+ "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)"
			+ "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:"
			+ "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\"
			+ "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

	UserDto getUserById(long id);

	boolean userExists(String username);

	UserDto createUser(String authHeader, User user, HttpServletResponse response);

	UserDto loginUser(String authHeader, HttpServletResponse response);

	UserDto logoutUser(String accessToken, HttpServletResponse response);

	UserDto checkToken(String token);

	UserDto updateUser(long id, User user, String verificationCode);
	
	UserDto toggleFavorite(long userId, long vendorId);

	boolean isVendorFavorite(Long id, Long vendorId);
}