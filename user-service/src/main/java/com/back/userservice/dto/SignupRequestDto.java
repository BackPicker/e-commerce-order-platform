package com.back.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequestDto {

    private String username;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{4,}$", message = "비밀번호는 최소 4자리 이상이며, 숫자, 문자 및 특수 문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;

    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$", message = "유효하지 않은 이메일 주소입니다.")
    private String email;

    private String verifyNumber;

    private String address;

    private boolean admin;

    private String adminToken = "";

    // Address 클래스를 포함한 생성자
    @Builder
    public SignupRequestDto(String username,
                            String password,
                            String phoneNumber,
                            String email,
                            String address,
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