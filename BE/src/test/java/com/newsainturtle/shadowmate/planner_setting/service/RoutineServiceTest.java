package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.dto.request.AddRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.RemoveRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.UpdateRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.response.GetRoutineListResponse;
import com.newsainturtle.shadowmate.planner_setting.entity.Routine;
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineDay;
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineTodo;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineDayRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineTodoRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineServiceTest extends DateCommonService {

    @InjectMocks
    private RoutineServiceImpl routineService;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private DailyPlannerRepository dailyPlannerRepository;

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private RoutineTodoRepository routineTodoRepository;

    @Mock
    private RoutineDayRepository routineDayRepository;

    private final User user = User.builder()
            .id(1L)
            .email("yntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    private final Routine routine = Routine.builder()
            .id(1L)
            .routineContent("아침운동하기")
            .startDay("2023-12-25")
            .endDay("2024-01-09")
            .user(user)
            .category(null)
            .routineDays(new ArrayList<>())
            .routineTodos(new ArrayList<>())
            .build();
    private final List<String> days = Arrays.asList(new String[]{"월", "수"});

    @Nested
    class 루틴등록 {

        @Test
        void 실패_유효하지않은요일() {
            //given
            final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                    .routineContent(routine.getRoutineContent())
                    .startDay("2023-12-25")
                    .endDay("2024-01-09")
                    .categoryId(0L)
                    .days(Arrays.asList(new String[]{"디", "수"}))
                    .build();
            doReturn(routine).when(routineRepository).save(any(Routine.class));

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> routineService.addRoutine(user, null, addRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
        }

        @Test
        void 실패_요일중복() {
            //given
            final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                    .routineContent(routine.getRoutineContent())
                    .startDay("2023-12-25")
                    .endDay("2024-01-09")
                    .categoryId(0L)
                    .days(Arrays.asList(new String[]{"월", "수", "월"}))
                    .build();
            final RoutineTodo routineTodo = RoutineTodo.builder()
                    .id(1L)
                    .day("월")
                    .dailyPlannerDay("2023-12-25")
                    .todo(null)
                    .build();
            final RoutineDay routineDay = RoutineDay.builder()
                    .id(1L)
                    .day("월")
                    .build();
            doReturn(routine).when(routineRepository).save(any(Routine.class));
            doReturn(routineTodo).when(routineTodoRepository).save(any(RoutineTodo.class));

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> routineService.addRoutine(user, null, addRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
        }

        @Test
        void 성공() {
            //given
            final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                    .routineContent(routine.getRoutineContent())
                    .startDay(routine.getStartDay())
                    .endDay(routine.getEndDay())
                    .categoryId(0L)
                    .days(days)
                    .build();
            final RoutineTodo routineTodo = RoutineTodo.builder()
                    .id(1L)
                    .day("월")
                    .dailyPlannerDay("2023-12-25")
                    .todo(null)
                    .build();
            final RoutineDay routineDay = RoutineDay.builder()
                    .id(1L)
                    .day("월")
                    .build();
            doReturn(routine).when(routineRepository).save(any(Routine.class));
            doReturn(routineTodo).when(routineTodoRepository).save(any(RoutineTodo.class));
            doReturn(routineDay).when(routineDayRepository).save(any(RoutineDay.class));

            //when
            routineService.addRoutine(user, null, addRoutineRequest);

            //then

            //verify
            verify(routineRepository, times(1)).save(any(Routine.class));
            verify(routineTodoRepository, times(5)).save(any(RoutineTodo.class));
            verify(routineDayRepository, times(2)).save(any(RoutineDay.class));
        }
    }

    @Test
    void 루틴목록조회_성공() {
        //given
        final Routine[] routines = new Routine[]{routine};
        final RoutineDay routineDay1 = RoutineDay.builder()
                .id(1L)
                .routine(routine)
                .day("월")
                .build();
        final RoutineDay routineDay2 = RoutineDay.builder()
                .id(1L)
                .routine(routine)
                .day("수")
                .build();
        final RoutineDay[] routineDays = new RoutineDay[]{routineDay1, routineDay2};

        doReturn(routines).when(routineRepository).findAllByUser(any());
        doReturn(routineDays).when(routineDayRepository).findAllByRoutine(any(Routine.class));

        //when
        final GetRoutineListResponse getRoutineListResponse = routineService.getRoutineList(user);

        //then
        assertThat(getRoutineListResponse).isNotNull();
        assertThat(getRoutineListResponse.getRoutineList()).isNotNull().hasSize(1);

        //verify
        verify(routineRepository, times(1)).findAllByUser(any());
        verify(routineDayRepository, times(1)).findAllByRoutine(any(Routine.class));
    }

    @Nested
    class 루틴삭제 {
        final String[] days = new String[]{"월", "수"};

        @Test
        void 실패_유효하지않은루틴() {
            //given
            final RemoveRoutineRequest removeRoutineRequest = RemoveRoutineRequest.builder()
                    .routineId(0L)
                    .order(1)
                    .build();
            doReturn(null).when(routineRepository).findByIdAndUser(any(Long.class), any());

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> routineService.removeRoutine(user, removeRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE);
        }

        @Test
        void 실패_올바르지않은order값() {
            //given
            final RemoveRoutineRequest removeRoutineRequest = RemoveRoutineRequest.builder()
                    .routineId(0L)
                    .order(4)
                    .build();
            doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> routineService.removeRoutine(user, removeRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ORDER);
        }

        @Test
        void 성공_1() {
            //given
            final RemoveRoutineRequest removeRoutineRequest = RemoveRoutineRequest.builder()
                    .routineId(0L)
                    .order(1)
                    .build();
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(1L)
                    .dailyPlannerDay("2023-12-25")
                    .user(user)
                    .build();
            final Todo todo = Todo.builder()
                    .id(1L)
                    .category(null)
                    .todoContent(routine.getRoutineContent())
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .todoIndex(100000D)
                    .build();
            final RoutineTodo routineTodo = RoutineTodo.builder()
                    .id(1L)
                    .day("월")
                    .dailyPlannerDay("2023-12-25")
                    .todo(todo)
                    .build();
            routineTodo.setRoutine(routine);

            final RoutineTodo[] routineTodoList = new RoutineTodo[]{routineTodo};
            doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
            doReturn(routineTodoList).when(routineTodoRepository).findAllByRoutineAndTodoIsNotNull(any(Routine.class));

            //when
            routineService.removeRoutine(user, removeRoutineRequest);

            //then

            //verify
            verify(routineRepository, times(1)).findByIdAndUser(any(Long.class), any());
            verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNotNull(any(Routine.class));
            verify(routineRepository, times(1)).deleteByIdAndUser(any(Long.class), any());
        }

        @Test
        void 성공_2() {
            //given
            final RemoveRoutineRequest removeRoutineRequest = RemoveRoutineRequest.builder()
                    .routineId(0L)
                    .order(2)
                    .build();
            final DailyPlanner dailyPlanner1 = DailyPlanner.builder()
                    .id(1L)
                    .dailyPlannerDay("2023-12-20")
                    .user(user)
                    .build();
            final Todo todo = Todo.builder()
                    .id(1L)
                    .category(null)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner1)
                    .todoIndex(100000D)
                    .timeTables(new ArrayList<>())
                    .build();
            final RoutineTodo routineTodo1 = RoutineTodo.builder()
                    .id(1L)
                    .day("수")
                    .dailyPlannerDay("2023-12-20")
                    .todo(todo)
                    .build();
            routineTodo1.setRoutine(routine);
            final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                    .id(1L)
                    .dailyPlannerDay("2023-12-25")
                    .user(user)
                    .build();
            final RoutineTodo routineTodo2 = RoutineTodo.builder()
                    .id(1L)
                    .day("월")
                    .dailyPlannerDay("2023-12-25")
                    .todo(null)
                    .build();
            routineTodo2.setRoutine(routine);
            final RoutineTodo[] routineTodoList1 = new RoutineTodo[]{routineTodo1};
            final RoutineTodo[] routineTodoList2 = new RoutineTodo[]{routineTodo2};

            doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
            doReturn(routineTodoList1).when(routineTodoRepository).findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqual(any(Routine.class), any(String.class));
            doReturn(routineTodoList2).when(routineTodoRepository).findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThan(any(Routine.class), any(String.class));
            doReturn(dailyPlanner2).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
            doReturn(null).when(todoRepository).findTopByDailyPlannerOrderByTodoIndexDesc(any(DailyPlanner.class));

            //when
            routineService.removeRoutine(user, removeRoutineRequest);

            //then

            //verify
            verify(routineRepository, times(1)).findByIdAndUser(any(Long.class), any());
            verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqual(any(Routine.class), any(String.class));
            verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThan(any(Routine.class), any(String.class));
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(String.class));
            verify(todoRepository, times(1)).findTopByDailyPlannerOrderByTodoIndexDesc(any(DailyPlanner.class));
            verify(routineRepository, times(1)).deleteByIdAndUser(any(Long.class), any());
        }

        @Test
        void 성공_3() {
            //given
            final RemoveRoutineRequest removeRoutineRequest = RemoveRoutineRequest.builder()
                    .routineId(0L)
                    .order(3)
                    .build();
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(1L)
                    .dailyPlannerDay("2023-12-25")
                    .user(user)
                    .build();
            final RoutineTodo routineTodo = RoutineTodo.builder()
                    .id(1L)
                    .day("월")
                    .dailyPlannerDay("2023-12-25")
                    .todo(null)
                    .build();
            routineTodo.setRoutine(routine);
            final RoutineTodo[] routineTodoList = new RoutineTodo[]{routineTodo};

            doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
            doReturn(routineTodoList).when(routineTodoRepository).findAllByRoutineAndTodoIsNull(any(Routine.class));
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
            doReturn(null).when(todoRepository).findTopByDailyPlannerOrderByTodoIndexDesc(any(DailyPlanner.class));

            //when
            routineService.removeRoutine(user, removeRoutineRequest);

            //then

            //verify
            verify(routineRepository, times(1)).findByIdAndUser(any(Long.class), any());
            verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNull(any(Routine.class));
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(String.class));
            verify(todoRepository, times(1)).findTopByDailyPlannerOrderByTodoIndexDesc(any(DailyPlanner.class));
            verify(routineRepository, times(1)).deleteByIdAndUser(any(Long.class), any());
        }
    }

    @Nested
    class 루틴수정 {
        final List<String> days = Arrays.asList(new String[]{"월", "수"});
        final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                .routineId(1L)
                .order(1)
                .routineContent("아침운동하기")
                .startDay("2024-01-01")
                .endDay("2024-01-31")
                .days(days)
                .categoryId(0L)
                .build();

        @Test
        void 실패_유효하지않은루틴() {
            //given
            doReturn(null).when(routineRepository).findByIdAndUser(any(Long.class), any());

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> routineService.updateRoutine(user, null, updateRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE);
        }

        @Test
        void 실패_올바르지않은order값() {
            //given
            final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                    .routineId(1L)
                    .order(4)
                    .routineContent("아침운동하기")
                    .startDay("2024-01-01")
                    .endDay("2024-01-31")
                    .days(days)
                    .categoryId(0L)
                    .build();
            doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> routineService.updateRoutine(user, null, updateRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ORDER);
        }

        @Test
        void 실패_유효하지않은요일() {
            //given
            final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                    .routineId(1L)
                    .order(1)
                    .routineContent("아침운동하기")
                    .startDay("2024-01-01")
                    .endDay("2024-01-31")
                    .days(Arrays.asList(new String[]{"디", "수"}))
                    .categoryId(0L)
                    .build();
            doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> routineService.updateRoutine(user, null, updateRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
        }

        @Test
        void 실패_요일중복() {
            //given
            final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                    .routineId(1L)
                    .order(1)
                    .routineContent("아침운동하기")
                    .startDay("2024-01-01")
                    .endDay("2024-01-31")
                    .days(Arrays.asList(new String[]{"월", "월", "수"}))
                    .categoryId(0L)
                    .build();
            doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> routineService.updateRoutine(user, null, updateRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
        }

        @Test
        void 성공_order1_모두수정() {
            //given
            final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                    .routineId(1L)
                    .order(1)
                    .routineContent("저녁운동하기")
                    .startDay("2023-12-25")
                    .endDay("2024-01-09")
                    .days(days)
                    .categoryId(0L)
                    .build();

            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(1L)
                    .dailyPlannerDay("2023-12-25")
                    .user(user)
                    .build();
            final Todo todo = Todo.builder()
                    .id(1L)
                    .category(null)
                    .todoContent(routine.getRoutineContent())
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .todoIndex(100000D)
                    .build();
            final RoutineDay routineDay1 = RoutineDay.builder()
                    .id(1L)
                    .day("월")
                    .build();
            final RoutineDay routineDay2 = RoutineDay.builder()
                    .id(2L)
                    .day("수")
                    .build();
            routineDay1.setRoutine(routine);
            routineDay2.setRoutine(routine);
            final RoutineTodo routineTodo = RoutineTodo.builder()
                    .id(1L)
                    .day("월")
                    .dailyPlannerDay("2023-12-25")
                    .todo(todo)
                    .build();
            routineTodo.setRoutine(routine);

            final RoutineDay[] routineDayList = new RoutineDay[]{routineDay1, routineDay2};
            final RoutineTodo[] routineTodoList = new RoutineTodo[]{routineTodo};
            doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
            doReturn(routineDayList).when(routineDayRepository).findAllByRoutine(any(Routine.class));
            doReturn(routineTodoList).when(routineTodoRepository).findAllByRoutineAndTodoIsNotNullAndDayIn(any(Routine.class), any(List.class));

            //when
            routineService.updateRoutine(user, null, updateRoutineRequest);

            //then

            //verify
            verify(routineRepository, times(1)).findByIdAndUser(any(Long.class), any());
            verify(routineDayRepository, times(1)).findAllByRoutine(any(Routine.class));
        }

        @Test
        void 성공_order2_오늘이후수정() {
            //given
            final LocalDate today = LocalDate.now();
            final Routine routine = Routine.builder()
                    .id(1L)
                    .routineContent("아침운동하기")
                    .startDay("2023-12-25")
                    .endDay(String.valueOf(today))
                    .user(user)
                    .category(null)
                    .routineDays(new ArrayList<>())
                    .routineTodos(new ArrayList<>())
                    .build();
            final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                    .routineId(1L)
                    .order(2)
                    .routineContent("저녁운동하기")
                    .startDay("2023-12-25")
                    .endDay(String.valueOf(today.plusDays(10)))
                    .days(days)
                    .categoryId(0L)
                    .build();
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(1L)
                    .dailyPlannerDay("2023-12-25")
                    .user(user)
                    .build();
            final Todo todo = Todo.builder()
                    .id(1L)
                    .category(null)
                    .todoContent(routine.getRoutineContent())
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .todoIndex(100000D)
                    .build();
            final RoutineDay routineDay1 = RoutineDay.builder()
                    .id(1L)
                    .day("월")
                    .build();
            final RoutineDay routineDay2 = RoutineDay.builder()
                    .id(2L)
                    .day("수")
                    .build();
            routineDay1.setRoutine(routine);
            routineDay2.setRoutine(routine);
            final RoutineTodo routineTodo1 = RoutineTodo.builder()
                    .id(1L)
                    .day("월")
                    .dailyPlannerDay("2023-12-25")
                    .todo(todo)
                    .build();
            final RoutineTodo routineTodo2 = RoutineTodo.builder()
                    .id(2L)
                    .day("월")
                    .dailyPlannerDay(String.valueOf(today))
                    .todo(todo)
                    .build();
            final RoutineTodo routineTodo3 = RoutineTodo.builder()
                    .id(3L)
                    .day("월")
                    .dailyPlannerDay(String.valueOf(today.plusDays(7)))
                    .todo(todo)
                    .build();
            routineTodo1.setRoutine(routine);
            routineTodo2.setRoutine(routine);
            routineTodo3.setRoutine(routine);

            doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
            doReturn(new RoutineDay[]{routineDay1, routineDay2}).when(routineDayRepository).findAllByRoutine(any(Routine.class));
            doReturn(new RoutineTodo[]{routineTodo1}).when(routineTodoRepository).findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThanAndDayIn(any(Routine.class), any(String.class), any(List.class));
            doReturn(new RoutineTodo[]{routineTodo2}).when(routineTodoRepository).findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqualAndDayIn(any(Routine.class), any(String.class), any(List.class));
            doReturn(routineTodo3).when(routineTodoRepository).save(any());

            //when
            routineService.updateRoutine(user, null, updateRoutineRequest);

            //then

            //verify
            verify(routineRepository, times(1)).findByIdAndUser(any(Long.class), any());
            verify(routineDayRepository, times(1)).findAllByRoutine(any(Routine.class));
            verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThanAndDayIn(any(Routine.class), any(String.class), any(List.class));
            verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqualAndDayIn(any(Routine.class), any(String.class), any(List.class));
        }

    }

    @Nested
    class 루틴할일삭제 {
        final String date = "2023-09-25";
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .id(1L)
                .dailyPlannerDay(date)
                .user(user)
                .build();
        final Todo todo = Todo.builder()
                .id(1L)
                .category(null)
                .todoContent("영단어 암기하기")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .todoIndex(100000D)
                .build();

        @Test
        void 성공_관련루틴없음() {
            //given
            doReturn(null).when(routineTodoRepository).findByTodo(todo);

            //when
            routineService.removeRoutineTodo(todo);

            //then

            //verify
            verify(routineTodoRepository, times(1)).findByTodo(any(Todo.class));
        }

        @Test
        void 성공_관련루틴있음() {
            //given
            final Routine routine = Routine.builder()
                    .id(1L)
                    .startDay("2023-12-25")
                    .endDay("2023-12-30")
                    .routineContent("아침운동")
                    .category(null)
                    .user(user)
                    .routineDays(new ArrayList<>())
                    .routineTodos(new ArrayList<>())
                    .build();
            final RoutineTodo routineTodo = RoutineTodo.builder()
                    .id(1L)
                    .todo(todo)
                    .dailyPlannerDay("2023-12-25")
                    .day("월")
                    .routine(routine)
                    .build();

            doReturn(routineTodo).when(routineTodoRepository).findByTodo(todo);

            //when
            routineService.removeRoutineTodo(todo);

            //then

            //verify
            verify(routineTodoRepository, times(1)).findByTodo(any(Todo.class));
            verify(routineTodoRepository, times(1)).deleteById(any(Long.class));
        }

    }
}