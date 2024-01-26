package com.newsainturtle.shadowmate.kh.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
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
            .plannerAccessScope(PlannerAccessScope.PRIVATE)
            .withdrawal(false)
            .build();

    final FollowRequest followRequest = FollowRequest.builder()
            .requester(user1)
            .receiver(user2)
            .build();

    @BeforeEach
    public void init() {
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void 성공_팔로우신청_조회() {
        //given

        //when
        followRequestRepository.save(followRequest);
        final FollowRequest result = followRequestRepository.findByRequesterAndReceiver(user1, user2);

        //then
        assertThat(result.getReceiver().getNickname()).isEqualTo(user2.getNickname());
    }
    
    @Test
    void 성공_받은친구신청조회() {
        //given
        
        //when
        followRequestRepository.save(followRequest);
        List<FollowRequest> followRequests = followRequestRepository.findAllByReceiver(user2);
        
        //then
        assertThat(followRequests.get(0).getRequester()).isEqualTo(user1);
    }
    

    @Test
    void 성공_팔로우신청_비공개() {
        //given

        //when
        FollowRequest result = followRequestRepository.save(followRequest);

        //then
        assertThat(result.getRequester()).isEqualTo(user1);
        assertThat(result.getReceiver()).isEqualTo(user2);
    }

    @Test
    void 성공_친구신청취소() {
        // given
        followRequestRepository.save(followRequest);

        // when
        followRequestRepository.deleteByRequesterAndReceiver(user1, user2);
        final FollowRequest result = followRequestRepository.findByRequesterAndReceiver(user1, user2);

        // then
        assertThat(result).isNull();
    }

    @Test
    void 성공_친구신청거절() {
        //given
        followRequestRepository.save(FollowRequest.builder()
                .requester(user1)
                .receiver(user2)
                .build());

        //when
        followRequestRepository.deleteByRequesterAndReceiver(user1, user2);
        final FollowRequest result = followRequestRepository.findByRequesterAndReceiver(user1, user2);

        //then
        assertThat(result).isNull();
    }

    @Test
    void 성공_친구신청수락() {
        //given
        followRequestRepository.save(FollowRequest.builder()
                .requester(user1)
                .receiver(user2)
                .build());

        //when
        final FollowRequest followRequest = followRequestRepository.findByRequesterAndReceiver(user1, user2);
        followRequestRepository.deleteByRequesterAndReceiver(user1, user2);
        final Follow follow = Follow.builder()
                .follower(followRequest.getRequester())
                .following(followRequest.getReceiver())
                .build();
        followRepository.save(follow);
        final Follow result = followRepository.findByFollowingAndFollower(user2, user1);

        //then
        assertThat(result.getFollower().getNickname()).isEqualTo(user1.getNickname());
        assertThat(result.getFollowing().getNickname()).isEqualTo(user2.getNickname());

    }

    @Test
    void 유저와관련된팔로우신청삭제() {
        // given
        followRequestRepository.save(FollowRequest.builder()
                .requester(user1)
                .receiver(user2)
                .build());
        followRequestRepository.save(FollowRequest.builder()
                .requester(user2)
                .receiver(user1)
                .build());

        // when
        followRequestRepository.deleteAllByRequesterOrReceiver(user1, user1);
        final List<FollowRequest> result1 = followRequestRepository.findAllByReceiver(user1);
        final FollowRequest result2 = followRequestRepository.findByRequesterAndReceiver(user1, user2);

        // then
        assertThat(result1).isEmpty();;
        assertThat(result2).isNull();
    }
}
