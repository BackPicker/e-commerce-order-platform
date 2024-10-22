package com.hello.ecommerceorderplatform.order.service;


import com.hello.ecommerceorderplatform.order.domain.Order;
import com.hello.ecommerceorderplatform.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;


    public void save(Order order) {
        orderRepository.save(order);
    }
}