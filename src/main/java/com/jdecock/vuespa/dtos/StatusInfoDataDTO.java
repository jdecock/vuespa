package com.jdecock.vuespa.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatusInfoDataDTO<T> extends StatusInfoDTO {
	private T data;

	public StatusInfoDataDTO(boolean success, String message) {
		super(success, message);
	}

	public StatusInfoDataDTO(boolean success, String message, T data) {
		super(success, message);
		this.data = data;
	}
}
