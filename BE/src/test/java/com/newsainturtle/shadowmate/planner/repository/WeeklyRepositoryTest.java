package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.Weekly;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WeeklyRepositoryTest {

    @Autowired
    private WeeklyRepository weeklyRepository;

    @Autowired
    private UserRepository userRepository;

    private final String startDay = "2023-10-09";
    private final String endDay = "2023-10-15";
    private User user;

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
    }

    @Test
    void 주차별플래너등록() {
        //given

        //when
        final Weekly weekly = weeklyRepository.save(Weekly.builder()
                .startDay(startDay)
                .endDay(endDay)
                .user(user)
                .build());

        //then
        assertThat(weekly).isNotNull();
        assertThat(weekly.getUser()).isEqualTo(user);
        assertThat(weekly.getStartDay()).isEqualTo(startDay);
        assertThat(weekly.getEndDay()).isEqualTo(endDay);
    }

    @Test
    void 주차별플래너조회() {
        //given
        weeklyRepository.save(Weekly.builder()
                .startDay(startDay)
                .endDay(endDay)
                .user(user)
                .build());

        //when
        final Weekly findWeekly = weeklyRepository.findByUserAndStartDayAndEndDay(user, startDay, endDay);

        //then
        assertThat(findWeekly).isNotNull();
        assertThat(findWeekly.getUser()).isEqualTo(user);
        assertThat(findWeekly.getStartDay()).isEqualTo(startDay);
        assertThat(findWeekly.getEndDay()).isEqualTo(endDay);
    }

}
