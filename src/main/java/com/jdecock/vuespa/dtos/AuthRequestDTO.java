package com.jdecock.vuespa.dtos;

public record AuthRequestDTO(String email, String password, boolean persistLogin) {
}
