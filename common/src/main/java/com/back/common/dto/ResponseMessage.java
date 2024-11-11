package com.back.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class ResponseMessage {

    private Object data;
    private int    statusCode;
    private String resultMessage;

    public ResponseMessage(int statusCode,
                           String resultMessage) {
        this.statusCode    = statusCode;
        this.resultMessage = resultMessage;
    }

    private String detailMessage;

}
