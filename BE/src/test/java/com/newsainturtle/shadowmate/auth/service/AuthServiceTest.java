package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.dto.CheckEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.dto.DuplicatedNicknameRequest;
import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import com.newsainturtle.shadowmate.auth.dto.SendEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.entity.EmailAuthentication;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private RedisService redisService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage message;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final String email = "yntest@shadowmate.com";
    private final String password = "yntest1234";
    private final String nickname = "거북이";
    private final SocialType socialType = SocialType.BASIC;
    private final PlannerAccessScope plannerAccessScope = PlannerAccessScope.PUBLIC;
    private final String code = "code127";
    private final User user = User.builder()
            .id(1L)
            .email(email)
            .password(password)
            .socialLogin(socialType)
            .nickname(nickname)
            .plannerAccessScope(plannerAccessScope)
            .withdrawal(false)
            .build();

    @Nested
    class 이메일인증 {
        final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                .email(email)
                .build();

        @Test
        void 실패_이메일중복() {
            //given
            doReturn(user).when(userRepository).findByEmailAndSocialLogin(email, socialType);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authService.sendEmailAuthenticationCode(sendEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_EMAIL);
        }

        @Test
        void 실패_이미인증된이메일사용() {
            //given
            final EmailAuthentication emailAuth = EmailAuthentication.builder()
                    .code(code)
                    .authStatus(true)
                    .build();

            doReturn(null).when(userRepository).findByEmailAndSocialLogin(email, socialType);
            doReturn(emailAuth).when(redisService).getEmailData(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authService.sendEmailAuthenticationCode(sendEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.ALREADY_AUTHENTICATED_EMAIL);
        }

        @Test
        void 성공_이메일중복아님_인증전() {
            //given
            final EmailAuthentication emailAuth = EmailAuthentication.builder()
                    .code(code)
                    .authStatus(false)
                    .build();

            doReturn(null).when(userRepository).findByEmailAndSocialLogin(email, SocialType.BASIC);
            doReturn(emailAuth).when(redisService).getEmailData(email);
            doReturn(message).when(mailSender).createMimeMessage();

            //when
            authService.sendEmailAuthenticationCode(sendEmailAuthenticationCodeRequest);

            //then

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
            verify(redisService, times(1)).getEmailData(any(String.class));
            verify(mailSender, times(1)).createMimeMessage();
        }

        @Test
        void 성공_이메일중복아님() {
            //given
            doReturn(null).when(userRepository).findByEmailAndSocialLogin(email, SocialType.BASIC);
            doReturn(null).when(redisService).getEmailData(email);
            doReturn(message).when(mailSender).createMimeMessage();

            //when
            authService.sendEmailAuthenticationCode(sendEmailAuthenticationCodeRequest);

            //then

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
            verify(redisService, times(1)).getEmailData(any(String.class));
            verify(mailSender, times(1)).createMimeMessage();
        }

        @Test
        void 성공_코드생성() {
            //given

            //when
            final String code = authService.createRandomCode();

            //then
            assertThat(code).hasSize(6);
        }
    }

    @Nested
    class 이메일인증코드확인 {
        final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                .email(email)
                .code(code)
                .build();

        @Test
        void 실패_이메일중복() {
            //given
            doReturn(user).when(userRepository).findByEmailAndSocialLogin(email, socialType);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authService.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_EMAIL);

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
        }

        @Test
        void 실패_이메일인증_유효시간지남() {
            //given
            doReturn(null).when(userRepository).findByEmailAndSocialLogin(email, socialType);
            doReturn(null).when(redisService).getEmailData(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authService.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.EMAIL_AUTHENTICATION_TIME_OUT);

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
            verify(redisService, times(1)).getEmailData(any(String.class));
        }

        @Test
        void 실패_이미인증된이메일사용() {
            //given
            final EmailAuthentication emailAuth = EmailAuthentication.builder()
                    .code(code)
                    .authStatus(true)
                    .build();

            doReturn(null).when(userRepository).findByEmailAndSocialLogin(email, socialType);
            doReturn(emailAuth).when(redisService).getEmailData(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authService.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.ALREADY_AUTHENTICATED_EMAIL);

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
            verify(redisService, times(1)).getEmailData(any(String.class));
        }

        @Test
        void 실패_이메일인증코드틀림() {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code("code1234")
                    .build();
            final EmailAuthentication emailAuth = EmailAuthentication.builder()
                    .code(code)
                    .authStatus(false)
                    .build();

            doReturn(null).when(userRepository).findByEmailAndSocialLogin(email, SocialType.BASIC);
            doReturn(emailAuth).when(redisService).getEmailData(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authService.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.INVALID_EMAIL_AUTHENTICATION_CODE);

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
            verify(redisService, times(1)).getEmailData(any(String.class));
        }

        @Test
        void 성공_이메일인증코드맞춤() {
            //given
            final EmailAuthentication emailAuth = EmailAuthentication.builder()
                    .code(code)
                    .authStatus(false)
                    .build();

            doReturn(null).when(userRepository).findByEmailAndSocialLogin(email, socialType);
            doReturn(emailAuth).when(redisService).getEmailData(email);

            //when
            authService.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest);

            //then

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
            verify(redisService, times(1)).getEmailData(any(String.class));
        }

    }

    @Nested
    class 회원가입 {
        final JoinRequest joinRequest = JoinRequest.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .build();
        final EmailAuthentication emailAuthentication = EmailAuthentication.builder()
                .authStatus(true)
                .code(code)
                .build();

        @Test
        void 실패_회원가입_닉네임검사_false() {
            //given
            doReturn(false).when(redisService).getNicknameData(any());
            doReturn(emailAuthentication).when(redisService).getEmailData(any());

            //when
            final UserException result = assertThrows(UserException.class, () -> authService.join(joinRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.RETRY_NICKNAME);
        }

        @Test
        void 실패_회원가입_닉네임검사_NULL() {
            //given
            doReturn(null).when(redisService).getNicknameData(any());
            doReturn(emailAuthentication).when(redisService).getEmailData(any());

            //when
            final UserException result = assertThrows(UserException.class, () -> authService.join(joinRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.RETRY_NICKNAME);
        }

        @Test
        void 실패_이메일중복() {
            //given
            doReturn(user).when(userRepository).findByEmailAndSocialLogin(email, socialType);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authService.join(joinRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_EMAIL);

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
            verify(userRepository, times(0)).save(any(User.class));
        }

        @Test
        void 실패_임시이메일_타임아웃() {
            //given
            doReturn(null).when(userRepository).findByEmailAndSocialLogin(email, socialType);
            doReturn(null).when(redisService).getEmailData(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authService.join(joinRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.EMAIL_AUTHENTICATION_TIME_OUT);

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
            verify(redisService, times(1)).getEmailData(email);
        }

        @Test
        void 실패_임시이메일_인증안됨() {
            //given
            final EmailAuthentication emailAuthentication = EmailAuthentication.builder()
                    .authStatus(false)
                    .code(code)
                    .build();

            doReturn(null).when(userRepository).findByEmailAndSocialLogin(email, socialType);
            doReturn(emailAuthentication).when(redisService).getEmailData(email);

            //when
            final AuthException result = assertThrows(AuthException.class, () -> authService.join(joinRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.UNAUTHENTICATED_EMAIL);

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
            verify(redisService, times(1)).getEmailData(email);
        }

        @Test
        void 성공_회원가입() {
            //given
            doReturn(null).when(userRepository).findByEmailAndSocialLogin(email, socialType);
            doReturn(true).when(redisService).getNicknameData(nickname);
            doReturn(emailAuthentication).when(redisService).getEmailData(email);
            doReturn(user).when(userRepository).save(any(User.class));

            //when
            authService.join(joinRequest);

            //then

            //verify
            verify(userRepository, times(1)).findByEmailAndSocialLogin(any(String.class), any(SocialType.class));
            verify(redisService, times(1)).getNicknameData(nickname);
            verify(redisService, times(1)).getEmailData(email);
            verify(userRepository, times(1)).save(any(User.class));
            verify(redisService, times(1)).deleteEmailData(email);
            verify(redisService, times(1)).deleteNicknameData(nickname);
        }
    }

    @Nested
    class 닉네임인증 {
        final DuplicatedNicknameRequest duplicatedNicknameRequest = DuplicatedNicknameRequest.builder().nickname(nickname).build();

        @Test
        void 실패_닉네임중복_DB사용중() {
            // given
            doReturn(true).when(userRepository).existsByNickname(duplicatedNicknameRequest.getNickname());

            // when
            final AuthException authException = assertThrows(AuthException.class, () -> authService.duplicatedCheckNickname(duplicatedNicknameRequest));

            // then
            assertThat(authException.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_NICKNAME);
        }

        @Test
        void 실패_닉네임중복_Redis사용중() {
            // given
            doReturn(false).when(userRepository).existsByNickname(duplicatedNicknameRequest.getNickname());
            doReturn(true).when(redisService).getNicknameData(duplicatedNicknameRequest.getNickname());

            // when
            final AuthException authException = assertThrows(AuthException.class, () -> authService.duplicatedCheckNickname(duplicatedNicknameRequest));

            // then
            assertThat(authException.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_NICKNAME);
        }

        @Test
        void 성공_닉네임중복아님() {
            // given
            doReturn(false).when(userRepository).existsByNickname(duplicatedNicknameRequest.getNickname());
            doReturn(null).when(redisService).getNicknameData(duplicatedNicknameRequest.getNickname());

            // when
            authService.duplicatedCheckNickname(duplicatedNicknameRequest);

            // then
            verify(userRepository, times(1)).existsByNickname(any(String.class));
            verify(redisService, times(1)).getNicknameData(any(String.class));
            verify(redisService, times(1)).setNicknameData(any(String.class), any(boolean.class), any(int.class));

        }

        @Test
        void 성공_닉네임중복검증삭제() {
            // given

            // when
            authService.deleteCheckNickname(duplicatedNicknameRequest);

            // then
            verify(redisService, times(1)).deleteNicknameData(any());
        }
    }
}
