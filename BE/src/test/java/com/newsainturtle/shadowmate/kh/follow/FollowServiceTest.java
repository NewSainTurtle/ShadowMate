package com.newsainturtle.shadowmate.kh.follow;

import com.newsainturtle.shadowmate.follow.dto.FollowingResponse;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.exception.FollowErrorResult;
import com.newsainturtle.shadowmate.follow.exception.FollowException;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.service.FollowServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @InjectMocks
    private FollowServiceImpl followService;

    @Mock
    private FollowRepository followRepository;

    final User user1 = User.builder()
            .email("test1@naver.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    final User user2 = User.builder()
            .email("test2@naver.com")
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
}
