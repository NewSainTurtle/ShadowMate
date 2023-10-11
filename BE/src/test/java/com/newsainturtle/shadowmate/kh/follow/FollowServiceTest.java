package com.newsainturtle.shadowmate.kh.follow;

import com.newsainturtle.shadowmate.follow.dto.AddFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.FollowerResponse;
import com.newsainturtle.shadowmate.follow.dto.FollowingResponse;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @InjectMocks
    private FollowServiceImpl followService;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private FollowRequestRepository followRequestRepository;

    @Spy
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

    @Nested
    class 팔로잉TEST {

        @Test
        public void 실패_팔로잉조회Null() {
            //given
            List<FollowingResponse> followingResponses = new ArrayList<>();
            doReturn(followingResponses).when(followRepository).findAllByFollowerId(user1);

            //when
            final List<FollowingResponse> result = followService.getFollowing(user1);

            //then
            assertThat(result).isEmpty();

        }

        @Test
        public void 성공_팔로잉조회() {
            //given

            List<Follow> followingList = new ArrayList<>();
            followingList.add(Follow.builder().followerId(user1).followingId(user2).build());

            doReturn(followingList).when(followRepository).findAllByFollowerId(user1);

            //when
            final List<FollowingResponse> result = followService.getFollowing(user1);

            //then
            assertThat(result.get(0).getNickname()).isEqualTo(user2.getNickname());
        }
    }

    @Nested
    class 팔로워TEST {

        @Test
        void 실패_팔로잉조회Null() {
            //given
            List<FollowerResponse> followerResponses = new ArrayList<>();
            doReturn(followerResponses).when(followRepository).findAllByFollowingId(user2);

            //when
            final List<FollowerResponse> result = followService.getFollower(user2);

            //then
            assertThat(result).isEmpty();
        }

        @Test
        public void 성공_팔로워조회() {
            //given

            List<Follow> followingList = new ArrayList<>();
            followingList.add(Follow.builder().followerId(user1).followingId(user2).build());

            doReturn(followingList).when(followRepository).findAllByFollowingId(user2);

            //when
            final List<FollowerResponse> result = followService.getFollower(user2);

            //then
            assertThat(result.get(0).getNickname()).isEqualTo(user1.getNickname());
        }

        @Test
        void 실패_팔로워유저없음() {
            // given

            // when
            final FollowException result = assertThrows(FollowException.class, () -> followService.deleteFollower(user1, user2.getId()));

            // then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.NOTFOUND_FOLLOW_USER);
        }

        @Test
        void 성공_팔로워삭제() {
            // given
            doReturn(Optional.ofNullable(user2)).when(userRepository).findById(any());

            // when
            followService.deleteFollower(user2, user1.getId());

            // then
            verify(followRepository, times(1)).deleteByFollowingIdAndFollowerId(any(), any());
        }
    }

    @Nested
    class 팔로우신청TEST {

        @Test
        public void 실패_중복친구신청() {
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
            doReturn(Optional.ofNullable(user2)).when(userRepository).findById(any());
            doReturn(FollowRequest.builder().requesterId(user1).receiverId(user2).build()).when(followRequestRepository).findByRequesterIdAndReceiverId(any(), any());

            //when
            final FollowException result = assertThrows(FollowException.class, () -> followService.addFollow(user1, userId));

            //then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.DUPLICATED_FOLLOW);
        }


        @Test
        public void 실패_중복팔로우신청() {
            //given
            final Long userId = 9999L;
            doReturn(Optional.ofNullable(user2)).when(userRepository).findById(any());
            doReturn(Follow.builder().followerId(user1).followingId(user2).build()).when(followRepository).findByFollowerIdAndFollowingId(any(), any());

            //when
            final FollowException result = assertThrows(FollowException.class, () -> followService.addFollow(user1, userId));

            //then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.DUPLICATED_FOLLOW);
        }


        @Test
        public void 실패_팔로우신청_유저없음() {
            //given
            final Long userId = 9999L;

            doThrow(new FollowException(FollowErrorResult.NOTFOUND_FOLLOW_USER)).when(userRepository).findById(any());

            //when
            final FollowException result = assertThrows(FollowException.class, () -> followService.addFollow(user1, userId));

            //then
            assertThat(result.getErrorResult()).isEqualTo(FollowErrorResult.NOTFOUND_FOLLOW_USER);
        }


        @Test
        public void 성공_팔로우신청_비공개() {
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
                    .requesterId(user1)
                    .receiverId(user2)
                    .build();

            final Optional<User> user = Optional.ofNullable(user2);
            doReturn(user).when(userRepository).findById(any());
            doReturn(followRequest).when(followRequestRepository).save(any());

            //when
            final AddFollowResponse result = followService.addFollow(user1, user2.getId());

            //then
            assertThat(result.getFollowId()).isEqualTo(followRequest.getId());
            assertThat(result.getPlannerAccessScope()).isEqualTo(followRequest.getReceiverId().getPlannerAccessScope());
        }


        @Test
        public void 성공_팔로우신청_전체공개() {
            //given
            final Follow follow = Follow.builder()
                    .id(1L)
                    .followerId(user1)
                    .followingId(user2)
                    .build();

            final Optional<User> user = Optional.ofNullable(user2);
            doReturn(user).when(userRepository).findById(any());
            doReturn(follow).when(followRepository).save(any());

            //when
            final AddFollowResponse result = followService.addFollow(user1, user2.getId());

            //then
            assertThat(result.getFollowId()).isEqualTo(follow.getId());
            assertThat(result.getPlannerAccessScope()).isEqualTo(follow.getFollowingId().getPlannerAccessScope());
        }

    }
}
