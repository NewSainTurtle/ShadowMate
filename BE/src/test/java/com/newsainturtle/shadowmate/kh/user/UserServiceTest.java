package com.newsainturtle.shadowmate.kh.user;

import com.newsainturtle.shadowmate.auth.service.RedisServiceImpl;
import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.service.FollowServiceImpl;
import com.newsainturtle.shadowmate.user.dto.*;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    @Mock
    private RedisServiceImpl redisService;

    @Mock
    private FollowServiceImpl followService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
    final Long userId1 = user1.getId();
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
        void 실패_내정보수정_닉네임검증Null() {
            //given
            final String newNickname = "NewNickName";
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newNickname(newNickname)
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();
            doReturn(null).when(userRepository).findByIdAndNickname(userId1, newNickname);
            doReturn(null).when(redisService).getHashNicknameData(newNickname);

            //when
            final UserException result = assertThrows(UserException.class, () -> userService.updateUser(userId1, updateUserRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.RETRY_NICKNAME);

        }

        @Test
        void 실패_내정보수정_닉네임검증false() {
            //given
            final String newNickname = "NewNickName";
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newNickname(newNickname)
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();
            doReturn(null).when(userRepository).findByIdAndNickname(userId1, newNickname);
            doReturn(false).when(redisService).getHashNicknameData(newNickname);

            //when
            final UserException result = assertThrows(UserException.class, () -> userService.updateUser(userId1, updateUserRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.RETRY_NICKNAME);

        }

        @Test
        void 성공_내정보수정_닉네임수정안함() {
            //given
            final String nickname = user1.getNickname();
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newNickname(nickname)
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();

            doReturn(user1).when(userRepository).findByIdAndNickname(userId1, nickname);

            //when
            userService.updateUser(userId1, updateUserRequest);

            //then
            verify(userRepository, times(1)).updateUser(any(), any(), any(), any(Long.class));

        }

        @Test
        void 성공_내정보수정_닉네임수정() {
            //given
            final String newNickname = "NewNickName";
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newNickname(newNickname)
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();
            doReturn(null).when(userRepository).findByIdAndNickname(userId1, newNickname);
            doReturn(true).when(redisService).getHashNicknameData(newNickname);

            //when
            userService.updateUser(userId1, updateUserRequest);

            //then
            verify(redisService, times(1)).deleteNicknameData(any());
            verify(userRepository, times(1)).updateUser(any(), any(), any(), any(Long.class));

        }

        @Test
        void 실패_비밀번호수정_비밀번호다름() {
            // given
            final String newPassword = "NewPassword";
            doReturn(Optional.of(user1)).when(userRepository).findById(userId1);
            doReturn(false).when(bCryptPasswordEncoder).matches(any(), any());

            // when
            final UserException result = assertThrows(UserException.class, () -> userService.updatePassword(userId1, user1.getPassword(), newPassword));

            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.DIFFERENT_PASSWORD);
        }

        @Test
        void 성공_비밀번호수정() {
            // given
            final String newPassword = "NewPassword";
            doReturn(Optional.of(user1)).when(userRepository).findById(userId1);
            doReturn(true).when(bCryptPasswordEncoder).matches(any(), any());

            // when
            userService.updatePassword(userId1, user1.getPassword(), newPassword);

            // then
            verify(userRepository, times(1)).updatePassword(any(), any(Long.class));
        }

        @Test
        void 성공_소개글수정() {
            // given
            final String newIntroduction = "새로운소개글";
            final UpdateIntroductionRequest updateIntroductionRequest = UpdateIntroductionRequest.builder()
                    .introduction(newIntroduction)
                    .build();

            // when
            userService.updateIntroduction(updateIntroductionRequest, userId1);

            // then
            verify(userRepository, times(1)).updateIntroduction(any(), any(Long.class));

        }

    }

    @Nested
    class 회원TEST {

        @Test
        void 실패_회원없음() {
            // given
            final String user2Nickname = user2.getNickname();

            // when
            final UserResponse result = userService.searchNickname(user1, user2Nickname);

            // then
            assertThat(result.getUserId()).isNull();
            assertThat(result.getNickname()).isNull();
        }

        @Test
        void 성공_회원검색_친구요청상태() {
            // given
            doReturn(FollowStatus.REQUESTED).when(followService).isFollow(any(), any());
            doReturn(user2).when(userRepository).findByNickname(any());

            // when
            final UserResponse result = userService.searchNickname(user1, user2.getNickname());

            // then
            assertThat(result.getNickname()).isEqualTo(user2.getNickname());
            assertThat(result.getIsFollow()).isEqualTo(FollowStatus.REQUESTED);
        }

        @Test
        void 성공_회원검색_팔로우아닌상태() {
            // given
            doReturn(FollowStatus.EMPTY).when(followService).isFollow(any(), any());
            doReturn(user2).when(userRepository).findByNickname(any());

            // when
            final UserResponse result = userService.searchNickname(user1, user2.getNickname());

            // then
            assertThat(result.getNickname()).isEqualTo(user2.getNickname());
            assertThat(result.getIsFollow()).isEqualTo(FollowStatus.EMPTY);
        }

        @Test
        void 성공_회원검색_FOLLOW상태() {
            // given
            doReturn(FollowStatus.FOLLOW).when(followService).isFollow(any(), any());
            doReturn(user2).when(userRepository).findByNickname(any());

            // when
            final UserResponse result = userService.searchNickname(user1, user2.getNickname());

            // then
            assertThat(result.getNickname()).isEqualTo(user2.getNickname());
            assertThat(result.getIsFollow()).isEqualTo(FollowStatus.FOLLOW);
        }

        @Test
        void 실패_회원탈퇴_유저없음() {
            //given
            doReturn(Optional.empty()).when(userRepository).findById(userId1);

            //when
            final UserException result = assertThrows(UserException.class, () -> userService.deleteUser(userId1));

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
            given(userRepository.findById(userId1)).willReturn(Optional.of(deleteUser));

            //when
            userService.deleteUser(userId1);

            //then
            verify(userRepository, times(1)).findById(any());
            verify(userRepository, times(1)).save(any());
        }

    }
}
