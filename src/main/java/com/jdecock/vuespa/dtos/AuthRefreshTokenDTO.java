package com.jdecock.vuespa.dtos;

import lombok.Getter;

@Getter
public class AuthRefreshTokenDTO {
	private final String refreshToken;

	AuthRefreshTokenDTO(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
