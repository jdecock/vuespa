package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.dtos.*;
import com.jdecock.vuespa.entities.RefreshToken;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.RefreshTokenRepository;
import com.jdecock.vuespa.repositories.UserRepository;
import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserService;
import com.jdecock.vuespa.utils.SecurityCipher;
import com.jdecock.vuespa.utils.StringUtils;
import org.springframework.http.*;
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
	public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequest) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
			authRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(token);

		if (!authentication.isAuthenticated())
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		// Create an encrypted HTTP cookie to hold onto the access token.
		HttpHeaders responseHeaders = new HttpHeaders();
		setAccessTokenCookie(responseHeaders, jwtService.generateToken(authRequest.getEmail()));

		RefreshToken refreshToken = jwtService.createRefreshToken(authRequest.getEmail());
		return ResponseEntity.ok().headers(responseHeaders).body(new AuthResponseDTO(refreshToken.getToken()));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody AuthRefreshTokenDTO refreshTokenDTO) {
		if (refreshTokenDTO == null || StringUtils.isEmpty(refreshTokenDTO.getRefreshToken()))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenDTO.getRefreshToken()).orElse(null);
		refreshToken = jwtService.verifyRefreshToken(refreshToken);
		User user = refreshToken == null ? null : refreshToken.getUser();

		if (refreshToken == null || user == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		// The refresh token should only be used once.
		refreshTokenRepository.delete(refreshToken);

		// Issue new access token and refresh token. Create an encrypted HTTP cookie to hold onto the access token.
		HttpHeaders responseHeaders = new HttpHeaders();
		setAccessTokenCookie(responseHeaders, jwtService.generateToken(user.getEmail()));

		RefreshToken newRefreshToken = jwtService.createRefreshToken(user.getEmail());
		return ResponseEntity.ok().headers(responseHeaders).body(new AuthResponseDTO(newRefreshToken.getToken()));
	}

	private void setAccessTokenCookie(HttpHeaders httpHeaders, String accessToken) {
		String encryptedToken = SecurityCipher.encrypt(accessToken);
		if (StringUtils.isEmpty(encryptedToken))
			return;

		HttpCookie cookie = ResponseCookie.from(JwtService.ACCESS_TOKEN_COOKIE_NAME, encryptedToken)
			.maxAge(JwtService.ACCESS_TOKEN_LIFETIME / 1000)
			.httpOnly(true)
			.path("/")
			.build();
		httpHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());
	}
}
