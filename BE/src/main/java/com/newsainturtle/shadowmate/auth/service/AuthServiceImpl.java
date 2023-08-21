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
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String serverEmail;

    @Override
    public void certifyEmail(CertifyEmailRequest certifyEmailRequest) {
        String email = certifyEmailRequest.getEmail();
        User user = userRepository.findByEmail(email);
      
      if (user != null) {
          throw new AuthException(AuthErrorResult.DUPLICATED_EMAIL);
      }

        // ** 구현 필요: 이메일 인증 번호 전송
        String code = createRandomCode();
        try {
            createMessage(email,code);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new AuthException(AuthErrorResult.FAIL_SEND_EMAIL);
        }
    }

    private MimeMessage createMessage(String email, String code) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        System.out.println("email = " + email);
        System.out.println("code = " + code);
        System.out.println(message);
        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("ShadowMate 회원가입 인증 메일이 도착했습니다.");
        String text = "";
        text += "<div style='margin:100px;'>";
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

    private String createRandomCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}
