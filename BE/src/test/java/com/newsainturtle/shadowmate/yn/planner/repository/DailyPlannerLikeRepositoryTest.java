package com.newsainturtle.shadowmate.yn.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.DailyPlannerLike;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerLikeRepository;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DailyPlannerLikeRepositoryTest {

    @Autowired
    private DailyPlannerLikeRepository dailyPlannerLikeRepository;

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private DailyPlanner dailyPlanner;

    private final String password = "yntest1234";
    private final SocialType socialType = SocialType.BASIC;
    private final PlannerAccessScope plannerAccessScope = PlannerAccessScope.PUBLIC;
    private final String date = "2023-09-25";

    @BeforeEach
    void init() {
        user1 = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password(password)
                .socialLogin(socialType)
                .nickname("거북이")
                .plannerAccessScope(plannerAccessScope)
                .withdrawal(false)
                .build());
        user2 = userRepository.save(User.builder()
                .email("jntest@shadowmate.com")
                .password(password)
                .socialLogin(socialType)
                .nickname("토끼")
                .plannerAccessScope(plannerAccessScope)
                .withdrawal(false)
                .build());
        dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay(date)
                .user(user1)
                .build());
    }

    @Test
    void 좋아요등록() {
        //given

        //when
        final DailyPlannerLike saveDailyPlannerLike = dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                .dailyPlanner(dailyPlanner)
                .user(user2)
                .build());

        //then
        assertThat(saveDailyPlannerLike).isNotNull();
        assertThat(saveDailyPlannerLike.getDailyPlanner()).isEqualTo(dailyPlanner);
        assertThat(saveDailyPlannerLike.getUser()).isEqualTo(user2);
    }

    @Test
    void 좋아요조회_이전에좋아요안누름() {
        //given

        //when
        final boolean dailyPlannerLike = dailyPlannerLikeRepository.existsByUserAndDailyPlanner(user2, dailyPlanner);

        //then
        assertThat(dailyPlannerLike).isFalse();
    }

    @Test
    void 좋아요조회_이전에좋아요누름() {
        //given
        dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                .dailyPlanner(dailyPlanner)
                .user(user2)
                .build());

        //when
        final boolean dailyPlannerLike = dailyPlannerLikeRepository.existsByUserAndDailyPlanner(user2, dailyPlanner);

        //then
        assertThat(dailyPlannerLike).isTrue();
    }

    @Test
    void 좋아요취소() {
        //given
        dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                .dailyPlanner(dailyPlanner)
                .user(user2)
                .build());

        //when
        dailyPlannerLikeRepository.deleteByUserAndDailyPlanner(user2, dailyPlanner);
        final boolean findDailyPlannerLike = dailyPlannerLikeRepository.existsByUserAndDailyPlanner(user2, dailyPlanner);

        //then
        assertThat(findDailyPlannerLike).isFalse();
    }

    @Nested
    class 좋아요카운트_일별 {

        @Test
        void 좋아요0() {
            //given

            //when
            final long count = dailyPlannerLikeRepository.countByDailyPlanner(dailyPlanner);

            //then
            assertThat(count).isZero();
        }

        @Test
        void 좋아요1() {
            //given
            dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                    .dailyPlanner(dailyPlanner)
                    .user(user2)
                    .build());

            //when
            final long count = dailyPlannerLikeRepository.countByDailyPlanner(dailyPlanner);

            //then
            assertThat(count).isEqualTo(1);
        }

        @Test
        void 좋아요2() {
            //given
            final User user3 = userRepository.save(User.builder()
                    .email("titest@shadowmate.com")
                    .nickname("호랑이")
                    .password(password)
                    .socialLogin(socialType)
                    .plannerAccessScope(plannerAccessScope)
                    .withdrawal(false)
                    .build());
            dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                    .dailyPlanner(dailyPlanner)
                    .user(user2)
                    .build());
            dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                    .dailyPlanner(dailyPlanner)
                    .user(user3)
                    .build());

            //when
            final long count = dailyPlannerLikeRepository.countByDailyPlanner(dailyPlanner);

            //then
            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    class 좋아요카운트_월별 {

        @Test
        void 좋아요0개일때() {
            //given

            //when
            final long count = dailyPlannerLikeRepository.countByDailyPlannerIdIn(new ArrayList<>());

            //then
            assertThat(count).isZero();
        }

        @Test
        void 좋아요1개이상일때() {
            //given
            final List<Long> dailyPlannerIdList = new ArrayList<>();
            final DailyPlanner dailyPlanner1 = dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay("2023-10-31")
                    .user(user1)
                    .build());
            final DailyPlanner dailyPlanner2 = dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay("2023-11-01")
                    .user(user1)
                    .build());
            final DailyPlanner dailyPlanner3 = dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay("2023-11-02")
                    .user(user1)
                    .build());
            final DailyPlanner dailyPlanner4 = dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay("2023-11-03")
                    .user(user1)
                    .build());
            dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                    .dailyPlanner(dailyPlanner1)
                    .user(user2)
                    .build());
            dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                    .dailyPlanner(dailyPlanner2)
                    .user(user2)
                    .build());
            dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                    .dailyPlanner(dailyPlanner3)
                    .user(user2)
                    .build());
            dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                    .dailyPlanner(dailyPlanner4)
                    .user(user2)
                    .build());
            dailyPlannerIdList.add(dailyPlanner2.getId());
            dailyPlannerIdList.add(dailyPlanner3.getId());
            dailyPlannerIdList.add(dailyPlanner4.getId());

            //when
            final long count = dailyPlannerLikeRepository.countByDailyPlannerIdIn(dailyPlannerIdList);

            //then
            assertThat(count).isEqualTo(3);
        }
    }
}
