package com.newsainturtle.shadowmate.follow.service;

import com.newsainturtle.shadowmate.follow.constant.FollowConstant;
import com.newsainturtle.shadowmate.follow.dto.response.AddFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.response.CountFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.response.SearchUserResponse;
import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.follow.exception.FollowErrorResult;
import com.newsainturtle.shadowmate.follow.exception.FollowException;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFollowServiceTest {

    @InjectMocks
    private UserFollowServiceImpl userFollowService;

    @Mock
    private FollowService followService;

    @Mock
    private UserService userService;

    private final User user1 = User.builder()
            .id(1L)
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .email("test2@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이2")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    private final long user2Id = 2L;

    @Nested
    class 팔로잉삭제 {
        @Test
        void 실패_유저없음() {
            // given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(user2Id);

            // when
            final UserException result = assertThrows(UserException.class, () -> userFollowService.deleteFollowing(user1, user2Id));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
        }

        @Test
        void 성공() {
            // given
            doReturn(user2).when(userService).getUserById(user2Id);

            // when
            userFollowService.deleteFollowing(user1, user2Id);

            // then
            verify(userService, times(1)).getUserById(user2Id);
            verify(followService, times(1)).deleteFollowing(user1, user2);
        }
    }

    @Nested
    class 팔로워삭제 {
        @Test
        void 실패_유저없음() {
            // given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(user2Id);

            // when
            final UserException result = assertThrows(UserException.class, () -> userFollowService.deleteFollower(user1, user2Id));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
        }

        @Test
        void 성공() {
            // given
            doReturn(user2).when(userService).getUserById(user2Id);

            // when
            userFollowService.deleteFollower(user1, user2Id);

            // then
            verify(userService, times(1)).getUserById(user2Id);
            verify(followService, times(1)).deleteFollower(user1, user2);
        }
    }

    @Nested
    class 팔로우신청 {
        @Test
        void 실패_유저없음() {
            // given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(user2Id);

            // when
            final UserException result = assertThrows(UserException.class, () -> userFollowService.addFollow(user1, user2Id));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
        }

        @Test
        void 실패_중복팔로우신청() {
            //given
            doReturn(user2).when(userService).getUserById(user2Id);
            doThrow(new FollowException(FollowErrorResult.DUPLICATED_FOLLOW)).when(followService).addFollow(any(), any());

            //when
            final FollowException result = assertThrows(FollowException.class, () -> userFollowService.addFollow(user1, user2Id));

            //then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.DUPLICATED_FOLLOW);
        }

        @Test
        void 성공() {
            //given
            final AddFollowResponse addFollowResponse = AddFollowResponse.builder()
                    .followId(1L)
                    .plannerAccessScope(user2.getPlannerAccessScope())
                    .build();
            doReturn(user2).when(userService).getUserById(user2Id);
            doReturn(addFollowResponse).when(followService).addFollow(any(), any());

            //when
            final AddFollowResponse result = userFollowService.addFollow(user1, user2Id);

            //then
            assertThat(result.getPlannerAccessScope()).isEqualTo(user2.getPlannerAccessScope());
        }
    }

    @Nested
    class 팔로우신청취소 {

        @Test
        void 실패_유저없음() {
            // given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(user2Id);

            // when
            final UserException result = assertThrows(UserException.class, () -> userFollowService.deleteFollowRequest(user1, user2Id));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
        }

        @Test
        void 성공() {
            // given
            doReturn(user2).when(userService).getUserById(user2Id);

            // when
            userFollowService.deleteFollowRequest(user1, user2Id);

            // then
            verify(userService, times(1)).getUserById(user2Id);
            verify(followService, times(1)).deleteFollowRequest(user1, user2);
        }
    }

    @Nested
    class 팔로우신청응답 {

        @Test
        void 실패_유저없음() {
            // given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(user2Id);

            // when
            final UserException result = assertThrows(UserException.class, () -> userFollowService.receiveFollow(user1, user2Id, true));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
        }

        @Test
        void 실패_팔로우신청존재하지않음() {
            //given
            doReturn(user2).when(userService).getUserById(user2Id);
            doThrow(new FollowException(FollowErrorResult.NOT_FOUND_FOLLOW_REQUEST)).when(followService).receiveFollow(any(User.class), any(User.class), any(Boolean.class));

            //when
            final FollowException result = assertThrows(FollowException.class, () -> userFollowService.receiveFollow(user1, user2Id, true));

            //then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.NOT_FOUND_FOLLOW_REQUEST);
        }

        @Test
        void 성공_팔로우신청거절() {
            //given
            doReturn(user2).when(userService).getUserById(user2Id);
            doReturn(FollowConstant.SUCCESS_FOLLOW_RECEIVE_FALSE).when(followService).receiveFollow(any(User.class), any(User.class), any(Boolean.class));

            //when
            final String result = userFollowService.receiveFollow(user1, user2Id, false);

            //then
            assertThat(result).isEqualTo(FollowConstant.SUCCESS_FOLLOW_RECEIVE_FALSE);
        }

        @Test
        void 성공_팔로우신청수락() {
            //given
            doReturn(user2).when(userService).getUserById(user2Id);
            doReturn(FollowConstant.SUCCESS_FOLLOW_RECEIVE_TRUE).when(followService).receiveFollow(any(User.class), any(User.class), any(Boolean.class));

            //when
            final String result = userFollowService.receiveFollow(user1, user2Id, true);

            //then
            assertThat(result).isEqualTo(FollowConstant.SUCCESS_FOLLOW_RECEIVE_TRUE);
        }
    }


    @Nested
    class 성공_팔로우수조회 {
        @Test
        void 실패_유저없음() {
            // given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(user2Id);

            // when
            final UserException result = assertThrows(UserException.class, () -> userFollowService.countFollow(user2Id));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
        }

        @Test
        void 성공() {
            // given
            final CountFollowResponse countFollowResponse = CountFollowResponse.builder()
                    .followerCount(2L)
                    .followingCount(3L)
                    .build();
            doReturn(user2).when(userService).getUserById(user2Id);
            doReturn(countFollowResponse).when(followService).countFollow(any(User.class));

            // when
            final CountFollowResponse result = userFollowService.countFollow(user2Id);

            // then
            assertThat(result.getFollowerCount()).isEqualTo(2L);
            assertThat(result.getFollowingCount()).isEqualTo(3L);
        }
    }

    @Nested
    class 회원검색 {
        final String nickname = user2.getNickname();

        @Test
        void 성공_검색된회원없음() {
            // given
            doReturn(null).when(userService).getUserByNickname(nickname);
            doReturn(SearchUserResponse.builder().build()).when(followService).searchNickname(any(), any());

            // when
            final SearchUserResponse searchUserResponse = userFollowService.searchNickname(user1, nickname);

            // then
            assertThat(searchUserResponse).isNotNull();
            assertThat(searchUserResponse.getUserId()).isNull();
        }

        @Test
        void 성공_회원검색_팔로우요청상태() {
            // given
            final SearchUserResponse searchUserResponse = SearchUserResponse.builder()
                    .userId(user2.getId())
                    .email(user2.getEmail())
                    .profileImage(user2.getProfileImage())
                    .nickname(user2.getNickname())
                    .statusMessage(user2.getStatusMessage())
                    .plannerAccessScope(user2.getPlannerAccessScope())
                    .isFollow(FollowStatus.REQUESTED)
                    .build();
            doReturn(user2).when(userService).getUserByNickname(nickname);
            doReturn(searchUserResponse).when(followService).searchNickname(any(), any());

            // when
            final SearchUserResponse result = userFollowService.searchNickname(user1, nickname);

            // then
            assertThat(result.getNickname()).isEqualTo(user2.getNickname());
            assertThat(result.getIsFollow()).isEqualTo(FollowStatus.REQUESTED);
        }

        @Test
        void 성공_회원검색_팔로우아닌상태() {
            final SearchUserResponse searchUserResponse = SearchUserResponse.builder()
                    .userId(user2.getId())
                    .email(user2.getEmail())
                    .profileImage(user2.getProfileImage())
                    .nickname(user2.getNickname())
                    .statusMessage(user2.getStatusMessage())
                    .plannerAccessScope(user2.getPlannerAccessScope())
                    .isFollow(FollowStatus.EMPTY)
                    .build();
            doReturn(user2).when(userService).getUserByNickname(nickname);
            doReturn(searchUserResponse).when(followService).searchNickname(any(), any());

            // when
            final SearchUserResponse result = userFollowService.searchNickname(user1, nickname);

            // then
            assertThat(result.getNickname()).isEqualTo(user2.getNickname());
            assertThat(result.getIsFollow()).isEqualTo(FollowStatus.EMPTY);
        }

        @Test
        void 성공_회원검색_FOLLOW상태() {
            final SearchUserResponse searchUserResponse = SearchUserResponse.builder()
                    .userId(user2.getId())
                    .email(user2.getEmail())
                    .profileImage(user2.getProfileImage())
                    .nickname(user2.getNickname())
                    .statusMessage(user2.getStatusMessage())
                    .plannerAccessScope(user2.getPlannerAccessScope())
                    .isFollow(FollowStatus.FOLLOW)
                    .build();
            doReturn(user2).when(userService).getUserByNickname(nickname);
            doReturn(searchUserResponse).when(followService).searchNickname(any(), any());

            // when
            final SearchUserResponse result = userFollowService.searchNickname(user1, nickname);

            // then
            assertThat(result.getNickname()).isEqualTo(user2.getNickname());
            assertThat(result.getIsFollow()).isEqualTo(FollowStatus.FOLLOW);
        }
    }
}
