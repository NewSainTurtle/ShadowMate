package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.dto.*;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    void certifyUser(final Long userId, final User user);
    void sendEmailAuthenticationCode(final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest);
    void checkEmailAuthenticationCode(final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest);
    void duplicatedCheckNickname(final DuplicatedNicknameRequest duplicatedNicknameRequest);
    void deleteCheckNickname(final DuplicatedNicknameRequest duplicatedNicknameRequest);
    void join(final JoinRequest joinRequest);
    HttpHeaders changeToken(final String token, final Long userId, final ChangeTokenRequest changeTokenRequest);
    HttpHeaders checkAutoLogin(final String key);

}
