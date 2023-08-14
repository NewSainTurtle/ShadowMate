package com.newsainturtle.shadowmate.yn.auth;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Mock
    private UserRepository userRepository;

    private final String email = "test1234@naver.com";
    private final User user = User.builder()
            .email("test1234@naver.com")
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
        public void 성공_이메일중복아님() { //추가: 아니고_이메일인증번호전송함
            //given
            final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder()
                    .email(email)
                    .build();
            doReturn(null).when(userRepository).findByEmail(email);

            //when
            authServiceImpl.certifyEmail(certifyEmailRequest);

            //then
        }
    }

}
