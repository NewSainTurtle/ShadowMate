package com.newsainturtle.shadowmate.kh.auth;

import com.newsainturtle.shadowmate.auth.dto.DuplicatedNicknameRequest;
import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import com.newsainturtle.shadowmate.auth.entity.EmailAuthentication;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.auth.service.RedisService;
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

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

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

    private final String email = "test1234@naver.com";
    final User user = User.builder()
            .email("test1234@naver.com")
            .password("12345")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .withdrawal(false)
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();

    @Nested
    class 이메일인증 {

        @Test
        void 랜덤숫자생성() {
            for(int i=0; i<10; i++) {
                String code = createRandomCode();
                assertThat(code).hasSize(6);
            }
        }

        private String createRandomCode() {
            return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        }

    }
    @Nested
    class 회원가입 {

        @Test
        void 실패_회원가입_닉네임검사_false() {
            //given
            final JoinRequest joinRequest =
                    JoinRequest.builder()
                            .email("test@test.com")
                            .password("123456")
                            .nickname("테스트닉네임임")
                            .build();
            final String code = "testCode";
            final EmailAuthentication emailAuthentication = EmailAuthentication.builder()
                    .authStatus(true)
                    .code(code)
                    .build();
            doReturn(false).when(redisService).getHashNicknameData(any());
            doReturn(emailAuthentication).when(redisService).getHashEmailData(any());

            //when
            final UserException result = assertThrows(UserException.class, () -> authServiceImpl.join(joinRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.RETRY_NICKNAME);
        }

        @Test
        void 실패_회원가입_닉네임검사_NULL() {
            //given
            final JoinRequest joinRequest =
                    JoinRequest.builder()
                            .email("test@test.com")
                            .password("123456")
                            .nickname("테스트닉네임임")
                            .build();
            final String code = "testCode";
            final EmailAuthentication emailAuthentication = EmailAuthentication.builder()
                    .authStatus(true)
                    .code(code)
                    .build();
            doReturn(null).when(redisService).getHashNicknameData(any());
            doReturn(emailAuthentication).when(redisService).getHashEmailData(any());

            //when
            final UserException result = assertThrows(UserException.class, () -> authServiceImpl.join(joinRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.RETRY_NICKNAME);
        }
        @Test
        void 성공_회원가입() {
            //given
            final JoinRequest joinRequest =
                    JoinRequest.builder()
                            .email("test@test.com")
                            .password("123456")
                            .nickname("테스트닉네임임")
                            .build();
            final User userEntity =
                    User.builder()
                            .email(joinRequest.getEmail())
                            .password(joinRequest.getPassword())
                            .nickname(joinRequest.getNickname())
                            .socialLogin(SocialType.BASIC)
                            .plannerAccessScope(PlannerAccessScope.PUBLIC)
                            .withdrawal(false)
                            .build();
            final String code = "testCode";
            final EmailAuthentication emailAuthentication = EmailAuthentication.builder()
                    .authStatus(true)
                    .code(code)
                    .build();
            doReturn(true).when(redisService).getHashNicknameData(any());
            doReturn(emailAuthentication).when(redisService).getHashEmailData(any());
            doReturn(userEntity).when(userRepository).save(any(User.class));

            //when
            authServiceImpl.join(joinRequest);

            //then
            assertThat(userEntity.getEmail()).isEqualTo(joinRequest.getEmail());
            assertThat(userEntity.getNickname()).isEqualTo(joinRequest.getNickname());
            verify(redisService, times(1)).deleteNicknameData(any());
        }
    }

    @Nested
    class 닉네임인증 {
        String nickname = "거북이닉네임사용";
        final DuplicatedNicknameRequest duplicatedNicknameRequest = DuplicatedNicknameRequest.builder().nickname(nickname).build();

        @Test
        void 실패_닉네임중복_DB사용중() {
            // given
            doReturn(user).when(userRepository).findByNickname(nickname);

            // when
            final AuthException authException = assertThrows(AuthException.class,() -> authServiceImpl.duplicatedCheckNickname(duplicatedNicknameRequest));

            // then
            assertThat(authException.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_NICKNAME);
        }

        @Test
        void 실패_닉네임중복_Redis사용중() {
            // given
            doReturn(null).when(userRepository).findByNickname(nickname);
            doReturn(true).when(redisService).getHashNicknameData(duplicatedNicknameRequest.getNickname());

            // when
            final AuthException authException = assertThrows(AuthException.class,() -> authServiceImpl.duplicatedCheckNickname(duplicatedNicknameRequest));

            // then
            assertThat(authException.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_NICKNAME);
        }

        @Test
        void 성공_닉네임중복아님() {
            // given
            doReturn(null).when(userRepository).findByNickname(duplicatedNicknameRequest.getNickname());
            doReturn(null).when(redisService).getHashNicknameData(duplicatedNicknameRequest.getNickname());

            // when
            authServiceImpl.duplicatedCheckNickname(duplicatedNicknameRequest);

            // then
            verify(redisService, times(1)).setHashNicknameData(any(String.class), any(boolean.class), any(int.class));

        }
    }
}
