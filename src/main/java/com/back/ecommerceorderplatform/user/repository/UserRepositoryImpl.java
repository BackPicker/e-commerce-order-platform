package com.back.ecommerceorderplatform.user.repository;

import com.back.ecommerceorderplatform.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.back.ecommerceorderplatform.user.domain.QUser.user;


@Repository
public class UserRepositoryImpl {

    private final JPAQueryFactory factory;

    public UserRepositoryImpl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }

    public boolean existsUserByUsername(String username) {
        Long count = factory.select(user.count())
                .from(user)
                .where(user.username.eq(username))
                .fetchOne();
        return count != null && count > 0;
    }

    public boolean existsUserByEmail(String email) {
        Long count = factory.select(user.count())
                .from(user)
                .where(user.email.eq(email))
                .fetchOne();
        return count != null && count > 0;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(factory.selectFrom(user)
                .where(user.username.eq(username))
                .fetchOne());
    }
}
