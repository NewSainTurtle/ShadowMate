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
public class DailyPlannerLikeRepositoryTest {

    @Autowired
    private DailyPlannerLikeRepository dailyPlannerLikeRepository;

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    public void init() {
        user1 = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        user2 = userRepository.save(User.builder()
                .email("test127@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("토끼")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Test
    public void 좋아요등록() {
        //given
        final DailyPlanner saveDailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf("2023-09-25"))
                .user(user1)
                .build());

        //when
        final DailyPlannerLike saveDailyPlannerLike = dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                .dailyPlanner(saveDailyPlanner)
                .user(user2)
                .build());

        //then
        assertThat(saveDailyPlannerLike).isNotNull();
        assertThat(saveDailyPlannerLike.getDailyPlanner()).isEqualTo(saveDailyPlanner);
        assertThat(saveDailyPlannerLike.getUser()).isEqualTo(user2);
    }

    @Test
    public void 좋아요조회_이전에좋아요안누름() {
        //given
        final DailyPlanner saveDailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf("2023-09-25"))
                .user(user1)
                .build());

        //when
        final DailyPlannerLike dailyPlannerLike = dailyPlannerLikeRepository.findByUserAndDailyPlanner(user2, saveDailyPlanner);

        //then
        assertThat(dailyPlannerLike).isNull();
    }

    @Test
    public void 좋아요조회_이전에좋아요누름() {
        //given
        final DailyPlanner saveDailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf("2023-09-25"))
                .user(user1)
                .build());
        dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                .dailyPlanner(saveDailyPlanner)
                .user(user2)
                .build());

        //when
        final DailyPlannerLike dailyPlannerLike = dailyPlannerLikeRepository.findByUserAndDailyPlanner(user2, saveDailyPlanner);

        //then
        assertThat(dailyPlannerLike).isNotNull();
        assertThat(dailyPlannerLike.getDailyPlanner()).isEqualTo(saveDailyPlanner);
        assertThat(dailyPlannerLike.getUser()).isEqualTo(user2);

    }

    @Test
    public void 좋아요취소() {
        //given
        final DailyPlanner saveDailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf("2023-09-25"))
                .user(user1)
                .build());
        dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                .dailyPlanner(saveDailyPlanner)
                .user(user2)
                .build());

        //when
        dailyPlannerLikeRepository.deleteByUserAndDailyPlanner(user2, saveDailyPlanner);
        final DailyPlannerLike findDailyPlannerLike = dailyPlannerLikeRepository.findByUserAndDailyPlanner(user2, saveDailyPlanner);

        //then
        assertThat(findDailyPlannerLike).isNull();
    }

}
