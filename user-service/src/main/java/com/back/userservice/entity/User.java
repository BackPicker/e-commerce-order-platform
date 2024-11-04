package com.back.userservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber; // 전화번호

    @Column(nullable = false, unique = true)
    private String email; // 회원 이메일

    @Column(nullable = false)
    @Embedded
    private Address address; // 회원 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role;

    @PrePersist
    protected void onCreate() {
        this.role = UserRoleEnum.USER;
    }

    public User(String username,
                String password,
                String phoneNumber,
                String email,
                Address address,
                UserRoleEnum role) {
        this.username    = username;
        this.password    = password;
        this.phoneNumber = phoneNumber;
        this.email       = email;
        this.address     = address;
        this.role        = role;
    }
}