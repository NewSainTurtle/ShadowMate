package com.newsainturtle.shadowmate.yn.auth;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage message;

    private final String email = "test@test.com";
    private final User user = User.builder()
            .email("test@test.com")
            .password("123456")
            .socialLogin(false)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @Nested
    class 이메일인증 {
        @Test
        public void 실패_이메일중복() {
            //given
            final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder()
                    .email(email)
                    .build();
            doReturn(user).when(userRepository).findByEmail(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authServiceImpl.certifyEmail(certifyEmailRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_EMAIL);
        }

        @Test
        public void 성공_이메일중복아님() {
            //given
            final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder()
                    .email(email)
                    .build();

            doReturn(null).when(userRepository).findByEmail(email);
            doReturn(message).when(mailSender).createMimeMessage();

            //when
            authServiceImpl.certifyEmail(certifyEmailRequest);
            //then
        }

        @Test
        public void 성공_코드생성(){
            //given

            //when
            final String code = authServiceImpl.createRandomCode();

            //then
            assertThat(code.length()).isEqualTo(6);
        }
    }

}
