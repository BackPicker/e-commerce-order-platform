package com.back.itemservice.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    private Long         id;
    private String       username;
    private String       password;
    private String       phoneNumber;
    private String       email;
    private String       address;
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

}
