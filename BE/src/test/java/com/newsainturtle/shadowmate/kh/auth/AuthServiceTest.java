package com.newsainturtle.shadowmate.kh.auth;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Mock
    private UserRepository userRepository;

    private final String email = "test1234@naver.com";
    private final User user = User.builder()
            .email("test1234@naver.com")
            .password("12345")
            .socialLogin(false)
            .nickname("거북이")
            .withdrawal(false)
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();

    @Nested
    class 이메일인증 {

        @Test
        public void 실패_이메일중복() {
            //given
            doReturn(user).when(userRepository).findByEmail(email);
            final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder().email("test1234@naver.com").build();

            //when
            final AuthException result = Assertions.assertThrows(AuthException.class, () -> authServiceImpl.certifyEmail(certifyEmailRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_EMAIL);
        }

        @Test
        public void 성공_이메일중복아님() {
            //given
            doReturn(null).when(userRepository).findByEmail(email);
            final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder().email("test1234@naver.com").build();

            //when
            authServiceImpl.certifyEmail(certifyEmailRequest);

            //then

        }
    }
}
