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

import java.util.ArrayList;
import java.util.List;

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
                .user(user)
                .routineDays(new ArrayList<>())
                .routineTodos(new ArrayList<>())
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
                    .todo(null)
                    .dailyPlannerDay(startDay)
                    .day("월")
                    .build();

            //when
            final RoutineTodo saveRoutineTodo = routineTodoRepository.save(routineTodo);
            saveRoutineTodo.setRoutine(routine);

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
                    .timeTables(new ArrayList<>())
                    .build());
            final RoutineTodo routineTodo = RoutineTodo.builder()
                    .todo(todo)
                    .dailyPlannerDay(startDay)
                    .day("월")
                    .build();

            //when
            final RoutineTodo saveRoutineTodo = routineTodoRepository.save(routineTodo);
            saveRoutineTodo.setRoutine(routine);

            //then
            assertThat(saveRoutineTodo).isNotNull();
            assertThat(saveRoutineTodo.getRoutine()).isEqualTo(routine);
            assertThat(saveRoutineTodo.getTodo()).isEqualTo(todo);
            assertThat(saveRoutineTodo.getDailyPlannerDay()).isEqualTo(startDay);
            assertThat(saveRoutineTodo.getDay()).isEqualTo("월");
        }
    }

    @Nested
    class 루틴_할일_등록안된루틴조회 {
        @Test
        void 데이터없음() {
            //given
            final Todo todo = todoRepository.save(Todo.builder()
                    .category(null)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .todoIndex(100000D)
                    .timeTables(new ArrayList<>())
                    .build());
            final RoutineTodo routineTodo = routineTodoRepository.save(RoutineTodo.builder()
                    .todo(todo)
                    .dailyPlannerDay("2023-12-25")
                    .day("월")
                    .build());
            routineTodo.setRoutine(routine);

            //when
            final RoutineTodo[] routineTodos = routineTodoRepository.findAllByUserAndDailyPlannerDayAndTodoIsNull(user.getId(), "2023-12-25");

            //then
            assertThat(routineTodos).isNotNull().isEmpty();
        }

        @Test
        void 데이터있음() {
            //given
            final RoutineTodo routineTodo = routineTodoRepository.save(RoutineTodo.builder()
                    .todo(null)
                    .dailyPlannerDay("2023-12-25")
                    .day("월")
                    .build());
            routineTodo.setRoutine(routine);

            //when
            final RoutineTodo[] routineTodos = routineTodoRepository.findAllByUserAndDailyPlannerDayAndTodoIsNull(user.getId(), "2023-12-25");

            //then
            assertThat(routineTodos).isNotNull().hasSize(1);
        }
    }

    @Nested
    class 루틴_할일_등록안된루틴_카운트 {
        @Test
        void 데이터없음() {
            //given
            final Todo todo = todoRepository.save(Todo.builder()
                    .category(null)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .todoIndex(100000D)
                    .timeTables(new ArrayList<>())
                    .build());
            final RoutineTodo routineTodo = routineTodoRepository.save(RoutineTodo.builder()
                    .todo(todo)
                    .dailyPlannerDay("2023-12-25")
                    .day("월")
                    .build());
            routineTodo.setRoutine(routine);

            //when
            final int count = routineTodoRepository.countByUserAndDailyPlannerDayAndTodoIsNull(user.getId(), "2023-12-25");

            //then
            assertThat(count).isZero();
        }

        @Test
        void 데이터있음() {
            //given
            final RoutineTodo routineTodo = routineTodoRepository.save(RoutineTodo.builder()
                    .todo(null)
                    .dailyPlannerDay("2023-12-25")
                    .day("월")
                    .build());
            routineTodo.setRoutine(routine);

            //when
            final int count = routineTodoRepository.countByUserAndDailyPlannerDayAndTodoIsNull(user.getId(), "2023-12-25");

            //then
            assertThat(count).isEqualTo(1);
        }
    }

    @Nested
    class 루틴_할일_목록조회 {
        @Nested
        class 루틴_할일_등록안된_목록조회 {

            @Nested
            class 기본_findAllByRoutineAndTodoIsNull {
                @Test
                void 데이터없음() {
                    //given

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNull(routine);

                    //then
                    assertThat(routineTodos).isNotNull().isEmpty();
                }

                @Test
                void 데이터있음() {
                    //given
                    final Todo todo = todoRepository.save(Todo.builder()
                            .category(null)
                            .todoContent("수능완성 수학 과목별 10문제")
                            .todoStatus(TodoStatus.EMPTY)
                            .dailyPlanner(dailyPlanner)
                            .todoIndex(100000D)
                            .timeTables(new ArrayList<>())
                            .build());
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-27")
                            .day("수")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-26")
                            .day("화")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNull(routine);

                    //then
                    assertThat(routineTodos).isNotNull().hasSize(2);
                }
            }

            @Nested
            class 요일_findAllByRoutineAndTodoIsNullAndDay {
                @Test
                void 데이터없음() {
                    //given
                    final Todo todo = todoRepository.save(Todo.builder()
                            .category(null)
                            .todoContent("수능완성 수학 과목별 10문제")
                            .todoStatus(TodoStatus.EMPTY)
                            .dailyPlanner(dailyPlanner)
                            .todoIndex(100000D)
                            .timeTables(new ArrayList<>())
                            .build());
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNullAndDay(routine, "월");

                    //then
                    assertThat(routineTodos).isNotNull().isEmpty();
                }

                @Test
                void 데이터있음() {
                    //given
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-27")
                            .day("수")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-26")
                            .day("화")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNullAndDay(routine, "월");

                    //then
                    assertThat(routineTodos).isNotNull().hasSize(1);
                }
            }

            @Nested
            class 과거_findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThan {
                @Test
                void 데이터없음() {
                    //given
                    final Todo todo = todoRepository.save(Todo.builder()
                            .category(null)
                            .todoContent("수능완성 수학 과목별 10문제")
                            .todoStatus(TodoStatus.EMPTY)
                            .dailyPlanner(dailyPlanner)
                            .todoIndex(100000D)
                            .timeTables(new ArrayList<>())
                            .build());
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThan(routine, "2023-12-26");

                    //then
                    assertThat(routineTodos).isNotNull().isEmpty();
                }

                @Test
                void 데이터있음() {
                    //given
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-27")
                            .day("수")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-26")
                            .day("화")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThan(routine, "2023-12-26");

                    //then
                    assertThat(routineTodos).isNotNull().hasSize(1);
                }
            }

            @Nested
            class 날짜범위와요일들_findAllByRoutineAndTodoIsNullAndDailyPlannerDayBetweenAndDayIn {
                @Test
                void 데이터없음() {
                    //given
                    final Todo todo = todoRepository.save(Todo.builder()
                            .category(null)
                            .todoContent("수능완성 수학 과목별 10문제")
                            .todoStatus(TodoStatus.EMPTY)
                            .dailyPlanner(dailyPlanner)
                            .todoIndex(100000D)
                            .timeTables(new ArrayList<>())
                            .build());
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    final List<String> days = new ArrayList<>();
                    days.add("월");

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNullAndDailyPlannerDayBetweenAndDayIn(routine, "2024-01-01", "2024-01-10", days);

                    //then
                    assertThat(routineTodos).isNotNull().isEmpty();
                }

                @Test
                void 데이터있음() {
                    //given
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-27")
                            .day("수")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-26")
                            .day("화")
                            .build()).setRoutine(routine);
                    final List<String> days = new ArrayList<>();
                    days.add("월");

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNullAndDailyPlannerDayBetweenAndDayIn(routine, "2023-12-25", "2023-12-31", days);

                    //then
                    assertThat(routineTodos).isNotNull().hasSize(1);
                }
            }

        }

        @Nested
        class 루틴_할일_등록된_목록조회 {

            @Nested
            class 기본_findAllByRoutineAndTodoIsNotNull {
                @Test
                void 데이터없음() {
                    //given
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-27")
                            .day("수")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNotNull(routine);

                    //then
                    assertThat(routineTodos).isNotNull().isEmpty();
                }

                @Test
                void 데이터있음() {
                    //given
                    final Todo todo = todoRepository.save(Todo.builder()
                            .category(null)
                            .todoContent("수능완성 수학 과목별 10문제")
                            .todoStatus(TodoStatus.EMPTY)
                            .dailyPlanner(dailyPlanner)
                            .todoIndex(100000D)
                            .timeTables(new ArrayList<>())
                            .build());
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-27")
                            .day("수")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-26")
                            .day("화")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNotNull(routine);

                    //then
                    assertThat(routineTodos).isNotNull().hasSize(1);
                }
            }

            @Nested
            class 요일_findAllByRoutineAndTodoIsNotNullAndDay {
                @Test
                void 데이터없음() {
                    //given
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDay(routine, "월");

                    //then
                    assertThat(routineTodos).isNotNull().isEmpty();
                }

                @Test
                void 데이터있음() {
                    //given
                    final Todo todo = todoRepository.save(Todo.builder()
                            .category(null)
                            .todoContent("수능완성 수학 과목별 10문제")
                            .todoStatus(TodoStatus.EMPTY)
                            .dailyPlanner(dailyPlanner)
                            .todoIndex(100000D)
                            .timeTables(new ArrayList<>())
                            .build());
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-27")
                            .day("수")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-26")
                            .day("화")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDay(routine, "월");

                    //then
                    assertThat(routineTodos).isNotNull().hasSize(1);
                }
            }

            @Nested
            class 요일들_findAllByRoutineAndTodoIsNotNullAndDayIn {
                @Test
                void 데이터없음() {
                    //given
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    final List<String> days = new ArrayList<>();
                    days.add("월");

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDayIn(routine, days);

                    //then
                    assertThat(routineTodos).isNotNull().isEmpty();
                }

                @Test
                void 데이터있음() {
                    //given
                    final Todo todo = todoRepository.save(Todo.builder()
                            .category(null)
                            .todoContent("수능완성 수학 과목별 10문제")
                            .todoStatus(TodoStatus.EMPTY)
                            .dailyPlanner(dailyPlanner)
                            .todoIndex(100000D)
                            .timeTables(new ArrayList<>())
                            .build());
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-27")
                            .day("수")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-26")
                            .day("화")
                            .build()).setRoutine(routine);
                    final List<String> days = new ArrayList<>();
                    days.add("월");
                    days.add("화");

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDayIn(routine, days);

                    //then
                    assertThat(routineTodos).isNotNull().hasSize(1);
                }
            }

            @Nested
            class 오늘과미래_findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqual {
                @Test
                void 데이터없음() {
                    //given
                    final Todo todo = todoRepository.save(Todo.builder()
                            .category(null)
                            .todoContent("수능완성 수학 과목별 10문제")
                            .todoStatus(TodoStatus.EMPTY)
                            .dailyPlanner(dailyPlanner)
                            .todoIndex(100000D)
                            .timeTables(new ArrayList<>())
                            .build());
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-24")
                            .day("일")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqual(routine, "2023-12-25");

                    //then
                    assertThat(routineTodos).isNotNull().isEmpty();
                }

                @Test
                void 데이터있음() {
                    //given
                    final Todo todo = todoRepository.save(Todo.builder()
                            .category(null)
                            .todoContent("수능완성 수학 과목별 10문제")
                            .todoStatus(TodoStatus.EMPTY)
                            .dailyPlanner(dailyPlanner)
                            .todoIndex(100000D)
                            .timeTables(new ArrayList<>())
                            .build());
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-27")
                            .day("수")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-26")
                            .day("화")
                            .build()).setRoutine(routine);

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqual(routine, "2023-12-25");

                    //then
                    assertThat(routineTodos).isNotNull().hasSize(1);
                }
            }

            @Nested
            class 날짜범위와요일들_findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayBetweenAndDayIn {
                @Test
                void 데이터없음() {
                    //given
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    final List<String> days = new ArrayList<>();
                    days.add("월");

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayBetweenAndDayIn(routine, "2024-01-01", "2024-01-10", days);

                    //then
                    assertThat(routineTodos).isNotNull().isEmpty();
                }

                @Test
                void 데이터있음() {
                    //given
                    final Todo todo = todoRepository.save(Todo.builder()
                            .category(null)
                            .todoContent("수능완성 수학 과목별 10문제")
                            .todoStatus(TodoStatus.EMPTY)
                            .dailyPlanner(dailyPlanner)
                            .todoIndex(100000D)
                            .timeTables(new ArrayList<>())
                            .build());
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-25")
                            .day("월")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(todo)
                            .dailyPlannerDay("2023-12-27")
                            .day("수")
                            .build()).setRoutine(routine);
                    routineTodoRepository.save(RoutineTodo.builder()
                            .todo(null)
                            .dailyPlannerDay("2023-12-26")
                            .day("화")
                            .build()).setRoutine(routine);
                    final List<String> days = new ArrayList<>();
                    days.add("월");
                    days.add("수");

                    //when
                    final RoutineTodo[] routineTodos = routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayBetweenAndDayIn(routine, "2023-12-25", "2023-12-31", days);

                    //then
                    assertThat(routineTodos).isNotNull().hasSize(2);
                }
            }

        }
    }

}