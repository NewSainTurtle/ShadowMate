package com.newsainturtle.shadowmate.kh.follow;

import com.newsainturtle.shadowmate.follow.constant.FollowConstant;
import com.newsainturtle.shadowmate.follow.dto.response.*;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.follow.exception.FollowErrorResult;
import com.newsainturtle.shadowmate.follow.exception.FollowException;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.follow.service.FollowServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @InjectMocks
    private FollowServiceImpl followService;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private FollowRequestRepository followRequestRepository;

    @Mock
    private UserRepository userRepository;

    final User user1 = User.builder()
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    final User user2 = User.builder()
            .email("test2@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이2")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    final Long userId2 = user2.getId();

    @Test
    void 실패_팔로우개수조회_해당유저없음() {
        // given
        final Long userId = 1L;
        doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(userId);

        // when
        final FollowException result = assertThrows(FollowException.class, () -> followService.countFollow(userId));

        // then
        assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.NOT_FOUND_FOLLOW_USER);
    }

    @Test
    void 성공_팔로우개수조회() {
        // given
        final Long userId = 1L;
        doReturn(user1).when(userRepository).findByIdAndWithdrawalIsFalse(userId);
        doReturn(1L).when(followRepository).countByFollower(any());
        doReturn(10L).when(followRepository).countByFollowing(any());

        // when
        final CountFollowResponse result = followService.countFollow(userId);

        // then
        assertThat(result.getFollowerCount()).isEqualTo(10L);
        assertThat(result.getFollowingCount()).isEqualTo(1L);
    }

    @Test
    void 팔로우_상태조회_EMPTY() {
        // given
        doReturn(null).when(followRepository).findByFollowingAndFollower(user2, user1);
        doReturn(null).when(followRequestRepository).findByRequesterAndReceiver(user1, user2);

        // when
        final FollowStatus followStatus = followService.isFollow(user1, user2);

        // then
        assertThat(followStatus).isEqualTo(FollowStatus.EMPTY);
    }

    @Test
    void 팔로우_상태조회_FOLLOW() {
        // given
        final Follow follow = Follow.builder()
                .follower(user1)
                .following(user2)
                .build();
        doReturn(follow).when(followRepository).findByFollowingAndFollower(user2, user1);

        // when
        final FollowStatus followStatus = followService.isFollow(user1, user2);

        // then
        assertThat(followStatus).isEqualTo(FollowStatus.FOLLOW);
    }

    @Test
    void 팔로우_상태조회_REQUESTED() {
        // given
        final FollowRequest followRequest = FollowRequest.builder()
                .requester(user1)
                .receiver(user2)
                .build();
        doReturn(null).when(followRepository).findByFollowingAndFollower(user2, user1);
        doReturn(followRequest).when(followRequestRepository).findByRequesterAndReceiver(user1, user2);

        // when
        final FollowStatus followStatus = followService.isFollow(user1, user2);

        // then
        assertThat(followStatus).isEqualTo(FollowStatus.REQUESTED);
    }

    @Nested
    class 팔로잉TEST {

        @Test
        void 실패_팔로잉조회Null() {
            //given
            List<FollowingResponse> followingResponses = new ArrayList<>();
            doReturn(followingResponses).when(followRepository).findAllByFollower(user1);

            //when
            final List<FollowingResponse> result = followService.getFollowing(user1);

            //then
            assertThat(result).isEmpty();

        }

        @Test
        void 성공_팔로잉조회() {
            //given

            List<Follow> followingList = new ArrayList<>();
            followingList.add(Follow.builder().follower(user1).following(user2).build());

            doReturn(followingList).when(followRepository).findAllByFollower(user1);

            //when
            final List<FollowingResponse> result = followService.getFollowing(user1);

            //then
            assertThat(result.get(0).getNickname()).isEqualTo(user2.getNickname());
        }

        @Test
        void 실패_팔로잉유저없음() {
            // given
            assertThrows(FollowException.class, () -> followService.deleteFollowing(user1, userId2));

            // when
            final FollowException result = assertThrows(FollowException.class, () -> followService.deleteFollowing(user1, userId2));

            // then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.NOT_FOUND_FOLLOW_USER);
        }

        @Test
        void 성공_팔로잉삭제() {
            // given
            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());

            // when
            followService.deleteFollowing(user1, userId2);

            // then
            verify(followRepository, times(1)).deleteByFollowingAndFollower(any(), any());
        }
    }

    @Nested
    class 팔로워TEST {

        @Test
        void 실패_팔로워조회Null() {
            //given
            List<FollowerResponse> followerResponses = new ArrayList<>();
            doReturn(followerResponses).when(followRepository).findAllByFollowing(user2);

            //when
            final List<FollowerResponse> result = followService.getFollower(user2);

            //then
            assertThat(result).isEmpty();
        }

        @Test
        void 성공_팔로워조회() {
            //given

            List<Follow> followingList = new ArrayList<>();
            followingList.add(Follow.builder().follower(user1).following(user2).build());

            doReturn(followingList).when(followRepository).findAllByFollowing(user2);

            //when
            final List<FollowerResponse> result = followService.getFollower(user2);

            //then
            assertThat(result.get(0).getNickname()).isEqualTo(user1.getNickname());
        }

        @Test
        void 실패_팔로워삭제시유저없음() {
            // given

            // when
            final FollowException result = assertThrows(FollowException.class, () -> followService.deleteFollower(user1, userId2));

            // then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.NOT_FOUND_FOLLOW_USER);
        }

        @Test
        void 성공_팔로워삭제() {
            // given
            doReturn(user1).when(userRepository).findByIdAndWithdrawalIsFalse(any());

            // when
            followService.deleteFollower(user2, user1.getId());

            // then
            verify(followRepository, times(1)).deleteByFollowingAndFollower(any(), any());
        }
    }

    @Nested
    class 팔로우신청TEST {

        @Test
        void 성공_친구신청목록조회Null() {
            //given
            List<FollowRequest> followRequestList = new ArrayList<>();
            doReturn(followRequestList).when(followRequestRepository).findAllByReceiver(user2);

            //when
            List<FollowRequestResponse> result = followService.getFollowRequestList(user2);

            //then
            assertThat(result).isEmpty();
        }


        @Test
        void 성공_친구신청목록조회() {
            //given
            FollowRequest followRequest = FollowRequest.builder()
                    .id(1L)
                    .requester(user1)
                    .receiver(user2)
                    .build();
            List<FollowRequest> followRequestList = new ArrayList<>();
            followRequestList.add(followRequest);

            doReturn(followRequestList).when(followRequestRepository).findAllByReceiver(user2);

            //when
            List<FollowRequestResponse> result = followService.getFollowRequestList(user2);

            //then
            assertThat(result.get(0).getNickname()).isEqualTo(followRequest.getRequester().getNickname());
        }


        @Test
        void 실패_중복친구신청() {
            //given
            final User user2 = User.builder()
                    .email("test2@test.com")
                    .password("123456")
                    .socialLogin(SocialType.BASIC)
                    .nickname("거북이2")
                    .plannerAccessScope(PlannerAccessScope.PRIVATE)
                    .withdrawal(false)
                    .build();
            final Long userId = 9999L;
            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());
            doReturn(FollowRequest.builder().requester(user1).receiver(user2).build()).when(followRequestRepository).findByRequesterAndReceiver(any(), any());

            //when
            final FollowException result = assertThrows(FollowException.class, () -> followService.addFollow(user1, userId));

            //then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.DUPLICATED_FOLLOW);
        }


        @Test
        void 실패_중복팔로우신청() {
            //given
            final Long userId = 9999L;
            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());
            doReturn(Follow.builder().follower(user1).following(user2).build()).when(followRepository).findByFollowingAndFollower(any(), any());

            //when
            final FollowException result = assertThrows(FollowException.class, () -> followService.addFollow(user1, userId));

            //then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.DUPLICATED_FOLLOW);
        }


        @Test
        void 실패_팔로우신청_유저없음() {
            //given
            final Long userId = 9999L;

            doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(any());

            //when
            final FollowException result = assertThrows(FollowException.class, () -> followService.addFollow(user1, userId));

            //then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.NOT_FOUND_FOLLOW_USER);
        }


        @Test
        void 성공_팔로우신청_비공개() {
            //given
            final User user2 = User.builder()
                    .email("test2@test.com")
                    .password("123456")
                    .socialLogin(SocialType.BASIC)
                    .nickname("거북이2")
                    .plannerAccessScope(PlannerAccessScope.PRIVATE)
                    .withdrawal(false)
                    .build();

            final FollowRequest followRequest = FollowRequest.builder()
                    .id(1L)
                    .requester(user1)
                    .receiver(user2)
                    .build();

            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());
            doReturn(followRequest).when(followRequestRepository).save(any());

            //when
            final AddFollowResponse result = followService.addFollow(user1, userId2);

            //then
            assertThat(result.getFollowId()).isEqualTo(followRequest.getId());
            assertThat(result.getPlannerAccessScope()).isEqualTo(followRequest.getReceiver().getPlannerAccessScope());
        }


        @Test
        void 성공_팔로우신청_전체공개() {
            //given
            final Follow follow = Follow.builder()
                    .id(1L)
                    .follower(user1)
                    .following(user2)
                    .build();

            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());
            doReturn(follow).when(followRepository).save(any());

            //when
            final AddFollowResponse result = followService.addFollow(user1, userId2);

            //then
            assertThat(result.getFollowId()).isEqualTo(follow.getId());
            assertThat(result.getPlannerAccessScope()).isEqualTo(follow.getFollowing().getPlannerAccessScope());
        }

        @Test
        void 실패_친구신청취소유저없음() {
            // given

            // when
            final FollowException result = assertThrows(FollowException.class, () -> followService.deleteFollowRequest(user1, userId2));

            // then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.NOT_FOUND_FOLLOW_USER);
        }

        @Test
        void 성공_친구신청취소() {
            // given
            doReturn(user1).when(userRepository).findByIdAndWithdrawalIsFalse(any());

            // when
            followService.deleteFollowRequest(user2, user1.getId());

            // then
            verify(followRequestRepository, times(1)).deleteByRequesterAndReceiver(any(), any());
        }

        @Test
        void 실패_친구신청유저없음() {
            //given

            //when
            final FollowException result = assertThrows(FollowException.class, () -> followService.receiveFollow(user1, userId2, true));

            //then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.NOT_FOUND_FOLLOW_USER);
        }

        @Test
        void 실패_친구신청존재하지않음() {
            //given
            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());
            doThrow(new FollowException(FollowErrorResult.NOT_FOUND_FOLLOW_REQUEST)).when(followRequestRepository).findByRequesterAndReceiver(any(), any());

            //when
            final FollowException result = assertThrows(FollowException.class, () -> followService.receiveFollow(user1, userId2, true));

            //then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.NOT_FOUND_FOLLOW_REQUEST);
        }

        @Test
        void 성공_친구신청거절() {
            //given
            final FollowRequest followRequest = FollowRequest.builder()
                    .requester(user1)
                    .receiver(user2)
                    .build();
            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());
            doReturn(followRequest).when(followRequestRepository).findByRequesterAndReceiver(any(), any());

            //when
            final String result = followService.receiveFollow(user1, userId2, false);

            //then
            assertThat(result).isEqualTo(FollowConstant.SUCCESS_FOLLOW_RECEIVE_FALSE);
        }

        @Test
        void 성공_친구신청수락() {
            //given
            final FollowRequest followRequest = FollowRequest.builder()
                    .requester(user1)
                    .receiver(user2)
                    .build();
            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());
            doReturn(followRequest).when(followRequestRepository).findByRequesterAndReceiver(any(), any());

            //when
            final String result = followService.receiveFollow(user1, userId2, true);

            //then
            assertThat(result).isEqualTo(FollowConstant.SUCCESS_FOLLOW_RECEIVE_TRUE);
        }

    }

    @Nested
    class 회원검색 {
        @Test
        void 실패_회원없음() {
            // given
            final String user2Nickname = user2.getNickname();

            // when
            final SearchUserResponse result = followService.searchNickname(user1, user2Nickname);

            // then
            assertThat(result.getUserId()).isNull();
            assertThat(result.getNickname()).isNull();
        }

        @Test
        void 성공_회원검색_친구요청상태() {
            // given
            final FollowRequest followRequest = FollowRequest.builder()
                    .id(1L)
                    .requester(user1)
                    .receiver(user2)
                    .build();
            doReturn(null).when(followRepository).findByFollowingAndFollower(any(), any());
            doReturn(followRequest).when(followRequestRepository).findByRequesterAndReceiver(any(), any());
            doReturn(user2).when(userRepository).findByNicknameAndWithdrawalIsFalse(any());

            // when
            final SearchUserResponse result = followService.searchNickname(user1, user2.getNickname());

            // then
            assertThat(result.getNickname()).isEqualTo(user2.getNickname());
            assertThat(result.getIsFollow()).isEqualTo(FollowStatus.REQUESTED);
        }

        @Test
        void 성공_회원검색_팔로우아닌상태() {
            // given
            doReturn(null).when(followRepository).findByFollowingAndFollower(any(), any());
            doReturn(null).when(followRequestRepository).findByRequesterAndReceiver(any(), any());
            doReturn(user2).when(userRepository).findByNicknameAndWithdrawalIsFalse(any());

            // when
            final SearchUserResponse result = followService.searchNickname(user1, user2.getNickname());

            // then
            assertThat(result.getNickname()).isEqualTo(user2.getNickname());
            assertThat(result.getIsFollow()).isEqualTo(FollowStatus.EMPTY);
        }

        @Test
        void 성공_회원검색_FOLLOW상태() {
            // given
            final Follow follow = Follow.builder()
                    .id(1L)
                    .follower(user1)
                    .following(user2)
                    .build();
            doReturn(follow).when(followRepository).findByFollowingAndFollower(any(), any());
            doReturn(user2).when(userRepository).findByNicknameAndWithdrawalIsFalse(any());

            // when
            final SearchUserResponse result = followService.searchNickname(user1, user2.getNickname());

            // then
            assertThat(result.getNickname()).isEqualTo(user2.getNickname());
            assertThat(result.getIsFollow()).isEqualTo(FollowStatus.FOLLOW);
        }
    }
}
