package com.jdecock.vuespa.dtos;

import com.jdecock.vuespa.entities.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class UserAuthenticationDTO implements UserDetails {
	private final String email;

	private final String password;

	private final List<GrantedAuthority> authorities;

	public UserAuthenticationDTO(User user) {
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.authorities = Stream.of(user.getRoles().split(","))
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	@Override
	// Required for the JWT service
	public String getUsername() {
		return email;
	}
}
