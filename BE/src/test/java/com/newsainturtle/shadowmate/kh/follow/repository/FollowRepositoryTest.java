package com.newsainturtle.shadowmate.kh.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FollowRepositoryTest {

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
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();



    @BeforeEach
    public void init() {
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Nested
    class 팔로잉TEST {

        @Test
        void 실패_팔로잉조회_NULL() {
            //given
            final User user = userRepository.findByEmail("test1@test.com");

            //when
            final List<Follow> followingList = followRepository.findAllByFollowerId(user);

            //then
            assertThat(followingList).isEmpty();
        }

        @Test
        void 성공_팔로잉조회() {
            //given
            final User user1 = userRepository.findByEmail("test1@test.com");
            final User user2 = userRepository.findByEmail("test2@test.com");
            followRepository.save(Follow.builder()
                    .followerId(user1)
                    .followingId(user2)
                    .build());

            //when
            final List<Follow> followingList = followRepository.findAllByFollowerId(user1);

            //then
            assertThat(followingList.get(0).getFollowingId().getId()).isEqualTo(user2.getId());

        }

        @Test
        void 성공_팔로잉삭제() {
            //given
            final Follow follow = Follow.builder().followerId(user1).followingId(user2).build();
            followRepository.save(follow);

            //when
            followRepository.deleteByFollowingIdAndFollowerId(user2, user1);
            final Follow result = followRepository.findByFollowerIdAndFollowingId(user1, user2);

            //then
            assertThat(result).isNull();

        }
    }

    @Nested
    class 팔로우신청TEST {

        @Test
        void 성공_팔로우신청_조회() {
            //given
            followRepository.save(Follow.builder()
                    .followerId(user1)
                    .followingId(user2)
                    .build());

            //when
            final Follow result = followRepository.findByFollowerIdAndFollowingId(user1, user2);
            //then
            assertThat(result.getFollowingId().getNickname()).isEqualTo(user2.getNickname());
        }

        @Test
        void 성공_팔로우신청_전체공개() {
            //given
            followRepository.save(Follow.builder()
                    .followerId(user1)
                    .followingId(user2)
                    .build());

            //when
            final List<Follow> followingList = followRepository.findAllByFollowerId(user1);

            //then
            System.out.println(followingList.get(0).getFollowerId().getNickname());
            assertThat(followingList.get(0).getFollowerId()).isEqualTo(user1);
            assertThat(followingList.get(0).getFollowingId()).isEqualTo(user2);
        }

    }
    @Nested
    class 팔로워TEST {

        @Test
        void 실패_팔로워조회_NULL() {
            //given
            final User user2 = userRepository.findByEmail("test2@test.com");

            //when
            final List<Follow> followList = followRepository.findAllByFollowingId(user2);

            //then
            assertThat(followList).isEmpty();
        }

        @Test
        void 성공_팔로워조회() {
            //given
            Follow follow = followRepository.save(Follow.builder()
                    .followerId(user1)
                    .followingId(user2)
                    .build());

            //when
            final List<Follow> followList = followRepository.findAllByFollowingId(user2);

            //then
            assertThat(followList.get(0).getFollowerId().getNickname()).isEqualTo(user1.getNickname());
        }

        @Test
        void 성공_팔로워삭제() {
            // given
            followRepository.save(Follow.builder()
                    .followerId(user1)
                    .followingId(user2)
                    .build());

            // when
            followRepository.deleteByFollowingIdAndFollowerId(user2, user1);
            final Follow result = followRepository.findByFollowerIdAndFollowingId(user1, user2);

            // then
            assertThat(result).isNull();
        }
    }
}