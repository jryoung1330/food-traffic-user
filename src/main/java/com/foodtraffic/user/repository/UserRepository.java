package com.foodtraffic.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodtraffic.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findUserById(Long id);

	boolean existsByEmailIgnoreCase(String email);

    Optional<User> findUserByEmail(String email);

    boolean existsByEmailIgnoreCaseAndId(String email, long userId);
}
