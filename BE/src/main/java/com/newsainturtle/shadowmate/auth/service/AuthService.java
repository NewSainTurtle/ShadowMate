package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.dto.DuplicatedNicknameRequest;
import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
  
    void certifyEmail(final CertifyEmailRequest certifyEmailRequest);

    void duplicatedCheckNickname(final DuplicatedNicknameRequest duplicatedNicknameRequest);

    void join(final JoinRequest joinRequest);
}
