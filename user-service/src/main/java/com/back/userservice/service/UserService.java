package com.back.userservice.service;

import com.back.common.dto.ResponseMessage;
import com.back.userservice.config.AES128Config;
import com.back.userservice.dto.LoginRequestDto;
import com.back.userservice.dto.SignupRequestDto;
import com.back.userservice.dto.UserResponseDto;
import com.back.userservice.entity.User;
import com.back.userservice.entity.UserRoleEnum;
import com.back.userservice.repository.UserRepository;
import com.back.userservice.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private static int authNum;

    private final UserRepository                userRepository;
    private final PasswordEncoder               passwordEncoder;
    private final AES128Config                  aes128Config;
    private final RedisTemplate<String, String> redisTemplate;

    private final JavaMailSender mailSender;
    private final JwtUtil        jwtUtil;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    public static void createNumber() {
        authNum = (int) (Math.random() * (90000)) + 100000;
    }

    public void verifyEmail(String verifyEmail,
                            String verifyNumber) {
        String redisNumber = redisTemplate.opsForValue()
                .get(verifyEmail);
        log.info("redisNumber = {}, verifyNumber = {}", redisNumber, verifyNumber);

        // 인증 가능한 시간이 만료됐을 경우
        if (redisNumber == null) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다, 다시 인증을 받아주세요");
        }
        // 인증번호가 서로 다를 경우
        if (!redisNumber.equals(verifyNumber)) {
            throw new IllegalArgumentException("인증번호가 틀립니다");
        }
        redisTemplate.delete(verifyEmail);
    }

    @Transactional
    public ResponseMessage signup(SignupRequestDto createUserRequestDto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        if (userRepository.existsUserByUsername(createUserRequestDto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 ID 입니다");
        }

        if (userRepository.existsUserByUsername(createUserRequestDto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 ID 입니다");
        }

        String username = createUserRequestDto.getUsername();
        String password = passwordEncoder.encode(createUserRequestDto.getPassword());
        String email   = aes128Config.encryptAes(createUserRequestDto.getEmail());
        String phone   = aes128Config.encryptAes(createUserRequestDto.getPhoneNumber());
        String address = aes128Config.encryptAes(createUserRequestDto.getAddress());


        UserRoleEnum role = UserRoleEnum.USER;
        if (createUserRequestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(createUserRequestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려서 등록이 불가능합니다");
            }
            role = UserRoleEnum.ADMIN;
        }

        User user = new User(username, password, phone, email, address, role);
        userRepository.save(user);
        return ResponseMessage.builder()
                .data(new UserResponseDto(user))
                .statusCode(201)
                .resultMessage("회원가입 성공")
                .build();
    }

    public ResponseMessage sendVerifyEmail(String emailParam) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String aesEmail = aes128Config.encryptAes(emailParam);

        if (userRepository.existsUserByEmail(aesEmail)) {
            throw new IllegalArgumentException("이미 가입된 이메일 입니다");
        }

        redisTemplate.opsForValue()
                .set(emailParam, sendMail(emailParam), 30, TimeUnit.MINUTES);

        return ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("이메일 전송 성공")
                .build();
    }

    public String sendMail(String mail) {
        sendEmail(mail);
        return String.valueOf(authNum);
    }

    public void sendEmail(String emailParam) {
        createNumber();
        SimpleMailMessage email = new SimpleMailMessage();

        String subject = "[ 이메일 인증 요청 입니다 ]";
        String message = "요청하신 인증 번호는 다음과 같습니다. :\n" + authNum;

        email.setTo(emailParam);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }


    public ResponseEntity<?> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String userName = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new NoSuchElementException("등록된 사용자가 없습니다"));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        // JWT 생성 및 쿠키에 저장 후 Response객체 추가
        String token = jwtUtil.createToken(userName, user.getRole());
        jwtUtil.addJwtToCookie(token, response);

        return ResponseEntity.ok()
                .body(ResponseMessage.builder()
                              .statusCode(HttpStatus.OK.value())
                              .build());

    }


    public Queue<User> eurekaGetUserByQueue(List<Long> userIdWishList) {
        Queue<User> userQueue = new ArrayDeque<>();
        for (Long wishUserId : userIdWishList) {
            User user = userRepository.findById(wishUserId)
                    .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다"));
            userQueue.offer(user);
        }


        return userQueue;
    }
}
