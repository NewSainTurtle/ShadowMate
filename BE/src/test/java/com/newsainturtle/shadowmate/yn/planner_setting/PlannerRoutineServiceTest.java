package com.newsainturtle.shadowmate.yn.planner_setting;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner_setting.dto.request.AddRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.RemoveCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.UpdateRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.response.AddRoutineResponse;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.entity.Routine;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerRoutineServiceImpl;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingServiceImpl;
import com.newsainturtle.shadowmate.planner_setting.service.RoutineServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlannerRoutineServiceTest extends DateCommonService {

    @InjectMocks
    private PlannerRoutineServiceImpl plannerRoutineService;

    @Mock
    private PlannerSettingServiceImpl plannerSettingService;

    @Mock
    private DailyPlannerServiceImpl dailyPlannerService;

    @Mock
    private RoutineServiceImpl routineService;

    private final User user = User.builder()
            .id(1L)
            .email("yntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    final Routine routine = Routine.builder()
            .id(1L)
            .routineContent("아침운동하기")
            .startDay("2023-12-25")
            .endDay("2024-01-09")
            .user(user)
            .category(null)
            .routineDays(new ArrayList<>())
            .routineTodos(new ArrayList<>())
            .build();
    final CategoryColor categoryColor = CategoryColor.builder()
            .categoryColorCode("#D9B5D9")
            .build();
    final Category category = Category.builder()
            .id(1L)
            .categoryColor(categoryColor)
            .user(user)
            .categoryTitle("국어")
            .categoryRemove(false)
            .categoryEmoticon("🍅")
            .build();
    final List<String> days = Arrays.asList(new String[]{"월", "수"});

    @Nested
    class 카테고리삭제 {
        final RemoveCategoryRequest removeCategoryRequest = RemoveCategoryRequest.builder()
                .categoryId(1L)
                .build();

        @Test
        void 실패_없는카테고리() {
            //given
            doReturn(null).when(plannerSettingService).getCategory(any(User.class), any(Long.class));

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerRoutineService.removeCategory(user, removeCategoryRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
        }


        @Test
        void 성공() {
            //given
            final CategoryColor categoryColor = CategoryColor.builder()
                    .categoryColorCode("#D9B5D9")
                    .build();
            final Category category = Category.builder()
                    .id(1L)
                    .categoryColor(categoryColor)
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build();
            doReturn(category).when(plannerSettingService).getCategory(any(User.class), any(Long.class));
            doReturn(0L).when(dailyPlannerService).getRoutineCount(category);
            doReturn(0L).when(routineService).getRoutineCount(category);

            //when
            plannerRoutineService.removeCategory(user, removeCategoryRequest);

            //then

            //verify
            verify(plannerSettingService, times(1)).getCategory(any(), any(Long.class));
            verify(dailyPlannerService, times(1)).getRoutineCount(any(Category.class));
            verify(routineService, times(1)).getRoutineCount(any(Category.class));
            verify(plannerSettingService, times(1)).removeCategory(any(), any(Category.class), any(Long.class));
        }
    }

    @Nested
    class 루틴등록 {
        final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                .routineContent(routine.getRoutineContent())
                .startDay(routine.getStartDay())
                .endDay(routine.getEndDay())
                .categoryId(0L)
                .days(days)
                .build();

        @Test
        void 실패_시작날짜보다_과거인종료날짜() {
            //given
            final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                    .routineContent(routine.getRoutineContent())
                    .startDay("2023-12-25")
                    .endDay("2023-12-20")
                    .categoryId(0L)
                    .days(days)
                    .build();

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerRoutineService.addRoutine(user, addRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_유효하지않은카테고리() {
            //given
            doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY)).when(plannerSettingService).getCategory(any(User.class), any(Long.class));

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerRoutineService.addRoutine(user, addRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
        }

        @Test
        void 실패_유효하지않은요일() {
            //given
            doReturn(category).when(plannerSettingService).getCategory(any(User.class), any(Long.class));
            doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_ROUTINE_DAY)).when(routineService).addRoutine(any(User.class), any(Category.class), any(AddRoutineRequest.class));

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerRoutineService.addRoutine(user, addRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
        }

        @Test
        void 성공() {
            //given
            doReturn(category).when(plannerSettingService).getCategory(user, addRoutineRequest.getCategoryId());
            doReturn(AddRoutineResponse.builder()
                    .routineId(routine.getId())
                    .build()).when(routineService).addRoutine(any(User.class), any(Category.class), any(AddRoutineRequest.class));

            //when
            AddRoutineResponse addRoutineResponse = plannerRoutineService.addRoutine(user, addRoutineRequest);

            //then
            assertThat(addRoutineResponse).isNotNull();
            assertThat(addRoutineResponse.getRoutineId()).isEqualTo(routine.getId());

            //verify
            verify(plannerSettingService, times(1)).getCategory(any(User.class), any(Long.class));
            verify(routineService, times(1)).addRoutine(any(User.class), any(Category.class), any(AddRoutineRequest.class));
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
        void 실패_시작날짜보다_과거인종료날짜() {
            //given
            final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                    .routineId(1L)
                    .order(1)
                    .routineContent("아침운동하기")
                    .startDay("2023-12-25")
                    .endDay("2023-12-20")
                    .days(days)
                    .categoryId(0L)
                    .build();

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerRoutineService.updateRoutine(user, updateRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_유효하지않은카테고리() {
            //given
            doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY)).when(plannerSettingService).getCategory(any(User.class), any(Long.class));

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerRoutineService.updateRoutine(user, updateRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
        }

        @Test
        void 실패_유효하지않은루틴() {
            //given
            doReturn(category).when(plannerSettingService).getCategory(any(User.class), any(Long.class));
            doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_ROUTINE)).when(routineService).updateRoutine(any(User.class), any(Category.class), any(UpdateRoutineRequest.class));

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerRoutineService.updateRoutine(user, updateRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE);
        }

        @Test
        void 실패_올바르지않은order값() {
            //given
            doReturn(category).when(plannerSettingService).getCategory(any(User.class), any(Long.class));
            doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_ORDER)).when(routineService).updateRoutine(any(User.class), any(Category.class), any(UpdateRoutineRequest.class));

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerRoutineService.updateRoutine(user, updateRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ORDER);
        }

        @Test
        void 실패_유효하지않은요일() {
            //given
            doReturn(category).when(plannerSettingService).getCategory(any(User.class), any(Long.class));
            doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_ROUTINE_DAY)).when(routineService).updateRoutine(any(User.class), any(Category.class), any(UpdateRoutineRequest.class));

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerRoutineService.updateRoutine(user, updateRoutineRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
        }

        @Test
        void 성공() {
            //given
            doReturn(category).when(plannerSettingService).getCategory(any(User.class), any(Long.class));

            //when
            plannerRoutineService.updateRoutine(user, updateRoutineRequest);

            //then

            //verify
            verify(plannerSettingService, times(1)).getCategory(any(User.class), any(Long.class));
            verify(routineService, times(1)).updateRoutine(any(User.class), any(Category.class), any(UpdateRoutineRequest.class));
        }

    }

    @Nested
    class 일일플래너할일삭제 {

        final String date = "2023-09-25";
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .id(1L)
                .dailyPlannerDay(date)
                .user(user)
                .build();
        final Todo todo = Todo.builder()
                .id(1L)
                .category(category)
                .todoContent("영단어 암기하기")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .todoIndex(100000D)
                .build();
        final RemoveDailyTodoRequest removeDailyTodoRequest = RemoveDailyTodoRequest.builder()
                .date(date)
                .todoId(1L)
                .build();

        @Test
        void 실패_유효하지않은일일플래너() {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerService).getDailyPlanner(any(User.class), any(String.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> plannerRoutineService.removeDailyTodo(user, removeDailyTodoRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }

        @Test
        void 실패_유효하지않은할일() {
            //given
            doReturn(dailyPlanner).when(dailyPlannerService).getDailyPlanner(any(User.class), any(String.class));
            doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO)).when(dailyPlannerService).getTodo(any(Long.class), any(DailyPlanner.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> plannerRoutineService.removeDailyTodo(user, removeDailyTodoRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
        }

        @Test
        void 성공() {
            //given
            doReturn(dailyPlanner).when(dailyPlannerService).getDailyPlanner(any(User.class), any(String.class));
            doReturn(todo).when(dailyPlannerService).getTodo(any(Long.class), any(DailyPlanner.class));

            //when
            plannerRoutineService.removeDailyTodo(user, removeDailyTodoRequest);

            //then

            //verify
            verify(dailyPlannerService, times(1)).getDailyPlanner(any(User.class), any(String.class));
            verify(dailyPlannerService, times(1)).getTodo(any(Long.class), any(DailyPlanner.class));
            verify(routineService, times(1)).removeRoutineTodo(any(Todo.class));
            verify(dailyPlannerService, times(1)).removeDailyTodo(any(Long.class));
        }

    }
}