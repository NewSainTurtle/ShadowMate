package com.newsainturtle.shadowmate.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@SuperBuilder
@Getter
@NoArgsConstructor
public class BaseResponse {
    private String message;
    private Object data;

    public static BaseResponse from(String message) {
        return builder().message(message).build();
    }

    public static BaseResponse from(String message, Object data) {
        return builder().message(message).data(data).build();
    }
}