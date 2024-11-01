package com.back.ecommerceorderplatform.user.repository;


import com.back.ecommerceorderplatform.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
