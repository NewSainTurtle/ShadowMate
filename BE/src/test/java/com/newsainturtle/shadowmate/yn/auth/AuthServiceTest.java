package com.newsainturtle.shadowmate.yn.auth;

import com.newsainturtle.shadowmate.auth.dto.CheckEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import com.newsainturtle.shadowmate.auth.dto.SendEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.entity.EmailAuthentication;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.auth.service.RedisService;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisService redisServiceImpl;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage message;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final String email = "test@test.com";
    private final User user = User.builder()
            .email("test@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @Nested
    class 이메일인증 {
        @Test
        void 실패_이메일중복() {
            //given
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .build();
            doReturn(user).when(userRepository).findByEmail(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authServiceImpl.sendEmailAuthenticationCode(sendEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_EMAIL);
        }

        @Test
        void 실패_이미인증된이메일사용() {
            //given
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .build();
            final EmailAuthentication emailAuth = EmailAuthentication.builder()
                    .code("code127")
                    .authStatus(true)
                    .build();

            doReturn(null).when(userRepository).findByEmail(email);
            doReturn(emailAuth).when(redisServiceImpl).getHashEmailData(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authServiceImpl.sendEmailAuthenticationCode(sendEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.ALREADY_AUTHENTICATED_EMAIL);
        }

        @Test
        void 성공_이메일중복아님_인증전() {
            //given
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .build();
            final EmailAuthentication emailAuth = EmailAuthentication.builder()
                    .code("code127")
                    .authStatus(false)
                    .build();

            doReturn(null).when(userRepository).findByEmail(email);
            doReturn(emailAuth).when(redisServiceImpl).getHashEmailData(email);
            doReturn(message).when(mailSender).createMimeMessage();

            //when
            authServiceImpl.sendEmailAuthenticationCode(sendEmailAuthenticationCodeRequest);

            //then

            //verify
            verify(userRepository, times(1)).findByEmail(any(String.class));
            verify(redisServiceImpl, times(1)).getHashEmailData(any(String.class));
            verify(mailSender, times(1)).createMimeMessage();
        }

        @Test
        void 성공_이메일중복아님() {
            //given
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .build();

            doReturn(null).when(userRepository).findByEmail(email);
            doReturn(null).when(redisServiceImpl).getHashEmailData(email);
            doReturn(message).when(mailSender).createMimeMessage();

            //when
            authServiceImpl.sendEmailAuthenticationCode(sendEmailAuthenticationCodeRequest);

            //then

            //verify
            verify(userRepository, times(1)).findByEmail(any(String.class));
            verify(redisServiceImpl, times(1)).getHashEmailData(any(String.class));
            verify(mailSender, times(1)).createMimeMessage();
        }

        @Test
        void 성공_코드생성() {
            //given

            //when
            final String code = authServiceImpl.createRandomCode();

            //then
            assertThat(code).hasSize(6);
        }
    }

    @Nested
    class 이메일인증코드확인 {
        @Test
        void 실패_이메일중복() {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code("code127")
                    .build();
            doReturn(user).when(userRepository).findByEmail(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authServiceImpl.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_EMAIL);

            //verify
            verify(userRepository, times(1)).findByEmail(any(String.class));
        }

        @Test
        void 실패_이메일인증_유효시간지남() {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code("code127")
                    .build();

            doReturn(null).when(userRepository).findByEmail(email);
            doReturn(null).when(redisServiceImpl).getHashEmailData(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authServiceImpl.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.EMAIL_AUTHENTICATION_TIME_OUT);

            //verify
            verify(userRepository, times(1)).findByEmail(any(String.class));
            verify(redisServiceImpl, times(1)).getHashEmailData(any(String.class));
        }

        @Test
        void 실패_이미인증된이메일사용() {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code("code127")
                    .build();
            final EmailAuthentication emailAuth = EmailAuthentication.builder()
                    .code("code127")
                    .authStatus(true)
                    .build();

            doReturn(null).when(userRepository).findByEmail(email);
            doReturn(emailAuth).when(redisServiceImpl).getHashEmailData(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authServiceImpl.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.ALREADY_AUTHENTICATED_EMAIL);

            //verify
            verify(userRepository, times(1)).findByEmail(any(String.class));
            verify(redisServiceImpl, times(1)).getHashEmailData(any(String.class));
        }

        @Test
        void 실패_이메일인증코드틀림() {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code("code1234")
                    .build();
            final EmailAuthentication emailAuth = EmailAuthentication.builder()
                    .code("code127")
                    .authStatus(false)
                    .build();

            doReturn(null).when(userRepository).findByEmail(email);
            doReturn(emailAuth).when(redisServiceImpl).getHashEmailData(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authServiceImpl.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.INVALID_EMAIL_AUTHENTICATION_CODE);

            //verify
            verify(userRepository, times(1)).findByEmail(any(String.class));
            verify(redisServiceImpl, times(1)).getHashEmailData(any(String.class));
        }

        @Test
        void 성공_이메일인증코드맞춤() {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code("code127")
                    .build();
            final EmailAuthentication emailAuth = EmailAuthentication.builder()
                    .code("code127")
                    .authStatus(false)
                    .build();

            doReturn(null).when(userRepository).findByEmail(email);
            doReturn(emailAuth).when(redisServiceImpl).getHashEmailData(email);

            //when
            authServiceImpl.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest);

            //then

            //verify
            verify(userRepository, times(1)).findByEmail(any(String.class));
            verify(redisServiceImpl, times(1)).getHashEmailData(any(String.class));
        }

    }

    @Nested
    class 회원가입 {
        @Test
        void 실패_이메일중복() {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(email)
                    .nickname("테스트중")
                    .password("test1234")
                    .build();
            doReturn(user).when(userRepository).findByEmail(joinRequest.getEmail());

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authServiceImpl.join(joinRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_EMAIL);

            //verify
            verify(userRepository, times(1)).findByEmail(joinRequest.getEmail());
            verify(userRepository, times(0)).save(any(User.class));
        }

        @Test
        void 성공_회원가입() {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(email)
                    .nickname("테스트중")
                    .password("test1234")
                    .build();

            doReturn(null).when(userRepository).findByEmail(joinRequest.getEmail());
            doReturn(user).when(userRepository).save(any(User.class));

            //when
            authServiceImpl.join(joinRequest);

            //then

            //verify
            verify(userRepository, times(1)).findByEmail(joinRequest.getEmail());
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    class 사용자인증 {
        private final User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build();

        @Test
        void 실패_없는사용자() {
            //given
            final Long userId = 2L;

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authServiceImpl.certifyUser(userId, user));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.UNREGISTERED_USER);
        }
        
    }

}
