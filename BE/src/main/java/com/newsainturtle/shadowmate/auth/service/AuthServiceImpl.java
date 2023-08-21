package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String serverEmail;

    @Override
    public void certifyEmail(CertifyEmailRequest certifyEmailRequest) {
        String email = certifyEmailRequest.getEmail();
        User user = userRepository.findByEmail(email);

        if (user != null) {
            throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL);
        }

        try {
            mailSender.send(createMessage(email));
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new AuthException(AuthErrorResult.FAIL_SEND_EMAIL);
        }
    }

    public MimeMessage createMessage(String email) throws MessagingException, UnsupportedEncodingException {
        String code = createRandomCode();
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
