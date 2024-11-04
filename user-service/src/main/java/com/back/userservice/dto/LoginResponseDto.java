package com.back.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {

    private String username;

    public LoginResponseDto(String username) {
        this.username = username;
    }
}
