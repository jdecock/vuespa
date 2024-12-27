package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.dtos.AuthRequestDTO;
import com.jdecock.vuespa.dtos.StatusInfoDTO;
import com.jdecock.vuespa.dtos.StatusInfoDataDTO;
import com.jdecock.vuespa.dtos.UserDTO;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.UserRepository;
import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping("/generate-token")
	public StatusInfoDTO authenticateAndGetToken(@RequestBody AuthRequestDTO authRequest) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
			authRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(token);

		return authentication.isAuthenticated()
			? new StatusInfoDTO(true, jwtService.generateToken(authRequest.getEmail()))
			: new StatusInfoDTO(false, "Invalid email address or password");
	}
}
