package com.back.userservice.service;

import com.back.userservice.dto.LoginRequestDto;
import com.back.userservice.dto.SignupRequestDto;
import com.back.userservice.entity.User;

public interface UserService {
    User signup(SignupRequestDto createUserRequestDto);

    String login(LoginRequestDto userCommonDto);
}