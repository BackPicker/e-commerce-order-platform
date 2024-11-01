package com.back.ecommerceorderplatform.user.controller;


import com.back.ecommerceorderplatform.user.dto.UserRegisterRequestDto;
import com.back.ecommerceorderplatform.user.dto.UserResponseDto;
import com.back.ecommerceorderplatform.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     *
     * @param userRegisterRequestDto
     * @param bindingResult
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> createMember(
            @Valid
            @RequestBody
            UserRegisterRequestDto userRegisterRequestDto,
            BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : fieldErrors) {
                log.info("Error: {}", fieldError.getDefaultMessage());
            }
            throw new IllegalArgumentException("오류가 발생했습니다");
        }

        return userService.createMember(userRegisterRequestDto);
    }


    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        userService.logout(response);
        return ResponseEntity.ok("Logout successful.");
    }

    /**
     * 회원 정보 변경
     */


    /**
     * 회원 비밀번호 변경
     */

}
