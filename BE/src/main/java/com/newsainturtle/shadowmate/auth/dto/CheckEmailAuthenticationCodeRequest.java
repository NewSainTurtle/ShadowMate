package com.newsainturtle.shadowmate.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckEmailAuthenticationCodeRequest {

    @Email
    @NotNull
    private String email;

    @NotNull
    private String code;

    @Builder
    public CheckEmailAuthenticationCodeRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
