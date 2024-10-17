package com.hello.ecommerceorderplatform.user.domain.dto;

import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.domain.UserRoleEnum;
import lombok.Getter;

@Getter
public class UserRegisterResponseDto {

    private String       username;
    private String       email;
    private String       address;
    private String       phoneNumber;
    private UserRoleEnum userRole;

    public UserRegisterResponseDto(User user) {
        this.username    = user.getUsername();
        this.email       = user.getEmail();
        this.address     = user.getAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.userRole    = user.getUserRoleEnum();
    }
}
