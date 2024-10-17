package com.hello.ecommerceorderplatform.user.service;


import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.domain.UserRoleEnum;
import com.hello.ecommerceorderplatform.user.domain.dto.UserRegisterRequestDto;
import com.hello.ecommerceorderplatform.user.domain.dto.UserRegisterResponseDto;
import com.hello.ecommerceorderplatform.user.repository.UserRepository;
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
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final  UserRepository  userRepository;
    private final  PasswordEncoder passwordEncoder;
    private final  JavaMailSender  mailSender;
    private static int             authNum;


    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;

    @Value("${ADMIN_ADDRESS}}")
    private String ADMIN_ADDRESS;

    @Override
    public ResponseEntity<UserRegisterResponseDto> createMember(UserRegisterRequestDto userRegisterRequestDto) {
        log.info("userRegisterRequestDto: {}", userRegisterRequestDto);
        String username = userRegisterRequestDto.getUsername();
        String password = passwordEncoder.encode(userRegisterRequestDto.getPassword());
        String email    = passwordEncoder.encode(userRegisterRequestDto.getEmail());
        String phone    = passwordEncoder.encode(userRegisterRequestDto.getPhoneNumber());
        String address  = passwordEncoder.encode(userRegisterRequestDto.getAddress());

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 ID 입니다");
        } else if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 EMAIL 입니다");
        } else {
            sendVerificationEmail(userRegisterRequestDto.getEmail());
        }

        UserRoleEnum role = UserRoleEnum.USER;
        if (userRegisterRequestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(userRegisterRequestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 다릅니다");
            }
            role = UserRoleEnum.ADMIN;
        }
        User user = new User(username, password, phone, email, address, role);
        userRepository.save(user);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserRegisterResponseDto(user));
    }


    private void sendVerificationEmail(String emailParam) {
        log.info("emailParam = {}", emailParam);
        SimpleMailMessage email = new SimpleMailMessage();
        createNumber();

        String subject = "[ 이메일 인증 요청 입니다 ]";
        String message = "요청하신 인증 번호는 다음과 같습니다. :\n" + authNum;

        email.setTo(emailParam);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);


    }

    public static void createNumber() {
        authNum = (int) (Math.random() * (90000)) + 100000;
    }


}
