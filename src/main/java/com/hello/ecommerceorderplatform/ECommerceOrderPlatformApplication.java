package com.hello.ecommerceorderplatform;

import com.hello.ecommerceorderplatform.item.repository.ItemRepository;
import com.hello.ecommerceorderplatform.user.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
public class ECommerceOrderPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceOrderPlatformApplication.class, args);
    }


    @Bean
    public BeforeDataInit beforeDataInit(UserRepository userRepository, ItemRepository itemRepository) {
        return new BeforeDataInit(userRepository, itemRepository);
    }
}
