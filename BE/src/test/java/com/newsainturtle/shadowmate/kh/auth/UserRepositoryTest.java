package com.newsainturtle.shadowmate.kh.auth;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
    }
}
