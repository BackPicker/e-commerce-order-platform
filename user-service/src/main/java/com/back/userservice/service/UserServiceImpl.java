package com.back.userservice.service;

import com.back.common.exception.BizRuntimeException;
import com.back.userservice.dto.LoginRequestDto;
import com.back.userservice.dto.SignupRequestDto;
import com.back.userservice.entity.Address;
import com.back.userservice.entity.User;
import com.back.userservice.entity.UserRoleEnum;
import com.back.userservice.repository.UserRepository;
import com.back.userservice.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private static int authNum;

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender  mailSender;
    private final JwtUtil         jwtUtil;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    /**
     * 메일 보내기 -> 랜덤 숫자 생성
     */
    public static void createNumber() {
        authNum = (int) (Math.random() * (900000)) + 1000000;
    }

    @Override
    @Transactional
    public User signup(SignupRequestDto createUserRequestDto) {
        try {
            String  username = createUserRequestDto.getUsername();
            String  password = passwordEncoder.encode(createUserRequestDto.getPassword());
            String  email    = passwordEncoder.encode(createUserRequestDto.getEmail());
            String  phone    = passwordEncoder.encode(createUserRequestDto.getPhoneNumber());
            Address address  = createUserRequestDto.getAddress();

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

            sendVerificationEmail(createUserRequestDto.getEmail());

            User user = new User(username, password, phone, email, address, role);
            return saveUser(user);
        } catch (DataAccessException e) {
            log.error("회원가입 처리 중 데이터베이스 오류 발생", e);
            throw new BizRuntimeException("회원가입 처리 중 데이터베이스 오류가 발생했습니다.", e);
        } catch (BizRuntimeException e) {
            log.error("회원가입 처리 중 비즈니스 로직 오류 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("회원가입 처리 중 예기치 않은 오류 발생", e);
            throw new BizRuntimeException("회원가입 처리 중 예기치 않은 오류가 발생했습니다.", e);
        }
    }

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

    @Override
    public String login(LoginRequestDto loginRequestDto) {
        try {
            Optional<User> user = userRepository.findByEmail(loginRequestDto.getEmail());
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


    private User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataAccessException e) {
            log.error("회원 저장 중 데이터베이스 오류 발생", e);
            throw new BizRuntimeException("회원 저장에 실패했습니다.", e);
        } catch (Exception e) {
            log.error("회원 저장 중 예기치 않은 오류 발생", e);
            throw new BizRuntimeException("회원 저장 중 예기치 않은 오류가 발생했습니다.", e);
        }
    }

}
