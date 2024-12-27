package com.jdecock.vuespa.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatusInfoDTO {
	private boolean success = false;

	private String message;

	public StatusInfoDTO(boolean success, String message) {
		this.success = success;
		this.message = message;
	}
}
