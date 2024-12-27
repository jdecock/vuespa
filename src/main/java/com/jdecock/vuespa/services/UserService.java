package com.jdecock.vuespa.services;

import com.jdecock.vuespa.dtos.UserAuthenticationDTO;
import com.jdecock.vuespa.dtos.UserDTO;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.entities.UserRoleType;
import com.jdecock.vuespa.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	// Required to implement UserDetailsService. For use in the JWT service.
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByEmail(username);
		return user
			.map(UserAuthenticationDTO::new)
			.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}

	public User createUser(UserDTO userDTO) {
		if (userDTO == null)
			return null;

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// Since we're creating a new user, we can ignore any incoming values for id, creation date, etc.
		User user = new User();
		user.setName(userDTO.getName());
		user.setEmail(userDTO.getEmail());
		user.setPassword(encoder.encode(userDTO.getPlainTextPassword()));

		List<UserRoleType> roles = userDTO.getRoles();
		if (roles == null || roles.isEmpty())
			roles = List.of(UserRoleType.ROLE_USER);
		user.setRoles(roles.stream().map(Enum::toString).collect(Collectors.joining(",")));

		return userRepository.save(user);
	}
}
