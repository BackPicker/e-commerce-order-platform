package com.hello.ecommerceorderplatform.delivery.service;

import com.hello.ecommerceorderplatform.delivery.domain.Delivery;
import com.hello.ecommerceorderplatform.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;


    public void save(Delivery delivery) {
        deliveryRepository.save(delivery);
    }
}
