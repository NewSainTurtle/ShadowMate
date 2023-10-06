package com.newsainturtle.shadowmate.kh.follow;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FollowRepositoryTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    public void init() {
        userRepository.save(User.builder()
                .email("test1@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이1")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        userRepository.save(User.builder()
                .email("test2@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이2")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Nested
    class 팔로잉TEST {

        @Test
        @DisplayName("팔로잉 NULL 조회")
        void 실패_팔로잉조회_NULL() {
            //given
            final Long userId = 1L;
            final User user = userRepository.findByEmail("test1234@naver.com");

            //when
            final List<Follow> followingList = followRepository.findAllByFollowerId(user);

            //then
            assertThat(followingList).isEmpty();
        }

        @Test
        void 성공_팔로잉조회() {
            //given
            final Long userId = 1L;
            final User user1 = userRepository.findByEmail("test1@naver.com");        final User user2 = userRepository.findByEmail("test2@naver.com");
            followRepository.save(Follow.builder()
                    .followerId(user1)
                    .followingId(user2)
                    .build());

            //when
            final List<Follow> followingList = followRepository.findAllByFollowerId(user1);

            //then
            assertThat(followingList.get(0).getFollowingId().getId()).isEqualTo(user2.getId());

        }
    }
}