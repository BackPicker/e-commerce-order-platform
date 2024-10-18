package com.hello.ecommerceorderplatform.user.repository;


import com.hello.ecommerceorderplatform.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);

    Optional<User> findByUsername(String username);
}
