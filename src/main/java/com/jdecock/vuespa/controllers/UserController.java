package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.dtos.AuthRequestDTO;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome this endpoint is not secure";
	}

	@PostMapping("/addNewUser")
	public String addNewUser(@RequestBody User userInfo) {
		var user = userService.addUser(userInfo);
		return user == null ? "Failed to save user" : "Successfully added user";
	}

	@GetMapping("/user/userProfile")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String userProfile() {
		return "Welcome to User Profile";
	}

	@GetMapping("/admin/adminProfile")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String adminProfile() {
		return "Welcome to Admin Profile";
	}

	@PostMapping("/generateToken")
	public String authenticateAndGetToken(@RequestBody AuthRequestDTO authRequest) {
		var token = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
		var authentication = authenticationManager.authenticate(token);

		if (authentication.isAuthenticated())
			return jwtService.generateToken(authRequest.getEmail());

		throw new UsernameNotFoundException("Invalid user request");
	}
}
