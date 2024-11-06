package com.back.userservice.controller;

import com.back.common.dto.ResponseMessage;
import com.back.userservice.dto.EmailRequestDto;
import com.back.userservice.dto.LoginRequestDto;
import com.back.userservice.dto.SignupRequestDto;
import com.back.userservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseMessage createUser(
            @RequestBody
            SignupRequestDto signupRequestDto) throws BadRequestException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        userService.verifyEmail(signupRequestDto.getEmail(), signupRequestDto.getVerifyNumber());

        return userService.signup(signupRequestDto);
    }

    // 이메일 인증
    @PostMapping("/email")
    public ResponseMessage sendEmail(
            @RequestBody
            EmailRequestDto requestDto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        return userService.sendVerifyEmail(requestDto.getEmail());
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(
            @RequestBody
            LoginRequestDto loginRequestDto,
            HttpServletResponse response) throws BadRequestException {

        String token = userService.login(loginRequestDto);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(token)
                .statusCode(200)
                .resultMessage("Login successful")
                .build();

        return ResponseEntity.ok(responseMessage);
    }
}
