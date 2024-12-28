package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.dtos.*;
import com.jdecock.vuespa.entities.RefreshToken;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.RefreshTokenRepository;
import com.jdecock.vuespa.repositories.UserRepository;
import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserService;
import com.jdecock.vuespa.utils.StringUtils;
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
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserService userService;

	public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwtService,
	                                UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
	                                UserService userService) {
		super(userRepository);
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.refreshTokenRepository = refreshTokenRepository;
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
	public StatusInfoDataDTO<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequest) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
			authRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(token);

		if (!authentication.isAuthenticated())
			return new StatusInfoDataDTO<>(false, "Invalid email address or password");

		RefreshToken refreshToken = jwtService.createRefreshToken(authRequest.getEmail());
		String accessToken = jwtService.generateToken(authRequest.getEmail());

		return new StatusInfoDataDTO<>(true, "Successful authentication",
			new AuthResponseDTO(accessToken, refreshToken.getToken()));
	}

	@PostMapping("/refresh-token")
	public StatusInfoDataDTO<AuthResponseDTO> refreshToken(@RequestBody AuthRefreshTokenDTO refreshTokenDTO) {
		if (refreshTokenDTO == null || StringUtils.isEmpty(refreshTokenDTO.getRefreshToken()))
			return new StatusInfoDataDTO<>(false, "Invalid refresh token");

		RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenDTO.getRefreshToken()).orElse(null);
		refreshToken = jwtService.verifyRefreshToken(refreshToken);
		User user = refreshToken == null ? null : refreshToken.getUser();

		if (refreshToken == null || user == null)
			return new StatusInfoDataDTO<>(false, "Invalid refresh token");

		// The refresh token should only be used once.
		refreshTokenRepository.delete(refreshToken);

		// Issue new access token and refresh token
		RefreshToken newRefreshToken = jwtService.createRefreshToken(user.getEmail());
		String accessToken = jwtService.generateToken(user.getEmail());

		return new StatusInfoDataDTO<>(true, "Used refresh token to generate new access token",
			new AuthResponseDTO(accessToken, newRefreshToken.getToken()));
	}
}
