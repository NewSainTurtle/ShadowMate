package com.newsainturtle.shadowmate.yn.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Routine;
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineDay;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineDayRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineRepository;
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
class RoutineDayRepositoryTest {

    @Autowired
    private RoutineDayRepository routineDayRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Routine routine;
    private final String startDay = "2023-12-25";
    private final String endDay = "2024-01-05";
    private final String routineContent = "아침운동하기";

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
        routine = routineRepository.save(Routine.builder()
                .startDay(startDay)
                .endDay(endDay)
                .routineContent(routineContent)
                .category(null)
                .user(user)
                .build());
    }

    @Test
    void 루틴요일등록() {
        //given
        final RoutineDay routineDay = RoutineDay.builder()
                .day("월")
                .routine(routine)
                .build();

        //when
        final RoutineDay saveRoutineDay = routineDayRepository.save(routineDay);

        //then
        assertThat(saveRoutineDay).isNotNull();
        assertThat(saveRoutineDay.getDay()).isEqualTo("월");
        assertThat(saveRoutineDay.getRoutine()).isEqualTo(routine);
    }

}