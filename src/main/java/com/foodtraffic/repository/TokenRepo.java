package com.foodtraffic.repository;

import com.foodtraffic.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {
    Token findByTokenCode(String tokenCode);
}
