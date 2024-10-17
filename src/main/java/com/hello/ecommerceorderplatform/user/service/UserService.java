package com.hello.ecommerceorderplatform.user.service;


import com.hello.ecommerceorderplatform.user.domain.dto.UserRegisterRequestDto;
import com.hello.ecommerceorderplatform.user.domain.dto.UserRegisterResponseDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<UserRegisterResponseDto> createMember(UserRegisterRequestDto userRegisterRequestDto);

}
