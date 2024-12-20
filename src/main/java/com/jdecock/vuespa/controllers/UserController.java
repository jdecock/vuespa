package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.entities.AuthRequest;
import com.jdecock.vuespa.entities.UserInfo;
import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserInfoService;
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
	private UserInfoService userInfoService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome this endpoint is not secure";
	}

	@PostMapping("/addNewUser")
	public String addNewUser(@RequestBody UserInfo userInfo) {
		return userInfoService.addUser(userInfo);
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
	public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
		var token = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
		var authentication = authenticationManager.authenticate(token);

		if (authentication.isAuthenticated())
			return jwtService.generateToken(authRequest.getUsername());

		throw new UsernameNotFoundException("Invalid user request");
	}
}
