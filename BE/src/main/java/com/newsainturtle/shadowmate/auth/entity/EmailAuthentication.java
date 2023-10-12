package com.newsainturtle.shadowmate.auth.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuthentication {

    private String code;

    private boolean authStatus;

    @Builder
    public EmailAuthentication(String code, boolean authStatus) {
        this.code = code;
        this.authStatus = authStatus;
    }
}
