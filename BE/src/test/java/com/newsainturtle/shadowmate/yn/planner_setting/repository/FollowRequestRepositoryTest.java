package com.newsainturtle.shadowmate.yn.planner_setting.repository;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FollowRequestRepositoryTest {

    @Autowired
    private FollowRequestRepository followRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private final String password = "yntest1234";
    private final SocialType socialType = SocialType.BASIC;
    private final PlannerAccessScope plannerAccessScope = PlannerAccessScope.PUBLIC;

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password(password)
                .socialLogin(socialType)
                .nickname("거북이")
                .plannerAccessScope(plannerAccessScope)
                .withdrawal(false)
                .build());
    }

    @Test
    void 팔로우요청목록전체삭제_요청0() {
        //given

        //when
        followRequestRepository.deleteAllByReceiverId(user);
        final List<FollowRequest> followRequestList = followRequestRepository.findAllByReceiverId(user);

        //then
        assertThat(followRequestList).isEmpty();
    }

    @Test
    void 팔로우요청목록전체삭제_요청2() {
        //given
        final User user2 = userRepository.save(User.builder()
                .email("jntest@shadowmate.com")
                .password(password)
                .socialLogin(socialType)
                .nickname("토끼")
                .plannerAccessScope(plannerAccessScope)
                .withdrawal(false)
                .build());
        final User user3 = userRepository.save(User.builder()
                .email("nctest@shadowmate.com")
                .password(password)
                .socialLogin(socialType)
                .nickname("고양이")
                .plannerAccessScope(plannerAccessScope)
                .withdrawal(false)
                .build());
        followRequestRepository.save(FollowRequest.builder()
                .receiverId(user)
                .requesterId(user2)
                .build());
        followRequestRepository.save(FollowRequest.builder()
                .receiverId(user)
                .requesterId(user3)
                .build());

        //when
        followRequestRepository.deleteAllByReceiverId(user);
        final List<FollowRequest> followRequestList = followRequestRepository.findAllByReceiverId(user);

        //then
        assertThat(followRequestList).isEmpty();
    }
}
