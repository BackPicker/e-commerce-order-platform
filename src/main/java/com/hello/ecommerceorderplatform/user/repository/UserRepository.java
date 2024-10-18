package com.hello.ecommerceorderplatform.user.repository;


import com.hello.ecommerceorderplatform.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
