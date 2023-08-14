package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;

    @Override
    public void certifyEmail(CertifyEmailRequest certifyEmailRequest) {
        String email = certifyEmailRequest.getEmail();
        User user = userRepository.findByEmail(email);
        if(user!=null) {
            throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL);
        }

        // **구현 필요: 이메일 인증 전송**
    }
}
