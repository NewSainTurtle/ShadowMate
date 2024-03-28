package com.newsainturtle.shadowmate.auth.repository;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final String email = "yntest@shadowmate.com";
    private final String nickname = "거북이";
    private final String password = "yntest1234";
    private final SocialType socialType = SocialType.BASIC;
    private final User user = User.builder()
            .email(email)
            .password(password)
            .socialLogin(socialType)
            .nickname(nickname)
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @Nested
    class 회원가입 {

        @Test
        void 성공() {
            // given
            final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            final User user = User.builder()
                    .email(email)
                    .password(bCryptPasswordEncoder.encode(password))
                    .socialLogin(socialType)
                    .nickname(nickname)
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .withdrawal(false)
                    .build();

            // when
            final User saveUser = userRepository.save(user);

            // then
            assertThat(saveUser.getEmail()).isEqualTo(email);
            assertThat(saveUser.getNickname()).isEqualTo(nickname);
            assertThat(bCryptPasswordEncoder.matches(password, saveUser.getPassword())).isTrue();
        }

        @Test
        void 사용자등록() {
            //given
            userRepository.save(user);

            //when
            final User findUser = userRepository.findByEmailAndSocialLogin(user.getEmail(), socialType);

            //then
            assertThat(findUser).isNotNull();
            assertThat(findUser.getEmail()).isEqualTo(user.getEmail());
            assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
            assertThat(findUser.getSocialLogin()).isEqualTo(user.getSocialLogin());
            assertThat(findUser.getNickname()).isEqualTo(user.getNickname());
            assertThat(findUser.getPlannerAccessScope()).isEqualTo(user.getPlannerAccessScope());
            assertThat(findUser.getWithdrawal()).isEqualTo(user.getWithdrawal());
        }
    }

    @Nested
    class 닉네임인증 {
        @Test
        @DisplayName("닉네임이 중복된 경우")
        void 실패_닉네임중복() {
            //given
            userRepository.save(user);

            //when
            final boolean existsUser = userRepository.existsByNickname(user.getNickname());

            //then
            assertThat(existsUser).isTrue();
        }

        @Test
        @DisplayName("닉네임이 중복되지 않은 경우")
        void 성공_닉네임중복이아님() {
            // given

            // when
            final boolean existsUser = userRepository.existsByNickname(user.getNickname());

            // then
            assertThat(existsUser).isFalse();
        }
    }

    @Nested
    class 이메일인증 {
        @Test
        void 실패_이메일이_중복된_경우() {
            //given
            userRepository.save(user);

            //when
            final User result = userRepository.findByEmailAndSocialLogin(email, socialType);

            //then
            assertThat(result).isNotNull()
                    .isEqualTo(user);
        }

        @Test
        void 성공_이메일이_중복되지_않은_경우() {
            //given

            //when
            final User result = userRepository.findByEmailAndSocialLogin(email, socialType);

            //then
            assertThat(result).isNull();
        }
    }
}
