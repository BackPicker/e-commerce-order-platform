package com.back.ecommerceorderplatform.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateOrderResponseDto {
    private String  message; // 성공 메시지
    private Integer statusCode; // HTTP 상태 코드

    public CreateOrderResponseDto(String message,
                                  Integer statusCode) {
        this.message    = message;
        this.statusCode = statusCode;
    }
}
