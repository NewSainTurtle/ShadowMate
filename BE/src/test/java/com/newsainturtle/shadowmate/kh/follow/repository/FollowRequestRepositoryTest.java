package com.newsainturtle.shadowmate.kh.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FollowRequestRepositoryTest {

    @Autowired
    private FollowRequestRepository followRequestRepository;

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
            .requesterId(user1)
            .receiverId(user2)
            .build();

    @BeforeEach
    public void init() {
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    public void 성공_팔로우신청_조회() {
        //given

        //when
        followRequestRepository.save(followRequest);
        final FollowRequest result = followRequestRepository.findByRequesterIdAndReceiverId(user1, user2);

        //then
        assertThat(result.getReceiverId().getNickname()).isEqualTo(user2.getNickname());
    }


    @Test
    public void 성공_팔로우신청_비공개() {
        //given

        //when
        FollowRequest result = followRequestRepository.save(followRequest);

        //then
        assertThat(result.getRequesterId()).isEqualTo(user1);
        assertThat(result.getReceiverId()).isEqualTo(user2);
    }
}