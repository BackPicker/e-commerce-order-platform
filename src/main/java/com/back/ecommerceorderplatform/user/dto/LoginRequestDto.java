package com.back.ecommerceorderplatform.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequestDto {
    private String username;
    private String password;
}
