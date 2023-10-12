package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.dto.CheckEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.dto.DuplicatedNicknameRequest;
import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    void certifyUser(final Long userId, final User user);
  
    void certifyEmail(final CertifyEmailRequest certifyEmailRequest);

    void checkEmailAuthenticationCode(final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest);

    void duplicatedCheckNickname(final DuplicatedNicknameRequest duplicatedNicknameRequest);

    void join(final JoinRequest joinRequest);
}
