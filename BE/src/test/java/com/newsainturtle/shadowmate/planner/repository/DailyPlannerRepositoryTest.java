package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
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
class DailyPlannerRepositoryTest {

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private DailyPlanner dailyPlanner;
    private final String date = "2023-09-25";

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(date)
                .user(user)
                .build();
    }

    @Test
    void 일일플래너등록() {
        //given
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(date)
                .user(user)
                .build();

        //when
        final DailyPlanner saveDailyPlanner = dailyPlannerRepository.save(dailyPlanner);

        //then
        assertThat(saveDailyPlanner).isNotNull();
        assertThat(saveDailyPlanner.getDailyPlannerDay()).isEqualTo(date);
        assertThat(saveDailyPlanner.getUser()).isEqualTo(user);
        assertThat(saveDailyPlanner.getRetrospection()).isNull();
        assertThat(saveDailyPlanner.getRetrospectionImage()).isNull();
        assertThat(saveDailyPlanner.getTodayGoal()).isNull();
        assertThat(saveDailyPlanner.getTomorrowGoal()).isNull();
    }

    @Nested
    class 일일플래너_USER로_조회 {
        @Test
        void 개별조회_일일플래너없음_Null() {
            //given

            //when
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, date);

            //then
            assertThat(findDailyPlanner).isNull();
        }

        @Test
        void 개별조회_일일플래너있음() {
            //given
            dailyPlannerRepository.save(dailyPlanner);

            //when
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, date);

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(date);
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
        }

        @Test
        void 전체조회() {
            //given
            dailyPlannerRepository.save(dailyPlanner);
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay("2023-09-26")
                    .user(user)
                    .build());

            //when
            final List<DailyPlanner> dailyPlanners = dailyPlannerRepository.findAllByUser(user);

            //then
            assertThat(dailyPlanners).isNotNull().hasSize(2);
        }
    }

}
