package com.back.userservice.dto;

import com.back.userservice.entity.User;
import com.back.userservice.entity.UserRoleEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponseDto {


    private String username;

    private String phoneNumber; // 전화번호

    private String email; // 회원 이메일

    private String address; // 회원 주소

    private UserRoleEnum role;


    public UserResponseDto(User user) {
        this.username    = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.email       = user.getEmail();
        this.address     = user.getAddress();
        this.role        = user.getRole();
    }
}
