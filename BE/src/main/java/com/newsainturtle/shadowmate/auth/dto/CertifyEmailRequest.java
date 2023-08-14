package com.newsainturtle.shadowmate.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
@Builder
@AllArgsConstructor
public class CertifyEmailRequest {

    @Email
    private String email;
}
