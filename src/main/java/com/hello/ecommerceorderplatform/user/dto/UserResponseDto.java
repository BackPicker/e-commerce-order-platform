package com.hello.ecommerceorderplatform.user.dto;

import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.domain.UserRoleEnum;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private final String       username;
    private final String       email;
    private final String       address;
    private final String       phoneNumber;
    private final UserRoleEnum userRole;

    public UserResponseDto(User user) {
        this.username    = user.getUsername();
        this.email       = user.getEmail();
        this.address     = user.getAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.userRole = user.getRole();
    }
}
