package com.jdecock.vuespa.repositories;

import com.jdecock.vuespa.entities.RefreshToken;
import com.jdecock.vuespa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	List<RefreshToken> findByUser(User user);
}
