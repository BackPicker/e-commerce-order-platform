package com.back.ecommerceorderplatform.user.dto;

import lombok.Getter;

@Getter
public class UserInfoModifyRequestDto {

    private final String phoneNumber;
    private final String address;

    public UserInfoModifyRequestDto(String phoneNumber,
                                    String address) {
        this.phoneNumber = phoneNumber;
        this.address     = address;
    }
}
