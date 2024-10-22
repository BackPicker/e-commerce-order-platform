package com.hello.ecommerceorderplatform;


import com.hello.ecommerceorderplatform.item.domain.Item;
import com.hello.ecommerceorderplatform.item.repository.ItemRepository;
import com.hello.ecommerceorderplatform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Random;

@RequiredArgsConstructor
public class BeforeDataInit {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @EventListener(ApplicationReadyEvent.class)
    public void save() {
        for (int i = 1; i <= 100; i++) {
            itemRepository.save(new Item("item" + i, "옷", new Random().nextInt(500) * 10, 500, "정말 좋습니다"));

        }


    }


}
