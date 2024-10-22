package com.hello.ecommerceorderplatform.order.service;

import com.hello.ecommerceorderplatform.order.domain.OrderItem;
import com.hello.ecommerceorderplatform.order.repository.OrderItemRepository;
import com.hello.ecommerceorderplatform.order.repository.OrderItemRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository     orderItemRepository;
    private final OrderItemRepositoryImpl orderItemRepositoryImpl;

    public void save(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }
}
