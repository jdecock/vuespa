package com.jdecock.vuespa.controllers;

import com.jdecock.vuespa.dtos.ChangePasswordDTO;
import com.jdecock.vuespa.dtos.StatusInfoDTO;
import com.jdecock.vuespa.dtos.StatusInfoDataDTO;
import com.jdecock.vuespa.dtos.UserDTO;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.UserRepository;
import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserService;
import com.jdecock.vuespa.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {
	private final UserService userService;
	private final JwtService jwtService;

	public UserController(UserRepository userRepository, UserService userService, JwtService jwtService) {
		super(userRepository);
		this.userService = userService;
		this.jwtService = jwtService;
	}

	@GetMapping("")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public StatusInfoDataDTO<UserDTO> index() {
		User user = getCurrentUser();
		return user == null
			? new StatusInfoDataDTO<>(false, "The requested user does not exist")
			: new StatusInfoDataDTO<>(true, "User found", new UserDTO(user));
	}

	@PostMapping("/change-password")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public StatusInfoDTO changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
		if (changePasswordDTO == null || StringUtils.isEmpty(changePasswordDTO.getCurPassword()) ||
			StringUtils.isEmpty(changePasswordDTO.getNewPassword()))
			return new StatusInfoDTO(false, "The specified password is invalid");

		User user = getCurrentUser();
		if (user == null)
			return new StatusInfoDTO(false, "There is no user logged in");

		if (!UserService.isPasswordCorrect(user, changePasswordDTO.getCurPassword()))
			return new StatusInfoDTO(false, "The current password is incorrect");

		userService.changePassword(user, changePasswordDTO.getNewPassword());
		return new StatusInfoDTO(true, "Password changed successfully");
	}

	@PostMapping("/update")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public StatusInfoDataDTO<UserDTO> updateUser(HttpServletRequest request, HttpServletResponse response,
	                                             @RequestBody UserDTO userDTO) {
		if (userDTO == null || StringUtils.isEmpty(userDTO.getName()) || StringUtils.isEmpty(userDTO.getEmail()))
			return new StatusInfoDataDTO<>(false, "The specified user information is invalid");

		User user = getCurrentUser();
		if (user == null)
			return new StatusInfoDataDTO<>(false, "There is no user logged in");

		if (!Objects.equals(user.getId(), userDTO.getId()))
			return new StatusInfoDataDTO<>(false, "You do not have permission to edit the specified user");

		user.setName(userDTO.getName());
		user.setEmail(userDTO.getEmail());

		user = userRepository.save(user);

		// Update the authorization tokens
		jwtService.reissueTokens(request, response, user);

		return new StatusInfoDataDTO<>(true, "User updated successfully", new UserDTO(user));
	}

	@GetMapping("/search")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public StatusInfoDataDTO<List<UserDTO>> searchForUsers(@RequestParam String query) {
		List<UserDTO> results = userRepository
			.findByNameContainingIgnoreCaseOrEmailIsContainingIgnoreCase(query, query)
			.stream().map(UserDTO::new).collect(Collectors.toList());

		return new StatusInfoDataDTO<>(true, "Found " + results.size() + " users", results);
	}
}
