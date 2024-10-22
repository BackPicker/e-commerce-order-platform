package com.hello.ecommerceorderplatform.order.service;


import com.hello.ecommerceorderplatform.order.domain.Order;
import com.hello.ecommerceorderplatform.order.repository.OrderRepository;
import com.hello.ecommerceorderplatform.order.repository.OrderRepositoryImpl;
import com.hello.ecommerceorderplatform.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderRepositoryImpl orderRepositoryImpl;


    public void save(Order order) {
        orderRepository.save(order);
    }

    public void getOrders(User user) {
        orderRepositoryImpl.findByUserIdOrderByCreateDateDesc(user.getId());


    }
}