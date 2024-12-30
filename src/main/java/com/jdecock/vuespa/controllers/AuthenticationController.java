package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.dtos.*;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.UserRepository;
import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
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
	public StatusInfoDataDTO<UserDTO> signUp(@RequestBody UserDTO userDTO) {
		User user = userRepository.findByEmail(userDTO.getEmail()).orElse(null);
		if (user != null)
			return new StatusInfoDataDTO<>(false, "User already exists");

		user = userService.createUser(userDTO);
		return user == null
			? new StatusInfoDataDTO<>(false, "User creation failed")
			: new StatusInfoDataDTO<>(true, "User created successfully", new UserDTO(user));
	}

	@PostMapping("/login")
	public ResponseEntity<Void> login(HttpServletResponse response, @RequestBody AuthRequestDTO authRequestDTO) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(),
			authRequestDTO.getPassword());
		Authentication authentication = authenticationManager.authenticate(token);

		if (!authentication.isAuthenticated())
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		jwtService.generateToken(response, authRequestDTO.getEmail());
		jwtService.createRefreshToken(response, authRequestDTO.getEmail(), authRequestDTO.isPersistLogin());

		return ResponseEntity.ok().build();
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		jwtService.removeAuthenticationTokens(request, response);
		return ResponseEntity.ok().build();
	}
}
