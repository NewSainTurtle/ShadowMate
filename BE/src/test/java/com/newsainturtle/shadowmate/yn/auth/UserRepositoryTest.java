package com.newsainturtle.shadowmate.yn.auth;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Nested
    class 이메일인증 {
        @Test
        public void 실패_이메일이_중복된_경우() {
            //given
            final String email = "test1234@naver.com";
            final User user = User.builder()
                    .email("test1234@naver.com")
                    .password("123456")
                    .nickname("거북이")
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .build();
            userRepository.save(user);

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
