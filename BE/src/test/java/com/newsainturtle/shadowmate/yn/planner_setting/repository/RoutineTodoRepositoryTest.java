package com.newsainturtle.shadowmate.yn.planner_setting.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.entity.Routine;
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineTodo;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineTodoRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoutineTodoRepositoryTest {

    @Autowired
    private RoutineTodoRepository routineTodoRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    private User user;
    private Routine routine;
    private DailyPlanner dailyPlanner;
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
                .build());
        dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay(startDay)
                .user(user)
                .build());
    }

    @Nested
    class 루틴_할일등록 {
        @Test
        void 할일없음() {
            //given
            final RoutineTodo routineTodo = RoutineTodo.builder()
                    .routine(routine)
                    .todo(null)
                    .dailyPlannerDay(startDay)
                    .day("월")
                    .build();

            //when
            final RoutineTodo saveRoutineTodo = routineTodoRepository.save(routineTodo);

            //then
            assertThat(saveRoutineTodo).isNotNull();
            assertThat(saveRoutineTodo.getRoutine()).isEqualTo(routine);
            assertThat(saveRoutineTodo.getTodo()).isNull();
            assertThat(saveRoutineTodo.getDailyPlannerDay()).isEqualTo(startDay);
            assertThat(saveRoutineTodo.getDay()).isEqualTo("월");
        }

        @Test
        void 할일있음() {
            //given
            final Todo todo = todoRepository.save(Todo.builder()
                    .category(null)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .todoIndex(100000D)
                    .build());
            final RoutineTodo routineTodo = RoutineTodo.builder()
                    .routine(routine)
                    .todo(todo)
                    .dailyPlannerDay(startDay)
                    .day("월")
                    .build();

            //when
            final RoutineTodo saveRoutineTodo = routineTodoRepository.save(routineTodo);

            //then
            assertThat(saveRoutineTodo).isNotNull();
            assertThat(saveRoutineTodo.getRoutine()).isEqualTo(routine);
            assertThat(saveRoutineTodo.getTodo()).isEqualTo(todo);
            assertThat(saveRoutineTodo.getDailyPlannerDay()).isEqualTo(startDay);
            assertThat(saveRoutineTodo.getDay()).isEqualTo("월");
        }
    }

}