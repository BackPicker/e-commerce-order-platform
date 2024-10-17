package com.hello.ecommerceorderplatform.user.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;          // 회원 ID

    @Column(nullable = false, unique = true)
    private String username; // 회원 아이디

    @Column(nullable = false)
    private String password; // 회원 비밀번호 (암호화 저장)

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email; // 회원 이메일

    @Column(nullable = false)
    private String address; // 회원 주소

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public UserRoleEnum userRoleEnum;

/*
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
*/


    public User(String username, String password, String phoneNumber, String email, String address, UserRoleEnum userRoleEnum) {
        this.username     = username;
        this.password     = password;
        this.phoneNumber  = phoneNumber;
        this.email        = email;
        this.address      = address;
        this.userRoleEnum = userRoleEnum;
    }

    private String formatPhoneNumber(String phoneNumber) {
        // 하이픈(-)을 포함한 전화번호로 변환
        return phoneNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }
}

