package com.back.ecommerceorderplatform.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {


    @Column(nullable = false)
    private String city; // 도시


    @Column(nullable = false)
    private String street; // 도로명 주소


    @Column(nullable = false)
    private String zipCode; // 우편번호

    public Address(String city,
                   String street,
                   String zipCode) {
        this.city    = city;
        this.street  = street;
        this.zipCode = zipCode;
    }
}