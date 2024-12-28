package com.jdecock.vuespa.repositories;

import com.jdecock.vuespa.entities.RefreshToken;
import com.jdecock.vuespa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);
	List<RefreshToken> findByUser(User user);
}