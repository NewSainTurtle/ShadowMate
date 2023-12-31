package com.newsainturtle.shadowmate.yn.auth;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final String email = "yntest@shadowmate.com";
    private final SocialType socialType = SocialType.BASIC;
    private final User user = User.builder()
            .email(email)
            .password("yntest1234")
            .socialLogin(socialType)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

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

    @Nested
    class 회원가입 {
        @Test
        void 사용자등록() {
            //given
            userRepository.save(user);

            //when
            final User result = userRepository.findByEmailAndSocialLogin(user.getEmail(), socialType);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(user.getEmail());
            assertThat(result.getPassword()).isEqualTo(user.getPassword());
            assertThat(result.getSocialLogin()).isEqualTo(user.getSocialLogin());
            assertThat(result.getNickname()).isEqualTo(user.getNickname());
            assertThat(result.getPlannerAccessScope()).isEqualTo(user.getPlannerAccessScope());
            assertThat(result.getWithdrawal()).isEqualTo(user.getWithdrawal());
        }
    }


}
