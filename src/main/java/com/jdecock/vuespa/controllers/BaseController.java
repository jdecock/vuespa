package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class BaseController {
	protected final UserRepository userRepository;

	BaseController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	protected User getCurrentUser() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext == null ? null : securityContext.getAuthentication();
		UserDetails principal = authentication == null ? null : (UserDetails) authentication.getPrincipal();

		return principal == null ? null : userRepository.findByEmail(principal.getUsername()).orElse(null);
	}
}
