package com.jdecock.vuespa.services;

import com.jdecock.vuespa.dtos.UserAuthenticationDTO;
import com.jdecock.vuespa.dtos.UserDTO;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	// Required for the JWT service
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = userRepository.findByEmail(username);
		return user.map(UserAuthenticationDTO::new).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}

	public User createUser(UserDTO userDTO) {
		var encoder = new BCryptPasswordEncoder();

		// Since we're creating a new user, we can ignore any incoming values for id, creation date, etc.
		var user = new User();
		user.setName(userDTO.getName());
		user.setEmail(userDTO.getEmail());
		user.setPassword(encoder.encode(userDTO.getPlainTextPassword()));
		user.setRoles(userDTO.getRoles().stream().map(Enum::toString).collect(Collectors.joining(",")));

		return userRepository.save(user);
	}
}
