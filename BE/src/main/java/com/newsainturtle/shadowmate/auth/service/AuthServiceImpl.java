package com.newsainturtle.shadowmate.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.newsainturtle.shadowmate.auth.dto.*;
import com.newsainturtle.shadowmate.auth.entity.EmailAuthentication;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Date;

import static com.newsainturtle.shadowmate.auth.constant.AuthConstant.*;
import static com.newsainturtle.shadowmate.config.constant.ConfigConstant.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisService redisServiceImpl;

    @Value("${shadowmate.jwt.header}")
    private String header;

    @Value("${shadowmate.jwt.prefix}")
    private String prefix;

    @Value("${shadowmate.jwt.secret}")
    private String secretKey;

    @Value("${shadowmate.jwt.access.expires}")
    private long accessExpires;

    @Value("${shadowmate.jwt.refresh.expires}")
    private long refreshExpires;

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
    public void sendEmailAuthenticationCode(final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest) {
        final String email = sendEmailAuthenticationCodeRequest.getEmail();
        checkDuplicatedEmail(email);

        final String code = createRandomCode();
        final EmailAuthentication emailAuth = EmailAuthentication.builder()
                .code(code)
                .authStatus(false)
                .build();

        final EmailAuthentication findEmailAuth = redisServiceImpl.getEmailData(email);
        if (findEmailAuth != null && findEmailAuth.isAuthStatus()) {
            throw new AuthException(AuthErrorResult.ALREADY_AUTHENTICATED_EMAIL);
        }
        redisServiceImpl.setEmailData(email, emailAuth, 3);

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

        final EmailAuthentication findEmailAuth = redisServiceImpl.getEmailData(email);
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
        redisServiceImpl.setEmailData(email, emailAuth, 10);
    }

    @Override
    public void duplicatedCheckNickname(final DuplicatedNicknameRequest duplicatedNicknameRequest) {
        final User user = userRepository.findByNickname(duplicatedNicknameRequest.getNickname());
        if (user != null) {
            throw new AuthException(AuthErrorResult.DUPLICATED_NICKNAME);
        }
        final Boolean checkNickname = redisServiceImpl.getNicknameData(duplicatedNicknameRequest.getNickname());
        if (checkNickname != null) {
            throw new AuthException(AuthErrorResult.DUPLICATED_NICKNAME);
        }
        redisServiceImpl.setNicknameData(duplicatedNicknameRequest.getNickname(), true, 10);
    }

    @Override
    public void deleteCheckNickname(DuplicatedNicknameRequest duplicatedNicknameRequest) {
        redisServiceImpl.deleteNicknameData(duplicatedNicknameRequest.getNickname());
    }

    @Override
    @Transactional
    public void join(final JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        checkDuplicatedEmail(email);

        final EmailAuthentication findEmailAuth = redisServiceImpl.getEmailData(email);
        if (findEmailAuth == null) {
            throw new AuthException(AuthErrorResult.EMAIL_AUTHENTICATION_TIME_OUT);
        } else if (!findEmailAuth.isAuthStatus()) {
            throw new AuthException(AuthErrorResult.UNAUTHENTICATED_EMAIL);
        }
        final Boolean getHashNickname = redisServiceImpl.getNicknameData(joinRequest.getNickname());
        if (getHashNickname == null || !getHashNickname) {
            throw new UserException(UserErrorResult.RETRY_NICKNAME);
        }

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
        redisServiceImpl.deleteEmailData(email);
        redisServiceImpl.deleteNicknameData(joinRequest.getNickname());
    }

    @Override
    @Transactional
    public void logout(final String key, final RemoveTokenRequest removeTokenRequest) {
        final String type = removeTokenRequest.getType();
        redisServiceImpl.deleteRefreshTokenData(removeTokenRequest.getUserId(), type);
        if (key != null) redisServiceImpl.deleteAutoLoginData(key);
    }

    @Override
    @Transactional
    public HttpHeaders changeToken(final String token, final Long userId, final ChangeTokenRequest changeTokenRequest) {
        final String type = changeTokenRequest.getType();
        final User user = userRepository.findByIdAndWithdrawalIsFalse(userId);

        if (user == null || !userId.toString().equals(JWT.decode(token.replace(prefix, "")).getClaim("id").toString())) {
            throw new AuthException(AuthErrorResult.UNREGISTERED_USER);
        }
        final String refreshToken = redisServiceImpl.getRefreshTokenData(userId, type);
        if (refreshToken == null || JWT.decode(refreshToken).getExpiresAt().before(new Date())) {
            throw new AuthException(AuthErrorResult.EXPIRED_REFRESH_TOKEN);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(header, new StringBuilder().append(prefix).append(createAccessToken(user)).toString());
        return headers;
    }

    @Override
    @Transactional
    public HttpHeaders checkAutoLogin(final String key) {
        final String userId = redisServiceImpl.getAutoLoginData(key);
        if (userId == null) {
            throw new AuthException(AuthErrorResult.UNREGISTERED_USER);
        }
        final User user = userRepository.findByIdAndWithdrawalIsFalse(Long.valueOf(userId));
        if (user == null) {
            throw new AuthException(AuthErrorResult.UNREGISTERED_USER);
        }
        HttpHeaders headers = new HttpHeaders();
        if (RequestContextHolder.getRequestAttributes() != null) {
            final String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
            createRefreshToken(user, sessionId);
            headers.set(header, new StringBuilder().append(prefix).append(createAccessToken(user)).toString());
            headers.set(KEY_ID, userId);
            headers.set(KEY_TYPE, sessionId);
        }
        redisServiceImpl.setAutoLoginData(key, userId);
        return headers;
    }

    private void createRefreshToken(final User user, final String type) {
        final String refreshToken = JWT.create()
                .withSubject("ShadowMate 리프레시 토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpires))
                .withClaim(KEY_ID, user.getId())
                .withClaim(TOKEN_KEY_EMAIL, user.getEmail())
                .withClaim(TOKEN_KEY_SOCIAL_TYPE, user.getSocialLogin().toString())
                .sign(Algorithm.HMAC512(secretKey));
        redisServiceImpl.setRefreshTokenData(user.getId(), type, refreshToken, (int) refreshExpires);
    }

    private String createAccessToken(final User user) {
        return JWT.create()
                .withSubject("ShadowMate 액세스 토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + accessExpires))
                .withClaim(KEY_ID, user.getId())
                .withClaim(TOKEN_KEY_EMAIL, user.getEmail())
                .withClaim(TOKEN_KEY_SOCIAL_TYPE, user.getSocialLogin().toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    private void checkDuplicatedEmail(final String email) {
        User user = userRepository.findByEmailAndSocialLogin(email, SocialType.BASIC);
        if (user != null) {
            throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL);
        }
    }

    public MimeMessage createMessage(final String email, final String code) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject(MESSAGE_SUBJECT);
        message.setText(MESSAGE_FRONT + code + MESSAGE_BACK, "utf-8", "html");
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
