package com.lococator.repository;

import com.lococator.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {
    Token findByTokenCode(String tokenCode);
}
