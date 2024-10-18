package com.hello.ecommerceorderplatform.user.security;

import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.repository.UserRepositoryImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;


@Slf4j(topic = "AuthFilter")
// @Component
@RequiredArgsConstructor
public class JwtAuthFilter implements Filter {

    private static final String[] whitelist = {"/", "/api/users/signup", "/api/users/login", "/api/users/logout", " / css/*", "/js/*"};

    private final UserRepositoryImpl userRepository;
    private final JwtUtil            jwtUtil;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String             requestURI         = httpServletRequest.getRequestURI();
        if (!isLoginCheckPath(requestURI)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String tokenValue = jwtUtil.getTokenFromRequest(httpServletRequest);

            if (StringUtils.hasText(tokenValue)) {
                String token = jwtUtil.substringToken(tokenValue);

                if (!jwtUtil.validateToken(token)) {
                    throw new IllegalArgumentException("Token Error");
                }
                // 토큰에서 사용자 정보 가져오기
                Claims info = jwtUtil.getUserInfoFromToken(token);

                User user = userRepository.findByUsername(info.getSubject())
                        .orElseThrow(() -> new NullPointerException("Not Found User"));
                servletRequest.setAttribute("user", user);
                filterChain.doFilter(servletRequest, servletResponse); // 다음 Filter 로 이동

            } else {
                throw new IllegalArgumentException("Not Found Token");
            }
        }
    }

    /**
     * 화이트 리스트의 경우 인증 체크X
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}