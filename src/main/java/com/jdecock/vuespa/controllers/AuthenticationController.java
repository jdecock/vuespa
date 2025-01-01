package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.dtos.*;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.UserRepository;
import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController extends BaseController {
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserService userService;

	public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService,
	                                UserRepository userRepository, UserService userService) {
		super(userRepository);
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userService = userService;
	}

	@PostMapping("/sign-up")
	public StatusInfoDataDTO<UserDTO> signUp(HttpServletResponse response, @RequestBody UserDTO userDTO) {
		User user = userRepository.findByEmail(userDTO.getEmail()).orElse(null);
		if (user != null)
			return new StatusInfoDataDTO<>(false, "User already exists");

		user = userService.createUser(userDTO);
		if (user == null)
			return new StatusInfoDataDTO<>(false, "User creation failed");

		AuthRequestDTO authRequestDTO = new AuthRequestDTO(user.getEmail(), userDTO.getPlainTextPassword(), false);
		return login(response, authRequestDTO);
	}

	@PostMapping("/login")
	public StatusInfoDataDTO<UserDTO> login(HttpServletResponse response, @RequestBody AuthRequestDTO authRequestDTO) {
		User user = userRepository.findByEmail(authRequestDTO.getEmail()).orElse(null);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(),
			authRequestDTO.getPassword());
		Authentication authentication = authenticationManager.authenticate(token);

		if (user == null || !authentication.isAuthenticated())
			return new StatusInfoDataDTO<>(false, "Username or password is incorrect");

		jwtService.generateToken(response, authRequestDTO.getEmail());
		jwtService.createRefreshToken(response, authRequestDTO.getEmail(), authRequestDTO.isPersistLogin());

		return new StatusInfoDataDTO<>(true, "User logged in", new UserDTO(user));
	}

	@PostMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		jwtService.removeAuthenticationTokens(request, response);
	}
}
