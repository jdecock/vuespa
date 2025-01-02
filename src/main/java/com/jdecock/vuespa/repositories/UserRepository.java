package com.jdecock.vuespa.repositories;

import com.jdecock.vuespa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	List<User> findByNameContainingIgnoreCaseOrEmailIsContainingIgnoreCase(String name, String email);
}
