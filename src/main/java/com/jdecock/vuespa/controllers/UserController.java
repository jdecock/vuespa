package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.dtos.StatusInfoDataDTO;
import com.jdecock.vuespa.dtos.UserDTO;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {
	public UserController(UserRepository userRepository) {
		super(userRepository);
	}

	@GetMapping("")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public StatusInfoDataDTO<UserDTO> index() {
		User user = getCurrentUser();
		return user == null
			? new StatusInfoDataDTO<>(false, "The requested user does not exist")
			: new StatusInfoDataDTO<>(true, "User found", new UserDTO(user));
	}
}
