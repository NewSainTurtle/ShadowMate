package com.newsainturtle.shadowmate.yn.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Routine;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoutineRepositoryTest {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
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
    }

    @Test
    void 루틴등록() {
        //given
        final Routine routine = Routine.builder()
                .startDay(startDay)
                .endDay(endDay)
                .routineContent(routineContent)
                .category(null)
                .user(user)
                .routineDays(new ArrayList<>())
                .routineTodos(new ArrayList<>())
                .build();

        //when
        final Routine saveRoutine = routineRepository.save(routine);

        //then
        assertThat(saveRoutine).isNotNull();
        assertThat(saveRoutine.getStartDay()).isEqualTo(startDay);
        assertThat(saveRoutine.getEndDay()).isEqualTo(endDay);
        assertThat(saveRoutine.getRoutineContent()).isEqualTo(routineContent);
    }

    @Nested
    class 루틴목록조회 {
        @Test
        void 루틴없음() {
            //given

            //when
            final Routine[] findRoutine = routineRepository.findAllByUser(user);

            //then
            assertThat(findRoutine).isNotNull().isEmpty();
        }

        @Test
        void 루틴있음() {
            //given
            routineRepository.save(Routine.builder()
                    .startDay(startDay)
                    .endDay(endDay)
                    .routineContent(routineContent)
                    .category(null)
                    .user(user)
                    .routineDays(new ArrayList<>())
                    .routineTodos(new ArrayList<>())
                    .build());
            routineRepository.save(Routine.builder()
                    .startDay(startDay)
                    .endDay(endDay)
                    .routineContent(routineContent)
                    .category(null)
                    .user(user)
                    .routineDays(new ArrayList<>())
                    .routineTodos(new ArrayList<>())
                    .build());

            //when
            final Routine[] findRoutine = routineRepository.findAllByUser(user);

            //then
            assertThat(findRoutine).isNotNull().hasSize(2);
        }
    }

}