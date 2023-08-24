package com.newsainturtle.shadowmate.kh.auth;

import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    //이메일 인증 테스트(+중복검사)
    // 1. 이메일 중복검사
    // - 이메일 중복된 경우
    // - 이메일 중복되지 않은 경우

    @Autowired
    private UserRepository userRepository;


    @Nested
    class 이메일인증 {

        @Test
        public void 실패_이메일이_중복된_경우() {
            //given
            final User user = User.builder()
                    .email("test1234@naver.com")
                    .password("12345")
                    .socialLogin(false)
                    .nickname("거북이")
                    .withdrawal(false)
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .build();
            userRepository.save(user);

            final String email = "test1234@naver.com";

            //when
            final User result = userRepository.findByEmail(email);

            //then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(user);
        }

        @Test
        public void 성공_이메일이_중복되지_않은_경우() {
            //given
            final String email = "test1234@naver.com";

            //when
            final User result = userRepository.findByEmail(email);

            //then
            assertThat(result).isNull();
        }

        @Test
        void 성공_회원가입() {
            // given
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
                            .plannerAccessScope(PlannerAccessScope.PUBLIC)
                            .build();
            // when
            final User user = userRepository.save(userEntity);

            // then
            assertThat(user.getEmail()).isEqualTo(joinRequest.getEmail());
            assertThat(user.getNickname()).isEqualTo(joinRequest.getNickname());
            assertThat(new BCryptPasswordEncoder().matches(
                    joinRequest.getPassword(), user.getPassword())).isTrue();
        }
    }
}
