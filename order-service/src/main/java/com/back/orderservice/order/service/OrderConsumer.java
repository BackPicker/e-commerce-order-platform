package com.back.orderservice.order.service;

import com.back.orderservice.order.domain.Order;
import com.back.orderservice.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

    @Service
    public class OrderConsumer {

        @Autowired
        private OrderRepository orderRepository;

        // "order_create" 토픽에서 메시지 수신
        @KafkaListener(topics = "order_create", groupId = "orderGroup")
        public void consumeOrder(Order order) {
            // 데이터베이스에 주문 저장
            orderRepository.save(order);
        }
    }