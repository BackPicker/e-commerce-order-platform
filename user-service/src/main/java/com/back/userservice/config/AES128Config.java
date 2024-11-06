package com.back.userservice.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class AES128Config {

    private static final Charset ENCODING_TYPE = StandardCharsets.UTF_8;
    private static final String  INSTANCE_TYPE = "AES/CBC/PKCS5Padding";

    @Value("${aes.secret.key}")
    private String secretKey;

    private IvParameterSpec ivParameterSpec;
    private SecretKeySpec   secretKeySpec;
    private Cipher          cipher;

    @PostConstruct
    public void init() throws NoSuchPaddingException, NoSuchAlgorithmException {
        // secretKey 길이가 16바이트로 맞춰지도록 처리
        if (secretKey.length() != 16) {
            if (secretKey.length() > 16) {
                secretKey = secretKey.substring(0, 16); // 16바이트로 자르기
            } else {
                // 16바이트로 패딩하기
                secretKey = String.format("%-16s", secretKey); // 공백으로 패딩
            }
        }

        log.info("사용할 시크릿 키: {}", secretKey);

        byte[] iv = new byte[16]; // 16bytes = 128bits
        new SecureRandom().nextBytes(iv);  // 랜덤 IV 생성
        ivParameterSpec = new IvParameterSpec(iv);

        secretKeySpec = new SecretKeySpec(secretKey.getBytes(ENCODING_TYPE), "AES");
        cipher        = Cipher.getInstance(INSTANCE_TYPE);
    }

    // AES 암호화
    public String encryptAes(String plaintext) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        log.info("secret key : {}", secretKey);
        log.info("암호화할 데이터 : {}", plaintext);

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(ENCODING_TYPE));

        // 암호화된 데이터와 IV를 함께 반환
        byte[] combined = new byte[encrypted.length + ivParameterSpec.getIV().length];
        System.arraycopy(ivParameterSpec.getIV(), 0, combined, 0, ivParameterSpec.getIV().length);
        System.arraycopy(encrypted, 0, combined, ivParameterSpec.getIV().length, encrypted.length);

        return Base64.getEncoder()
                .encodeToString(combined);  // IV + 암호화된 데이터를 Base64로 인코딩하여 반환
    }

    // AES 복호화
    public String decryptAes(String encryptedText) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        log.info("시크릿 키 : {}", secretKey);
        log.info("복호화할 데이터 : {}", encryptedText);

        byte[] combined = Base64.getDecoder()
                .decode(encryptedText);  // Base64로 디코딩
        byte[] iv        = new byte[16];  // 앞 16바이트는 IV
        byte[] encrypted = new byte[combined.length - 16];  // 나머지 데이터는 암호화된 부분

        System.arraycopy(combined, 0, iv, 0, iv.length);  // 앞 16바이트는 IV
        System.arraycopy(combined, 16, encrypted, 0, encrypted.length);  // 나머지 데이터는 암호화된 부분

        ivParameterSpec = new IvParameterSpec(iv);  // 복호화 시 IV 설정
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(encrypted);  // 암호화된 데이터 복호화

        return new String(decrypted, ENCODING_TYPE);
    }
}
