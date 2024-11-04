package com.back.userservice.dto;


import com.back.userservice.domain.Address;
import com.back.userservice.domain.User;
import com.back.userservice.domain.UserRoleEnum;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private final String       username;
    private final String       email;
    private final Address      address; // Address 객체로 변경
    private final String       phoneNumber;
    private final UserRoleEnum userRole;

    public UserResponseDto(User user) {
        this.username    = user.getUsername();
        this.email       = user.getEmail();
        this.address     = user.getAddress(); // Address 객체
        this.phoneNumber = user.getPhoneNumber();
        this.userRole    = user.getRole();
    }
}