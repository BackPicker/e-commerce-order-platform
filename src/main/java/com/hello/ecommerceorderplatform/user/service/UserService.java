package com.hello.ecommerceorderplatform.user.service;


import com.hello.ecommerceorderplatform.user.domain.Address;
import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.domain.UserRoleEnum;
import com.hello.ecommerceorderplatform.user.dto.UserRegisterRequestDto;
import com.hello.ecommerceorderplatform.user.dto.UserResponseDto;
import com.hello.ecommerceorderplatform.user.jwt.JwtUtil;
import com.hello.ecommerceorderplatform.user.repository.UserRepository;
import com.hello.ecommerceorderplatform.user.repository.UserRepositoryImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static int authNum;

    private final UserRepository     userRepository;
    private final UserRepositoryImpl userRepositoryImpl;

    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender  mailSender;
    private final JwtUtil         jwtUtil;


    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;

    @Value("${ADMIN_ADDRESS}}")
    private String ADMIN_ADDRESS;

    /**
     * 메일 보내기 -> 랜덤 숫자 생성
     */
    public static void createNumber() {
        authNum = (int) (Math.random() * (900000)) + 1000000;
    }

    /**
     * 회원가입
     *
     * @param userRegisterRequestDto
     * @return
     */
    @Transactional
    public ResponseEntity<UserResponseDto> createMember(UserRegisterRequestDto userRegisterRequestDto) {
        log.info("userRegisterRequestDto: {}", userRegisterRequestDto);
        String  username = userRegisterRequestDto.getUsername();
        String  password = passwordEncoder.encode(userRegisterRequestDto.getPassword());
        String  email    = passwordEncoder.encode(userRegisterRequestDto.getEmail());
        String  phone    = passwordEncoder.encode(userRegisterRequestDto.getPhoneNumber());
        Address address  = userRegisterRequestDto.getAddress();

        if (userRepositoryImpl.existsUserByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 ID 입니다");
        }
        if (userRepositoryImpl.existsUserByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 EMAIL 입니다");
        }
        sendVerificationEmail(userRegisterRequestDto.getEmail());

        UserRoleEnum role = UserRoleEnum.USER;
        if (userRegisterRequestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(userRegisterRequestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려서 등록이 불가능합니다");
            }
            role = UserRoleEnum.ADMIN;
        }

        User user = new User(username, password, phone, email, address, role);
        userRepository.save(user);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponseDto(user));
    }

    /**
     * 회원가입 -> 이메일 보내기
     *
     * @param emailParam
     */

    private void sendVerificationEmail(String emailParam) {
        SimpleMailMessage email = new SimpleMailMessage();
        createNumber();

        String subject = "[ 이메일 인증 요청 입니다 ]";
        String message = "요청하신 인증 번호는 다음과 같습니다. :\n" + authNum;

        email.setTo(emailParam);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
/*
    @Transactional
    public String login(LoginRequestDto requestDto, HttpServletResponse res) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        User user = userRepositoryImpl.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다"));

        if (passwordEncoder.matches(passwordEncoder.encode(password), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        String token = jwtUtil.createToken(user.getUsername(), user.getUserRoleEnum());
        jwtUtil.addJwtToCookie(token, res);

        return token;
    } */

    // Bearer%20eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkEiLCJhdXRoIjoiQURNSU4iLCJleHAiOjE3Mjk0OTY2NjcsImlhdCI6MTcyOTQ5MzA2N30.Od_rPlhsJ2uUOHK2RZFPDThAwZ6ouh87ocEVmMmYiOM

    public void logout(HttpServletResponse response) {
        jwtUtil.addJwtToCookie("", response);
    }
}
