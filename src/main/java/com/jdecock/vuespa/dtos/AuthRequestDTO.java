package com.jdecock.vuespa.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthRequestDTO {
	private String email;

	private String password;

	AuthRequestDTO(String email, String password) {
		this.email = email;
		this.password = password;
	}
}
