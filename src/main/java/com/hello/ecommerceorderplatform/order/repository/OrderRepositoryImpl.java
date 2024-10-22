package com.hello.ecommerceorderplatform.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl {

    private final JPAQueryFactory factory;

    public OrderRepositoryImpl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }
}
