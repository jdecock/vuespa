package com.jdecock.vuespa.dtos;

import lombok.Getter;

@Getter
public class ChangePasswordDTO {
	private final String curPassword;
	private final String newPassword;

	public ChangePasswordDTO(String curPassword, String newPassword) {
		this.curPassword = curPassword;
		this.newPassword = newPassword;
	}
}
