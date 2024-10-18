package com.hello.ecommerceorderplatform.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class UserRegisterRequestDto {

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{5,}$")
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{4,}$")
    private String password;

    @NotBlank
    private String phoneNumber;

    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$", message = "유효하지 않은 이메일 주소입니다.")
    private String email;

    @NotBlank
    private String address;

    private boolean admin = false;

    private String adminToken = "";
}
