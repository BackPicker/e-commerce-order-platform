package com.hello.ecommerceorderplatform.order.controller;

import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    /**
     * WishList 를 이용한 주문
     * 여러 상품, 다수 주문
     */

    /**
     * 그냥 주문
     * 하나의 상품, 다수 주문
     */
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }


}
