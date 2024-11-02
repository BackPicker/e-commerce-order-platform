package com.back.ecommerceorderplatform;


import com.back.itemservice.domain.Item;
import com.back.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Random;

@RequiredArgsConstructor
public class BeforeDataInit {
    private final ItemRepository itemRepository;


    @EventListener(ApplicationReadyEvent.class)
    public void save() {
        for (int i = 1; i <= 100; i++) {
            itemRepository.save(new Item("item" + i, "신발", new Random().nextInt(500) * 10, 500));

        }


    }


}
