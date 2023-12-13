package com.newsainturtle.shadowmate.auth.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class SendEmailAuthenticationCodeRequest {
    @Email
    @NotNull
    private String email;

    @Builder
    public SendEmailAuthenticationCodeRequest(String email) {
        this.email = email;
    }
}
