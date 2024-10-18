package com.hello.ecommerceorderplatform.item.domain;


import com.hello.ecommerceorderplatform.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 위시리스트 Id

    @OneToMany(mappedBy = "wishList", fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "users_id")
    private User user;  // 위시리스트 회원

    @CreatedDate
    private LocalDateTime createdAt;    // 위시리스트 생성일
}
