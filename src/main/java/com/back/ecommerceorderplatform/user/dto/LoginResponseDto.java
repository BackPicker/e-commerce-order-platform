package com.back.ecommerceorderplatform.user.dto;

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
