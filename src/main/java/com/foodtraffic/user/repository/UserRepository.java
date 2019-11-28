package com.foodtraffic.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodtraffic.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findUserById(Long id);

	User getUserByUsernameIgnoreCase(String username);

	User getUserByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

	boolean existsByUsernameIgnoreCase(String username);

	boolean existsByEmailIgnoreCase(String email);
}
