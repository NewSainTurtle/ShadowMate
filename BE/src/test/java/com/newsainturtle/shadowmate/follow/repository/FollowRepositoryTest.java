package com.newsainturtle.shadowmate.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.Follow;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FollowRepositoryTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void init() {
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
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Nested
    class 팔로잉 {

        @Test
        void 실패_팔로잉조회_NULL() {
            //given

            //when
            final List<Follow> followingList = followRepository.findAllByFollower(user1);

            //then
            assertThat(followingList).isEmpty();
        }

        @Test
        void 성공_팔로잉조회() {
            //given
            followRepository.save(Follow.builder()
                    .follower(user1)
                    .following(user2)
                    .build());

            //when
            final List<Follow> followingList = followRepository.findAllByFollower(user1);

            //then
            assertThat(followingList.get(0).getFollowing().getId()).isEqualTo(user2.getId());

        }

        @Test
        void 성공_팔로잉삭제() {
            //given
            followRepository.save(Follow.builder()
                    .follower(user1)
                    .following(user2)
                    .build());

            //when
            followRepository.deleteByFollowingAndFollower(user2, user1);
            final Follow result = followRepository.findByFollowingAndFollower(user2, user1);

            //then
            assertThat(result).isNull();

        }

        @Test
        void 팔로잉개수조회() {
            // given
            followRepository.save(Follow.builder()
                    .follower(user1)
                    .following(user2)
                    .build());

            // when
            final List<Follow> resultFollow = followRepository.findAllByFollowing(user2);
            final Long result = followRepository.countByFollowing(user2);

            // then
            assertThat(resultFollow).hasSize(1);
            assertThat(result).isEqualTo(1L);
        }
    }

    @Nested
    class 팔로우신청 {

        @Test
        void 성공_팔로우신청_조회() {
            //given
            followRepository.save(Follow.builder()
                    .follower(user1)
                    .following(user2)
                    .build());

            //when
            final Follow result = followRepository.findByFollowingAndFollower(user2, user1);
            //then
            assertThat(result.getFollowing().getNickname()).isEqualTo(user2.getNickname());
        }

        @Test
        void 성공_팔로우신청_전체공개() {
            //given
            followRepository.save(Follow.builder()
                    .follower(user1)
                    .following(user2)
                    .build());

            //when
            final List<Follow> followingList = followRepository.findAllByFollower(user1);

            //then
            assertThat(followingList.get(0).getFollower()).isEqualTo(user1);
            assertThat(followingList.get(0).getFollowing()).isEqualTo(user2);
        }

    }

    @Nested
    class 팔로워 {

        @Test
        void 실패_팔로워조회_NULL() {
            //given

            //when
            final List<Follow> followList = followRepository.findAllByFollowing(user2);

            //then
            assertThat(followList).isEmpty();
        }

        @Test
        void 성공_팔로워조회() {
            //given
            followRepository.save(Follow.builder()
                    .follower(user1)
                    .following(user2)
                    .build());

            //when
            final List<Follow> followList = followRepository.findAllByFollowing(user2);

            //then
            assertThat(followList.get(0).getFollower().getNickname()).isEqualTo(user1.getNickname());
        }

        @Test
        void 성공_팔로워삭제() {
            // given
            followRepository.save(Follow.builder()
                    .follower(user1)
                    .following(user2)
                    .build());

            // when
            followRepository.deleteByFollowingAndFollower(user2, user1);
            final Follow result = followRepository.findByFollowingAndFollower(user2, user1);

            // then
            assertThat(result).isNull();
        }

        @Test
        void 팔로워개수조회() {
            // given
            followRepository.save(Follow.builder()
                    .follower(user1)
                    .following(user2)
                    .build());

            // when
            final List<Follow> resultFollow = followRepository.findAllByFollower(user1);
            final Long result = followRepository.countByFollower(user1);

            // then
            assertThat(resultFollow).hasSize(1);
            assertThat(result).isEqualTo(1L);
        }

        @Test
        void 유저와관련된팔로우삭제() {
            // given
            followRepository.save(Follow.builder()
                    .follower(user1)
                    .following(user2)
                    .build());
            followRepository.save(Follow.builder()
                    .follower(user2)
                    .following(user1)
                    .build());

            // when
            followRepository.deleteAllByFollowingOrFollower(user1, user1);
            List<Follow> result1 = followRepository.findAllByFollower(user1);
            List<Follow> result2 = followRepository.findAllByFollowing(user1);

            // then
            assertThat(result1).isEmpty();
            assertThat(result2).isEmpty();
            ;
        }
    }
}