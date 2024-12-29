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
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<Void> login(@RequestBody AuthRequestDTO authRequest) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
			authRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(token);

		if (!authentication.isAuthenticated())
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		// Create an access token and refresh token. Store as encrypted HTTP cookies.
		HttpHeaders responseHeaders = new HttpHeaders();
		String accessToken = jwtService.generateToken(authRequest.getEmail());
		setEncryptedHttpCookie(responseHeaders, JwtService.ACCESS_TOKEN_COOKIE_NAME, accessToken, null);

		RefreshToken refreshToken = jwtService.createRefreshToken(authRequest.getEmail());
		setEncryptedHttpCookie(responseHeaders, JwtService.REFRESH_TOKEN_COOKIE_NAME, refreshToken.getToken(), null);

		return ResponseEntity.ok().headers(responseHeaders).build();
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<Void> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshTokenCookieValue) {
		if (StringUtils.isEmpty(refreshTokenCookieValue))
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		RefreshToken refreshToken = refreshTokenRepository
			.findByToken(SecurityCipher.decrypt(refreshTokenCookieValue))
			.orElse(null);
		refreshToken = jwtService.verifyRefreshToken(refreshToken);
		User user = refreshToken == null ? null : refreshToken.getUser();

		if (refreshToken == null || user == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		// The refresh token should only be used once.
		refreshTokenRepository.delete(refreshToken);

		// Issue new access token and refresh token. Store as encrypted HTTP cookies.
		HttpHeaders responseHeaders = new HttpHeaders();
		String accessToken = jwtService.generateToken(user.getEmail());
		setEncryptedHttpCookie(responseHeaders, JwtService.ACCESS_TOKEN_COOKIE_NAME, accessToken, null);

		RefreshToken newRefreshToken = jwtService.createRefreshToken(user.getEmail());
		setEncryptedHttpCookie(responseHeaders, JwtService.REFRESH_TOKEN_COOKIE_NAME, newRefreshToken.getToken(), null);

		return ResponseEntity.ok().headers(responseHeaders).build();
	}

	private void setEncryptedHttpCookie(HttpHeaders httpHeaders, String cookieName, String cookieValue, Long maxAgeSeconds) {
		String encryptedValue = SecurityCipher.encrypt(cookieValue);
		if (StringUtils.isEmpty(encryptedValue))
			return;

		HttpCookie httpCookie = ResponseCookie.from(cookieName, encryptedValue)
			.maxAge(maxAgeSeconds == null ? -1 : maxAgeSeconds)
			.httpOnly(true)
			.path("/")
			.build();
		httpHeaders.add(HttpHeaders.SET_COOKIE, httpCookie.toString());
	}
}
