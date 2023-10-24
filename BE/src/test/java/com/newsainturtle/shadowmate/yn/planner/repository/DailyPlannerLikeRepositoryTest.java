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

import java.sql.Date;

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
                .dailyPlannerDay(Date.valueOf(date))
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
        final DailyPlannerLike dailyPlannerLike = dailyPlannerLikeRepository.findByUserAndDailyPlanner(user2, dailyPlanner);

        //then
        assertThat(dailyPlannerLike).isNull();
    }

    @Test
    void 좋아요조회_이전에좋아요누름() {
        //given
        dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                .dailyPlanner(dailyPlanner)
                .user(user2)
                .build());

        //when
        final DailyPlannerLike dailyPlannerLike = dailyPlannerLikeRepository.findByUserAndDailyPlanner(user2, dailyPlanner);

        //then
        assertThat(dailyPlannerLike).isNotNull();
        assertThat(dailyPlannerLike.getDailyPlanner()).isEqualTo(dailyPlanner);
        assertThat(dailyPlannerLike.getUser()).isEqualTo(user2);

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
        final DailyPlannerLike findDailyPlannerLike = dailyPlannerLikeRepository.findByUserAndDailyPlanner(user2, dailyPlanner);

        //then
        assertThat(findDailyPlannerLike).isNull();
    }

    @Nested
    class 좋아요카운트 {

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
}
