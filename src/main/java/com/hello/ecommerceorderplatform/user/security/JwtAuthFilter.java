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
@RequiredArgsConstructor
public class JwtAuthFilter implements Filter {

    private static final String[] whitelist = {"/", "/api/users/signup", "/api/users/login", "/api/users/logout", "/css/*", "/js/*"};

    private final UserRepositoryImpl userRepository;
    private final JwtUtil            jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String             requestURI         = httpServletRequest.getRequestURI();

        if (isProtectedPath(requestURI)) {
            String tokenValue = jwtUtil.getTokenFromRequest(httpServletRequest);

            if (StringUtils.hasText(tokenValue)) {
                String token = jwtUtil.substringToken(tokenValue);

                if (!jwtUtil.validateToken(token)) {
                    log.error("Invalid token: {}", token);
                    servletResponse.getWriter()
                            .write("Token Error");
                    return; // 적절한 응답 후 종료
                }

                // 토큰에서 사용자 정보 가져오기
                Claims info = jwtUtil.getUserInfoFromToken(token);
                User user = userRepository.findByUsername(info.getSubject())
                        .orElseThrow(() -> {
                            log.error("User not found: {}", info.getSubject());
                            return new IllegalArgumentException("Not Found User");
                        });

                servletRequest.setAttribute("user", user);
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                log.error("Token not found in request");
                servletResponse.getWriter()
                        .write("Not Found Token");
                return; // 적절한 응답 후 종료
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean isProtectedPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
