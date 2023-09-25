package com.newsainturtle.shadowmate.yn.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
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
public class DailyPlannerRepositoryTest {

    @Autowired
    DailyPlannerRepository dailyPlannerRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void init() {
        user = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Test
    public void 일일플래너등록() {
        //given
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf("2023-09-25"))
                .user(user)
                .build();

        //when
        final DailyPlanner saveDailyPlanner = dailyPlannerRepository.save(dailyPlanner);

        //then
        assertThat(saveDailyPlanner).isNotNull();
        assertThat(saveDailyPlanner.getDailyPlannerDay()).isEqualTo(Date.valueOf("2023-09-25"));
        assertThat(saveDailyPlanner.getUser()).isEqualTo(user);
        assertThat(saveDailyPlanner.getRetrospection()).isNull();
        assertThat(saveDailyPlanner.getRetrospectionImage()).isNull();
        assertThat(saveDailyPlanner.getTodayGoal()).isNull();
        assertThat(saveDailyPlanner.getTomorrowGoal()).isNull();
    }

    @Nested
    class 일일플래너조회 {
        @Test
        public void 일일플래너없음_Null() {
            //given

            //when
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));

            //then
            assertThat(findDailyPlanner).isNull();
        }

        @Test
        public void 일일플래너있음() {
            //given
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .dailyPlannerDay(Date.valueOf("2023-09-25"))
                    .user(user)
                    .build();
            dailyPlannerRepository.save(dailyPlanner);

            //when
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(Date.valueOf("2023-09-25"));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
        }
    }
}
