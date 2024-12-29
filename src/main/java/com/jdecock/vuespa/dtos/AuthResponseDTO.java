package com.jdecock.vuespa.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponseDTO {
	private String refreshToken;

	public AuthResponseDTO(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
