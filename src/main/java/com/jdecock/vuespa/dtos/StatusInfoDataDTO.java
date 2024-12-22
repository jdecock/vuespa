package com.jdecock.vuespa.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusInfoDataDTO<T> extends StatusInfoDTO {
	private T data;

	public StatusInfoDataDTO() {
		super();
	}

	public StatusInfoDataDTO(boolean success, String message) {
		super(success, message);
	}

	public StatusInfoDataDTO(boolean success, String message, T data) {
		super(success, message);
		this.data = data;
	}
}
