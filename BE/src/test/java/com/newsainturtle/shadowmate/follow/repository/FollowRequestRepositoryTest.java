package com.newsainturtle.shadowmate.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FollowRequestRepositoryTest {

    @Autowired
    private FollowRequestRepository followRequestRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private FollowRequest followRequest;

    @BeforeEach
    public void init() {
        user1 = userRepository.save(User.builder()
                .email("test1@test.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이1")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        user2 = userRepository.save(User.builder()
                .email("test2@test.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이2")
                .plannerAccessScope(PlannerAccessScope.PRIVATE)
                .withdrawal(false)
                .build());
        followRequest = FollowRequest.builder()
                .requester(user1)
                .receiver(user2)
                .build();
    }

    @Test
    void 성공_팔로우신청_조회() {
        //given
        followRequestRepository.save(followRequest);

        //when
        final FollowRequest result = followRequestRepository.findByRequesterAndReceiver(user1, user2);

        //then
        assertThat(result.getReceiver().getNickname()).isEqualTo(user2.getNickname());
    }

    @Test
    void 성공_받은친구신청조회() {
        //given
        followRequestRepository.save(followRequest);

        //when
        final List<FollowRequest> followRequests = followRequestRepository.findAllByReceiver(user2);

        //then
        assertThat(followRequests.get(0).getRequester()).isEqualTo(user1);
    }

    @Test
    void 성공_팔로우신청_비공개() {
        //given

        //when
        final FollowRequest result = followRequestRepository.save(followRequest);

        //then
        assertThat(result.getRequester()).isEqualTo(user1);
        assertThat(result.getReceiver()).isEqualTo(user2);
    }

    @Test
    void 성공_친구신청취소_또는거절() {
        // given
        followRequestRepository.save(followRequest);

        // when
        followRequestRepository.deleteByRequesterAndReceiver(user1, user2);
        final FollowRequest result = followRequestRepository.findByRequesterAndReceiver(user1, user2);

        // then
        assertThat(result).isNull();
    }

    @Test
    void 성공_친구신청수락() {
        //given
        followRequestRepository.save(followRequest);
        final Follow follow = Follow.builder()
                .follower(user1)
                .following(user2)
                .build();

        //when
        followRequestRepository.deleteByRequesterAndReceiver(user1, user2);
        final Follow result = followRepository.save(follow);

        //then
        assertThat(result.getFollower().getNickname()).isEqualTo(user1.getNickname());
        assertThat(result.getFollowing().getNickname()).isEqualTo(user2.getNickname());

    }

    @Test
    void 유저와관련된팔로우신청삭제() {
        // given
        followRequestRepository.save(followRequest);
        followRequestRepository.save(FollowRequest.builder()
                .requester(user2)
                .receiver(user1)
                .build());

        // when
        followRequestRepository.deleteAllByRequesterOrReceiver(user1, user1);
        final List<FollowRequest> result1 = followRequestRepository.findAllByReceiver(user1);
        final FollowRequest result2 = followRequestRepository.findByRequesterAndReceiver(user1, user2);

        // then
        assertThat(result1).isEmpty();
        assertThat(result2).isNull();
    }

    @Test
    void 팔로우요청목록전체삭제_요청0() {
        //given

        //when
        followRequestRepository.deleteAllByReceiver(user1.getId());
        final List<FollowRequest> followRequestList = followRequestRepository.findAllByReceiver(user1);

        //then
        assertThat(followRequestList).isEmpty();
    }

    @Test
    void 팔로우요청목록전체삭제_요청2() {
        //given
        final User user3 = userRepository.save(User.builder()
                .email("test3@test.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("고양이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        followRequestRepository.save(followRequest);
        followRequestRepository.save(FollowRequest.builder()
                .receiver(user1)
                .requester(user3)
                .build());

        //when
        followRequestRepository.deleteAllByReceiver(user1.getId());
        final List<FollowRequest> followRequestList = followRequestRepository.findAllByReceiver(user1);

        //then
        assertThat(followRequestList).isEmpty();
    }
}
