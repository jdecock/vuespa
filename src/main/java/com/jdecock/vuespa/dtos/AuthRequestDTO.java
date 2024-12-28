package com.jdecock.vuespa.dtos;

import lombok.Getter;

@Getter
public class AuthRequestDTO {
	private final String email;

	private final String password;

	AuthRequestDTO(String email, String password) {
		this.email = email;
		this.password = password;
	}
}
