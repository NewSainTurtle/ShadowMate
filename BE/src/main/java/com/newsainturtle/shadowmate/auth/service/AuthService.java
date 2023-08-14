package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    void certifyEmail(CertifyEmailRequest certifyEmailRequest);
}
