package com.back.userservice.controller;

import com.back.common.dto.ResponseMessage;
import com.back.userservice.dto.EmailRequestDto;
import com.back.userservice.dto.LoginRequestDto;
import com.back.userservice.dto.SignupRequestDto;
import com.back.userservice.entity.User;
import com.back.userservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Queue;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseMessage createUser(
            @Valid
            @RequestBody
            SignupRequestDto signupRequestDto,
            BindingResult bindingResult) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        // Validation 예외 처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " : " + fieldError.getDefaultMessage());
            }
        }


        userService.verifyEmail(signupRequestDto.getEmail(), signupRequestDto.getVerifyNumber());

        return userService.signup(signupRequestDto);
    }

    // 이메일 인증
    @PostMapping("/verify-email")
    public ResponseMessage sendEmail(
            @RequestBody
            EmailRequestDto requestDto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        return userService.sendVerifyEmail(requestDto.getEmail());
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody
            LoginRequestDto loginRequestDto, HttpServletResponse response) {

        return userService.login(loginRequestDto, response);
    }
    // Eureka
    @GetMapping("/api/user/getQueue")
    Queue<User> eurekaGetUserByQueue(List<Long> userIdWishList) {
        return userService.eurekaGetUserByQueue(userIdWishList);
    }



}
