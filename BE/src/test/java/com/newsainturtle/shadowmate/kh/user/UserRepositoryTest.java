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
        void 성공_내정보수정시_닉네임조회() {
            //given
            final String nickname = user.getNickname();

            //when
            final User result = userRepository.findByIdAndNickname(userId, nickname);

            //then
            assertThat(result.getNickname()).isEqualTo(nickname);

        }

        @Test
        void 성공_내정보수정() {
            //given
            final String newNickname = "NewNickName";
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";

            //when
            userRepository.updateUser(newNickname, newProfileImage, newStatusMessage, userId);
            final User result = userRepository.findByNickname(newNickname);

            //then
            assertThat(result.getNickname()).isEqualTo(newNickname);
            assertThat(result.getProfileImage()).isEqualTo(newProfileImage);
            assertThat(result.getStatusMessage()).isEqualTo(newStatusMessage);

        }

        @Test
        void 비밀번호수정() {
            // given
            final String newPassword = "newPassword";

            // when
            userRepository.updatePassword(newPassword, userId);
            final User result = userRepository.findByNickname(user.getNickname());

            // then
            assertThat(result.getPassword()).isEqualTo(newPassword);
        }

        @Test
        void 소개글조회() {
            // given
            final String newIntroduction = "새로운소개글";
            userRepository.updateIntroduction(newIntroduction, userId);

            // when
            final String result = userRepository.findIntroduction(userId);

            // then
            assertThat(result).isEqualTo(newIntroduction);
        }

        @Test
        void 소개글수정() {
            // given
            final String newIntroduction = "새로운소개글";

            // when
            userRepository.updateIntroduction(newIntroduction, userId);
            final User result = userRepository.findById(userId).orElse(null);

            // then
            assertThat(result.getIntroduction()).isEqualTo(newIntroduction);
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
            final User saveUser = userRepository.save(user);

            //when
            userRepository.deleteUser(LocalDateTime.now(), saveUser.getId(), PlannerAccessScope.PRIVATE);
            final User findUser = userRepository.findByNickname(user.getNickname());

            //then
            assertThat(findUser).isNotNull();
            assertThat(findUser.getDeleteTime()).isNotNull();
            assertThat(findUser.getWithdrawal()).isTrue();
            assertThat(findUser.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.PRIVATE);
        }

    }
}
