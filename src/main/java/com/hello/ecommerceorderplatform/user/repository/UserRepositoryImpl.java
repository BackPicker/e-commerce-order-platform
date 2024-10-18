package com.hello.ecommerceorderplatform.user.repository;

import com.hello.ecommerceorderplatform.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.hello.ecommerceorderplatform.user.domain.QUser.user;

@Repository
public class UserRepositoryImpl {

    private final JPAQueryFactory factory;

    public UserRepositoryImpl(EntityManager entityManager) {
        this.factory = new JPAQueryFactory(entityManager);
    }


    public boolean existsUserByUsername(String username) {

        long count = factory.select(user)
                .from(user)
                .where(user.username.eq(username))
                .fetch()
                .size();
        return count > 0;
    }

    public boolean existsUserByEmail(String email) {
        long count = factory.selectFrom(user)
                .where(user.email.eq(email))
                .fetch()
                .size();
        return count > 0;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(factory.selectFrom(user)
                .where(user.username.eq(username))
                .fetchOne());
    }


}
