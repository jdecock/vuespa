package com.jdecock.vuespa.dtos;

import lombok.Getter;

@Getter
public class AuthRequestDTO {
	private final String email;

	private final String password;

	private final boolean persistLogin;

	AuthRequestDTO(String email, String password, boolean persistLogin) {
		this.email = email;
		this.password = password;
		this.persistLogin = persistLogin;
	}
}
