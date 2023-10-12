package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.dto.CheckEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.dto.DuplicatedNicknameRequest;
import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import com.newsainturtle.shadowmate.auth.entity.EmailAuthentication;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisService redisServiceImpl;

    @Value("${spring.mail.username}")
    private String serverEmail;

    @Override
    public void certifyUser(Long userId, User user) {
        if (!userId.equals(user.getId())) {
            throw new AuthException(AuthErrorResult.UNREGISTERED_USER);
        }
    }

    @Override
    @Transactional
    public void certifyEmail(final CertifyEmailRequest certifyEmailRequest) {
        final String email = certifyEmailRequest.getEmail();
        checkDuplicatedEmail(email);

        final String code = createRandomCode();
        final EmailAuthentication emailAuth = EmailAuthentication.builder()
                .code(code)
                .authStatus(false)
                .build();

        final EmailAuthentication findEmailAuth = redisServiceImpl.getHashEmailData(email);
        if (findEmailAuth != null && findEmailAuth.isAuthStatus()) {
            throw new AuthException(AuthErrorResult.ALREADY_AUTHENTICATED_EMAIL);
        }
        redisServiceImpl.setHashEmailData(email, emailAuth);

        try {
            mailSender.send(createMessage(email, code));
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new AuthException(AuthErrorResult.FAIL_SEND_EMAIL);
        }
    }

    @Override
    @Transactional
    public void checkEmailAuthenticationCode(final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest) {
        final String email = checkEmailAuthenticationCodeRequest.getEmail();
        checkDuplicatedEmail(email);

        final EmailAuthentication findEmailAuth = redisServiceImpl.getHashEmailData(email);
        if (findEmailAuth == null) {
            throw new AuthException(AuthErrorResult.EMAIL_AUTHENTICATION_TIME_OUT);
        } else if (findEmailAuth.isAuthStatus()) {
            throw new AuthException(AuthErrorResult.ALREADY_AUTHENTICATED_EMAIL);
        }

        if (!findEmailAuth.getCode().equals(checkEmailAuthenticationCodeRequest.getCode())) {
            throw new AuthException(AuthErrorResult.INVALID_EMAIL_AUTHENTICATION_CODE);
        }

        final EmailAuthentication emailAuth = EmailAuthentication.builder()
                .code(findEmailAuth.getCode())
                .authStatus(true)
                .build();
        redisServiceImpl.setHashEmailData(email, emailAuth);
    }

    @Override
    public void duplicatedCheckNickname(final DuplicatedNicknameRequest duplicatedNicknameRequest) {
        User user = userRepository.findByNickname(duplicatedNicknameRequest.getNickname());
        if (user != null) {
            throw new AuthException(AuthErrorResult.DUPLICATED_NICKNAME);
        }
    }

    @Transactional
    @Override
    public void join(final JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        checkDuplicatedEmail(email);

        User userEntity =
                User.builder()
                        .email(email)
                        .password(bCryptPasswordEncoder.encode(joinRequest.getPassword()))
                        .nickname(joinRequest.getNickname())
                        .socialLogin(SocialType.BASIC)
                        .plannerAccessScope(PlannerAccessScope.PUBLIC)
                        .withdrawal(false)
                        .build();
        userRepository.save(userEntity);
    }

    private void checkDuplicatedEmail(final String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL);
        }
    }

    public MimeMessage createMessage(final String email, final String code) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("ShadowMate 회원가입 인증 코드");
        String text = "";
        text += "<div style='margin:10;'>";
        text += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        text += "<h3 style='color:blue;'>회원가입 코드입니다.</h3>";
        text += "<div style='font-size:130%'>";
        text += "CODE : <strong>";
        text += code;
        text += "</strong><div><br/> ";
        text += "</div>";
        message.setText(text, "utf-8", "html");
        message.setFrom(new InternetAddress(serverEmail, "ShadowMate"));
        return message;
    }

    public String createRandomCode() {
        final String temp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 6; i++) {
            sb.append(temp.charAt(random.nextInt(temp.length())));
        }

        return sb.toString();
    }

}
