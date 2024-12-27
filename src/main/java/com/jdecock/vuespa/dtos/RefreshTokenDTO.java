package com.jdecock.vuespa.dtos;

import com.jdecock.vuespa.entities.RefreshToken;
import com.jdecock.vuespa.entities.User;
import lombok.Getter;

import java.util.Date;

@Getter
public class RefreshTokenDTO {
	private Long id;

	private Long userId;

	private String token;

	private String description;

	private Date tokenExpiration;

	private Date creationDate;

	public RefreshTokenDTO(RefreshToken refreshToken) {
		if (refreshToken == null)
			return;

		this.id = refreshToken.getId();
		this.token = refreshToken.getToken();
		this.description = refreshToken.getDescription();
		this.tokenExpiration = refreshToken.getTokenExpiration();
		this.creationDate = refreshToken.getCreationDate();

		User user = refreshToken.getUser();
		this.userId = user == null ? null : user.getId();
	}
}
