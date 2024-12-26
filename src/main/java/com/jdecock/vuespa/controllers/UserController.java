package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.dtos.StatusInfoDataDTO;
import com.jdecock.vuespa.dtos.UserDTO;
import com.jdecock.vuespa.repositories.UserRepository;
import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.utils.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
	private final JwtService jwtService;
	private final UserRepository userRepository;

	public UserController(JwtService jwtService, UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	@GetMapping("/")
//	@PreAuthorize("hasAuthority('ROLE_USER')")
	public StatusInfoDataDTO<UserDTO> index(@RequestHeader (name="Authorization") String token) {
		if (token != null && token.startsWith ("Bearer "))
			token = token.substring (7);

		var email = StringUtils.isEmpty(token) ? null : jwtService.extractUsername(token);
		if (StringUtils.isEmpty(email))
			return new StatusInfoDataDTO<>(false, "The supplied authentication token is invalid");

		var user = userRepository.findByEmail(email).orElse(null);
		return user == null
			? new StatusInfoDataDTO<>(false, "The requested user does not exist")
			: new StatusInfoDataDTO<>(true, "User found", new UserDTO(user));
	}
}
