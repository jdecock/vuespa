package com.jdecock.vuespa.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusInfoDTO {
	private Boolean success;

	private String message;

	public StatusInfoDTO() {
	}

	public StatusInfoDTO(boolean success, String message) {
		this.success = success;
		this.message = message;
	}
}
