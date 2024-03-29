package com.newsainturtle.shadowmate.planner_setting.repository;

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

import java.util.ArrayList;

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
                .routineDays(new ArrayList<>())
                .routineTodos(new ArrayList<>())
                .build());
    }

    @Test
    void 루틴요일등록() {
        //given
        final RoutineDay routineDay = RoutineDay.builder()
                .day("월")
                .build();

        //when
        final RoutineDay saveRoutineDay = routineDayRepository.save(routineDay);
        saveRoutineDay.setRoutine(routine);

        //then
        assertThat(saveRoutineDay).isNotNull();
        assertThat(saveRoutineDay.getDay()).isEqualTo("월");
        assertThat(saveRoutineDay.getRoutine()).isEqualTo(routine);
    }

    @Test
    void 루틴요일조회() {
        //given
        final RoutineDay routineDay1 = routineDayRepository.save(RoutineDay.builder()
                .day("월")
                .build());
        routineDay1.setRoutine(routine);
        final RoutineDay routineDay2 = routineDayRepository.save(RoutineDay.builder()
                .day("수")
                .build());
        routineDay2.setRoutine(routine);

        //when
        final RoutineDay[] findRoutineDay = routineDayRepository.findAllByRoutine(routine);

        //then
        assertThat(findRoutineDay).isNotNull().hasSize(2);
    }

}