package com.foodtraffic.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodtraffic.user.entity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

	Optional<Token> findByTokenCode(String tokenCode);
	Token getByUserId(Long id);
	boolean existsByTokenCode(String tokenCode);
}
