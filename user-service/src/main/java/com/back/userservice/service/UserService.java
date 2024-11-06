package com.back.userservice.service;

import com.back.common.dto.ResponseMessage;
import com.back.common.exception.BizRuntimeException;
import com.back.userservice.config.AES128Config;
import com.back.userservice.dto.LoginRequestDto;
import com.back.userservice.dto.SignupRequestDto;
import com.back.userservice.dto.UserResponseDto;
import com.back.userservice.entity.User;
import com.back.userservice.entity.UserRoleEnum;
import com.back.userservice.repository.UserRepository;
import com.back.userservice.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Optional;
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

    public void verifyEmail(String verifyEmail,
                            String verifyNumber) {
        String redisNumber = redisTemplate.opsForValue()
                .get(verifyEmail);
        log.info("redisNumbr = {}, veryfyNumber = {}", redisNumber, verifyNumber);

        if (redisNumber == null) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다, 다시 인증을 받아주세요");
        }
        if (!redisNumber.equals(verifyNumber)) {
            throw new IllegalArgumentException("인증번호가 틀립니다");
        }
        redisTemplate.delete(verifyEmail);
    }

    @Transactional
    public ResponseMessage signup(SignupRequestDto createUserRequestDto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        String username = createUserRequestDto.getUsername();
        String password = passwordEncoder.encode(createUserRequestDto.getPassword());

        String email   = aes128Config.encryptAes(createUserRequestDto.getEmail());
        String phone   = aes128Config.encryptAes(createUserRequestDto.getPhoneNumber());
        String address = aes128Config.encryptAes(createUserRequestDto.getAddress());

        if (userRepository.existsUserByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 ID 입니다");
        }
        if (userRepository.existsUserByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 EMAIL 입니다");
        }

        UserRoleEnum role = UserRoleEnum.USER;
        if (createUserRequestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(createUserRequestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려서 등록이 불가능합니다");
            }
            role = UserRoleEnum.ADMIN;
        }
        User user = new User(username, password, phone, email, address, role);
        userRepository.save(user);
        UserResponseDto dto = new UserResponseDto(user);
        return ResponseMessage.builder()
                .data(dto)
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

    public static void createNumber() {
        authNum = (int) (Math.random() * (90000)) + 100000;
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


    public String login(LoginRequestDto loginRequestDto) {
        try {
            Optional<User> user = userRepository.findByUsername(loginRequestDto.getUsername());
            if (user.isEmpty()) {
                throw new BizRuntimeException("존재하지 않는 유저입니다.");
            }

            if (passwordEncoder.matches(loginRequestDto.getPassword(), user.get()
                    .getPassword())) {
                return jwtUtil.createToken(user.get()
                        .getId(), user.get()
                        .getRole()
                        .toString());
            } else {
                throw new BizRuntimeException("잘못된 비밀번호입니다.");
            }
        } catch (DataAccessException e) {
            log.error("로그인 처리 중 데이터베이스 오류 발생", e);
            throw new BizRuntimeException("로그인 처리 중 데이터베이스 오류가 발생했습니다.", e);
        } catch (BizRuntimeException e) {
            log.error("로그인 처리 중 비즈니스 로직 오류 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("로그인 처리 중 예기치 않은 오류 발생", e);
            throw new BizRuntimeException("로그인 처리 중 예기치 않은 오류가 발생했습니다.", e);
        }
    }


}
