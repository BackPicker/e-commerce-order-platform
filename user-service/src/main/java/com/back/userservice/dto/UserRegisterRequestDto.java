package com.back.userservice.dto;


import com.back.userservice.domain.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserRegisterRequestDto {

    private String username;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{4,}$", message = "비밀번호는 최소 4자리 이상이며, 숫자, 문자 및 특수 문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;

    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$", message = "유효하지 않은 이메일 주소입니다.")
    private String email;

    private Address address;

    private boolean admin = false;

    private String adminToken = "";

    // Address 클래스를 포함한 생성자
    public UserRegisterRequestDto(String username,
                                  String password,
                                  String phoneNumber,
                                  String email,
                                  Address address,
                                  boolean admin,
                                  String adminToken) {
        this.username    = username;
        this.password    = password;
        this.phoneNumber = phoneNumber;
        this.email       = email;
        this.address     = address;
        this.admin       = admin;
        this.adminToken  = adminToken;
    }
}