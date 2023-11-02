package com.newsainturtle.shadowmate.kh.user;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.user.dto.ProfileResponse;
import com.newsainturtle.shadowmate.user.dto.UpdateUserRequest;
import com.newsainturtle.shadowmate.user.dto.UserResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import com.newsainturtle.shadowmate.user.service.UserServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private FollowRepository followRepository;

    final User user1 = User.builder()
            .id(1L)
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .withdrawal(false)
            .profileImage("TestProfileURL")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();
    final User user2 = User.builder()
            .id(2L)
            .email("test2@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이2")
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

            // then
            assertThrows(UserException.class, () -> {
                userService.getProfile(userId);
            });
        }

        @Test
        void 성공_프로필조회() {
            // given
            final Long userId = 1L;
            Optional<User> optUser = Optional.ofNullable(user1);
            doReturn(optUser).when(userRepository).findById(userId);

            // when
            final ProfileResponse profileResponse = userService.getProfile(userId);

            // then
            assertThat(profileResponse.getEmail()).isEqualTo(user1.getEmail());

        }

        @Test
        void 성공_내정보수정() {
            //given
            final String newNickname = "NewNickName";
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newNickname(newNickname)
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();

            //when
            userService.updateUser(user1.getId(), updateUserRequest);

            //then
            verify(userRepository, times(1)).updateUser(any(), any(), any(), any(Long.class));

        }

    }

    @Nested
    class 회원TEST {

        @Test
        void 실패_회원없음() {
            // given

            // when
            final UserException result = assertThrows(UserException.class, () -> userService.searchNickname(user1, user2.getNickname()));

            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_NICKNAME);
        }

        @Test
        void 성공_회원검색() {
            // given
            Follow follow = Follow.builder().followerId(user1).followingId(user2).build();
            doReturn(follow).when(followRepository).findByFollowerIdAndFollowingId(any(), any());
            doReturn(user2).when(userRepository).findByNickname(any());

            // when
            final UserResponse result = userService.searchNickname(user1, user2.getNickname());

            // then
            assertThat(result.getNickname()).isEqualTo(user2.getNickname());
        }

        @Test
        void 실패_회원탈퇴_유저없음() {
            //given
            doReturn(Optional.empty()).when(userRepository).findById(user1.getId());

            //when
            final UserException result = assertThrows(UserException.class, () -> userService.deleteUser(user1.getId()));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);

        }

        @Test
        void 성공_회원탈퇴() {
            //given
            final User deleteUser = User.builder()
                    .id(user1.getId())
                    .email(user1.getEmail())
                    .password(user1.getPassword())
                    .socialLogin(user1.getSocialLogin())
                    .profileImage(user1.getProfileImage())
                    .nickname(user1.getNickname())
                    .statusMessage(user1.getStatusMessage())
                    .withdrawal(true)
                    .plannerAccessScope(user1.getPlannerAccessScope())
                    .createTime(user1.getCreateTime())
                    .updateTime(user1.getUpdateTime())
                    .deleteTime(LocalDateTime.now())
                    .build();
            given(userRepository.findById(user1.getId())).willReturn(Optional.of(deleteUser));

            //when
            userService.deleteUser(user1.getId());

            //then
            verify(userRepository, times(1)).findById(any());
            verify(userRepository, times(1)).save(any());
        }

    }
}
