package com.hello.ecommerceorderplatform;


import com.hello.ecommerceorderplatform.item.domain.Item;
import com.hello.ecommerceorderplatform.item.repository.ItemRepository;
import com.hello.ecommerceorderplatform.user.domain.User;
import com.hello.ecommerceorderplatform.user.domain.UserRoleEnum;
import com.hello.ecommerceorderplatform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Random;

@RequiredArgsConstructor
public class BeforeDataInit {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    // @EventListener(ApplicationReadyEvent.class)
    public void save() {

        userRepository.save(User.builder()
                .username("admin")
                .password("socom!23")
                .phoneNumber("010-1234-1234")
                .email("nico1a@naver.com")
                .address("Seoul")
                .userRoleEnum(UserRoleEnum.ADMIN)
                .build());
        userRepository.save(User.builder()
                .username("userA")
                .password("socom!23")
                .phoneNumber("010-1234-1234")
                .email("abc@naver.com")
                .address("Seoul")
                .userRoleEnum(UserRoleEnum.USER)
                .build());
        userRepository.save(User.builder()
                .username("userB")
                .password("socom!23")
                .phoneNumber("010-1234-1234")
                .email("abcd@naver.com")
                .address("Seoul")
                .userRoleEnum(UserRoleEnum.USER)
                .build());


        for (int i = 1; i <= 100; i++) {
            itemRepository.save(new Item("item" + i, "옷",
                    new Random().nextInt(500) * 10, new Random().nextInt(100), "정말 좋습니다"));

        }


    }


}
