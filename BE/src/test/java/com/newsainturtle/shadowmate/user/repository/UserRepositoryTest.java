package com.newsainturtle.shadowmate.user.repository;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Nested
    class 프로필수정 {

        @Test
        void 성공_내정보수정() {
            //given
            final String newNickname = "NewNickName";
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";

            //when
            userRepository.updateUser(newNickname, newProfileImage, newStatusMessage, user.getId());
            final User result = userRepository.findByNicknameAndWithdrawalIsFalse(newNickname);

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
            userRepository.updatePassword(newPassword, user.getId());
            final User result = userRepository.findByNicknameAndWithdrawalIsFalse(user.getNickname());

            // then
            assertThat(result.getPassword()).isEqualTo(newPassword);
        }

        @Test
        void 소개글조회() {
            // given
            final String newIntroduction = "새로운소개글";
            userRepository.updateIntroduction(newIntroduction, user.getId());

            // when
            final String result = userRepository.findIntroduction(user.getId());

            // then
            assertThat(result).isEqualTo(newIntroduction);
        }

        @Test
        void 소개글수정() {
            // given
            final String newIntroduction = "새로운소개글";

            // when
            userRepository.updateIntroduction(newIntroduction, user.getId());
            final User result = userRepository.findById(user.getId()).orElse(null);

            // then
            assertThat(result.getIntroduction()).isEqualTo(newIntroduction);
        }
    }

    @Nested
    class 회원닉네임조회 {

        @Test
        void 실패_회원없음() {
            // given

            // when
            final User result = userRepository.findByNicknameAndWithdrawalIsFalse("거북이234567");

            // then
            assertThat(result).isNull();
        }

        @Test
        void 성공_회원검색() {
            // given

            // when
            final User result = userRepository.findByNicknameAndWithdrawalIsFalse(user.getNickname());

            // then
            assertThat(result.getNickname()).isEqualTo(user.getNickname());
        }
    }

    @Nested
    class 회원ID조회 {
        @Test
        void 탈퇴하지않은사용자조회() {
            //given

            //when
            final User findUser = userRepository.findByIdAndWithdrawalIsFalse(user.getId());

            //then
            assertThat(findUser).isNotNull();
            assertThat(findUser.getEmail()).isEqualTo(user.getEmail());
            assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
            assertThat(findUser.getSocialLogin()).isEqualTo(user.getSocialLogin());
            assertThat(findUser.getNickname()).isEqualTo(user.getNickname());
            assertThat(findUser.getPlannerAccessScope()).isEqualTo(user.getPlannerAccessScope());
            assertThat(findUser.getWithdrawal()).isEqualTo(user.getWithdrawal());
        }

        @Test
        void 탈퇴하지않은사용자조회_탈퇴한경우() {
            //given
            final User user = userRepository.save(User.builder()
                    .email("yntest@shadowmate.com")
                    .password("yntest1234")
                    .socialLogin(SocialType.BASIC)
                    .nickname("거북이")
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .withdrawal(true)
                    .build());

            //when
            final User findUser = userRepository.findByIdAndWithdrawalIsFalse(user.getId());

            //then
            assertThat(findUser).isNull();
        }
    }

    @Test
    void 성공_회원탈퇴() {
        //given
        final long userId = user.getId();

        //when
        userRepository.deleteUser(LocalDateTime.now(), user.getId(), PlannerAccessScope.PRIVATE, "ABC");
        final Optional<User> findUser = userRepository.findById(userId);

        //then
        assertThat(findUser).isPresent();
        assertThat(findUser.get().getDeleteTime()).isNotNull();
        assertThat(findUser.get().getWithdrawal()).isTrue();
        assertThat(findUser.get().getPlannerAccessScope()).isEqualTo(PlannerAccessScope.PRIVATE);
        assertThat(findUser.get().getNickname()).isEqualTo("ABC");
    }

    @Test
    void 성공_플래너공개여부설정() {
        //given
        final User user = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());

        final User changeUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .socialLogin(user.getSocialLogin())
                .nickname(user.getNickname())
                .plannerAccessScope(PlannerAccessScope.FOLLOW)
                .withdrawal(user.getWithdrawal())
                .createTime(user.getCreateTime())
                .build();
        userRepository.save(changeUser);

        //when
        final User result = userRepository.findById(user.getId()).orElse(null);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.FOLLOW);

    }


}
