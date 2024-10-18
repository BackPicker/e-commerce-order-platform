package com.hello.ecommerceorderplatform.user.domain;


import jakarta.persistence.*;
import lombok.*;

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


    @Builder
    public User(String username, String password, String phoneNumber, String email, String address, UserRoleEnum userRoleEnum) {
        this.username     = username;
        this.password     = password;
        this.phoneNumber  = phoneNumber;
        this.email        = email;
        this.address      = address;
        this.userRoleEnum = userRoleEnum;
    }


}

