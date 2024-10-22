package com.hello.ecommerceorderplatform.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateOrderResponseDto {
    private String  message;
    private Integer statusCode;

    public CreateOrderResponseDto(String message, Integer statusCode) {
        this.message    = message;
        this.statusCode = statusCode;
    }
}
