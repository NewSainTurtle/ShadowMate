package com.newsainturtle.shadowmate.kh.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
            final User user = userRepository.findByEmailAndSocialLogin("test1@test.com", user1.getSocialLogin());

            //when
            final List<Follow> followingList = followRepository.findAllByFollowerId(user);

            //then
            assertThat(followingList).isEmpty();
        }

        @Test
        void 성공_팔로잉조회() {
            //given
            final User user1 = userRepository.findByEmailAndSocialLogin("test1@test.com", SocialType.BASIC);
            final User user2 = userRepository.findByEmailAndSocialLogin("test2@test.com", SocialType.BASIC);
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

        @Test
        void 팔로잉개수조회() {
            // given
            followRepository.save(Follow.builder()
                    .followerId(user1)
                    .followingId(user2)
                    .build());

            // when
            final List<Follow> resultFollow = followRepository.findAllByFollowingId(user2);
            final Long result = followRepository.countByFollowingId(user2);

            // then
            assertThat(resultFollow).hasSize(1);
            assertThat(result).isEqualTo(1L);
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
            final User user2 = userRepository.findByEmailAndSocialLogin("test2@test.com", SocialType.BASIC);

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

        @Test
        void 팔로워개수조회() {
            // given
            followRepository.save(Follow.builder()
                    .followerId(user1)
                    .followingId(user2)
                    .build());

            // when
            final List<Follow> resultFollow = followRepository.findAllByFollowerId(user1);
            final Long result = followRepository.countByFollowerId(user1);

            // then
            assertThat(resultFollow).hasSize(1);
            assertThat(result).isEqualTo(1L);
        }

        @Test
        void 유저와관련된팔로우삭제() {
            // given
            followRepository.save(Follow.builder()
                    .followerId(user1)
                    .followingId(user2)
                    .build());
            followRepository.save(Follow.builder()
                    .followerId(user2)
                    .followingId(user1)
                    .build());

            // when
            followRepository.deleteAllByFollowingIdOrFollowerId(user1, user1);
            List<Follow> result1 = followRepository.findAllByFollowerId(user1);
            List<Follow> result2 = followRepository.findAllByFollowingId(user1);

            // then
            assertThat(result1).hasSize(0);
            assertThat(result2).hasSize(0);
        }
    }
}