package com.newsainturtle.shadowmate.kh.user;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    final User user = User.builder()
            .email("aa@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("닉네임임")
            .withdrawal(false)
            .profileImage("TestProfileURL")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();

    @Nested
    class 프로필TEST {

        @Test
        void 실패_프로필조회() {
            // given
            final Long userId = 1L;

            // when
            final Optional<User> userEntity = userRepository.findById(userId);

            // then
            assertThat(userEntity).isEmpty();
        }

        @Test
        void 성공_프로필조회() {
            // given
            final User userResponse = userRepository.save(user);
            final Long userId = userResponse.getId();

            // when
            final User userEntity = userRepository.findById(userId).get();

            // then
            assertThat(userEntity.getEmail()).isEqualTo(user.getEmail());
            assertThat(userEntity.getNickname()).isEqualTo(user.getNickname());
            assertThat(userEntity.getPassword()).isEqualTo(user.getPassword());
            assertThat(userEntity.getWithdrawal()).isEqualTo(user.getWithdrawal());
            assertThat(userEntity.getStatusMessage()).isEqualTo(user.getStatusMessage());
            assertThat(userEntity.getProfileImage()).isEqualTo(user.getProfileImage());

        }
    }
}
