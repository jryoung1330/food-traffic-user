package com.foodtraffic.repository;

import com.foodtraffic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
