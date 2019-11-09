package com.foodtraffic.user.repository;

import com.foodtraffic.user.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {
    Token findByTokenCode(String tokenCode);
    Token getByUserId(Long id);
}
