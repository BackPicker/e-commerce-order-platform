package com.back.ecommerceorderplatform.delivery.service;

import com.back.ecommerceorderplatform.delivery.domain.Delivery;
import com.back.ecommerceorderplatform.delivery.repository.DeliveryRepository;
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
