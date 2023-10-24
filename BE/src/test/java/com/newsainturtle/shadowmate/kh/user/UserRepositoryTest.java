package com.newsainturtle.shadowmate.kh.user;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    final User user = User.builder()
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .withdrawal(false)
            .profileImage("TestProfileURL")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();
    Long userId;


    @BeforeEach
    public void init() {
        userId = userRepository.save(user).getId();
    }

    @Nested
    class 프로필TEST {

        @Test
        void 실패_프로필조회() {
            // given
            userId += 1L;

            // when
            final Optional<User> userEntity = userRepository.findById(userId);

            // then
            assertThat(userEntity).isEmpty();
        }

        @Test
        void 성공_프로필조회() {
            // given

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

        @Test
        void 성공_프로필사진수정() {
            //given
            final String newProfileImage = "NewProfileImage";

            //when
            Optional<User> oldUser = userRepository.findById(userId);
            oldUser.ifPresent(user -> user.updateProfileImage(newProfileImage));

            final User result = userRepository.findByNickname(user.getNickname());

            //then
            assertThat(result.getProfileImage()).isEqualTo(newProfileImage);

        }

    }

    @Nested
    class 회원TEST {

        @Test
        void 실패_회원없음() {
            // given

            // when
            final User result = userRepository.findByNickname("거북이234567");

            // then
            assertThat(result).isNull();
        }

        @Test
        void 성공_회원검색() {
            // given

            // when
            final User result = userRepository.findByNickname(user.getNickname());

            // then
            assertThat(result.getNickname()).isEqualTo(user.getNickname());
        }

        @Test
        void 성공_회원탈퇴() {
            //given
            final User deleteUser = User.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .socialLogin(user.getSocialLogin())
                    .profileImage(user.getProfileImage())
                    .nickname(user.getNickname())
                    .statusMessage(user.getStatusMessage())
                    .withdrawal(true)
                    .plannerAccessScope(user.getPlannerAccessScope())
                    .createTime(user.getCreateTime())
                    .updateTime(user.getUpdateTime())
                    .deleteTime(LocalDateTime.now())
                    .build();

            //when
            userRepository.save(deleteUser);
            final User result = userRepository.findByNickname(user.getNickname());

            //then
            assertThat(result.getWithdrawal()).isTrue();
            assertThat(result.getDeleteTime()).isNotNull();
        }

    }
}
