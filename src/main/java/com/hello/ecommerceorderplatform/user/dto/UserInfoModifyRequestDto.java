package com.hello.ecommerceorderplatform.user.dto;

import lombok.Getter;

@Getter
public class UserInfoModifyRequestDto {

    private String phoneNumber;
    private String address;

    public UserInfoModifyRequestDto(String phoneNumber, String address) {
        this.phoneNumber = phoneNumber;
        this.address     = address;
    }
}
