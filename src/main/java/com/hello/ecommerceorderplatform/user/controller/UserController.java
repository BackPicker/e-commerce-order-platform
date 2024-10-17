package com.hello.ecommerceorderplatform.user.controller;


import com.hello.ecommerceorderplatform.user.domain.dto.UserRegisterRequestDto;
import com.hello.ecommerceorderplatform.user.domain.dto.UserRegisterResponseDto;
import com.hello.ecommerceorderplatform.user.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserRegisterResponseDto> createMember(
            @Valid @RequestBody UserRegisterRequestDto userRegisterRequestDto, BindingResult bindingResult) {
        log.info("컨트롤러 접근");
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : fieldErrors) {
                log.info("Error: {}", fieldError.getDefaultMessage());
            }
            throw new IllegalArgumentException("오류가 발생했습니다");
        }

        return userService.createMember(userRegisterRequestDto);
    }


}
