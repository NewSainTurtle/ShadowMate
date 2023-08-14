package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;

public interface AuthService {
    void certifyEmail(CertifyEmailRequest certifyEmailRequest);
}
