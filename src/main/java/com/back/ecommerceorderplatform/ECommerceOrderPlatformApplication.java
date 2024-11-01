package com.back.ecommerceorderplatform;

import com.back.ecommerceorderplatform.item.repository.ItemRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
public class ECommerceOrderPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceOrderPlatformApplication.class, args);
    }

    @Bean
    public BeforeDataInit beforeDataInit(ItemRepository itemRepository) {
        return new BeforeDataInit(itemRepository);
    }

}
