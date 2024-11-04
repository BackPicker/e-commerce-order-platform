package com.back.userservice.controller;

import com.back.common.dto.ResponseMessage;
import com.back.userservice.dto.LoginRequestDto;
import com.back.userservice.dto.SignupRequestDto;
import com.back.userservice.entity.User;
import com.back.userservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> createUser(
            @RequestBody
            SignupRequestDto signupRequestDto) throws BadRequestException {
        User createdUser = userService.signup(signupRequestDto);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdUser)
                .statusCode(201)
                .resultMessage("회원가입 성공")
                .build();

        return ResponseEntity.status(201)
                .body(response);
    }

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
