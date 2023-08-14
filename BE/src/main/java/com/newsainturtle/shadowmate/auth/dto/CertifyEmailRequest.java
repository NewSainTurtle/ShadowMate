package com.newsainturtle.shadowmate.auth.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Builder
@AllArgsConstructor
public class CertifyEmailRequest {
    @Email
    private String email;
}
