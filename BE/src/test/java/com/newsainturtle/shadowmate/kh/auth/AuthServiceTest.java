package com.newsainturtle.shadowmate.kh.auth;

import com.newsainturtle.shadowmate.auth.dto.SendEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.dto.DuplicatedNicknameRequest;
import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender emailSender;

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
        public void 실패_이메일중복() {
            //given
            doReturn(user).when(userRepository).findByEmail(email);
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder().email("test1234@naver.com").build();

            //when
            final AuthException result = Assertions.assertThrows(AuthException.class, () -> authServiceImpl.sendEmailAuthenticationCode(sendEmailAuthenticationCodeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_EMAIL);
        }

        @Test
        public void 랜덤숫자생성() {
            for(int i=0; i<10; i++) {
                String code = createRandomCode();
                assertThat(code.length()).isEqualTo(6);
            }
        }

        private String createRandomCode() {
            return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        }

    }
    @Nested
    class 회원가입 {
        @Test
        void 성공_회원가입() {
            //given
            final JoinRequest joinRequest =
                    JoinRequest.builder()
                            .email("test@test.com")
                            .password("1234")
                            .nickname("닉")
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
            doReturn(userEntity).when(userRepository).save(any(User.class));

            //when
            authServiceImpl.join(joinRequest);

            //then
            assertThat(userEntity.getEmail()).isEqualTo(joinRequest.getEmail());
            assertThat(userEntity.getNickname()).isEqualTo(joinRequest.getNickname());
        }
    }

    @Nested
    class 닉네임인증 {

        @Test
        @DisplayName("닉네임이 중복된 경우")
        void 실패_닉네임중복() {
            // given
            String nickname = "거북이";
            doReturn(user).when(userRepository).findByNickname(nickname);

            // when
            User userEntity = userRepository.findByNickname(nickname);

            // then
            assertThat(userEntity.getNickname()).isEqualTo(user.getNickname());

        }

        @Test
        @DisplayName("닉네임이 중복되지 않은 경우")
        void 성공_닉네임중복안됨() {
            // given
            final String nickname = "거북이";
            doReturn(user).when(userRepository).findByNickname(nickname);
            DuplicatedNicknameRequest duplicatedNicknameRequest = DuplicatedNicknameRequest.builder().nickname(nickname).build();

            // when
            final AuthException authException = Assertions.assertThrows(AuthException.class,() -> authServiceImpl.duplicatedCheckNickname(duplicatedNicknameRequest));

            // then
            assertThat(authException.getErrorResult()).isEqualTo(AuthErrorResult.DUPLICATED_NICKNAME);
        }
    }
}
