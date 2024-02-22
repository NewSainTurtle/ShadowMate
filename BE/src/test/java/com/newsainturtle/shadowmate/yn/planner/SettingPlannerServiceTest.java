package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.planner.dto.request.AddDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.response.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.SettingPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingService;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingPlannerServiceTest {

    @InjectMocks
    private SettingPlannerServiceImpl settingPlannerService;

    @Mock
    private DailyPlannerServiceImpl dailyPlannerService;

    @Mock
    private PlannerSettingService plannerSettingService;

    private final User user = User.builder()
            .id(1L)
            .email("yntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .email("jntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("토끼")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    private final String date = "2023-09-25";

    @Nested
    class 일일플래너할일 {
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
        final String todoContent = "수능완성 수학 과목별 10문제";

        @Nested
        class 일일플래너할일등록 {

            final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                    .date(date)
                    .categoryId(1L)
                    .todoContent(todoContent)
                    .build();

            @Test
            void 실패_유효하지않은카테고리ID() {
                //given
                doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY)).when(plannerSettingService).getCategory(user, addDailyTodoRequest.getCategoryId());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> settingPlannerService.addDailyTodo(user, addDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
            }

            @Test
            void 성공() {
                //given
                final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                        .date(date)
                        .categoryId(0L)
                        .todoContent(todoContent)
                        .build();

                doReturn(category).when(plannerSettingService).getCategory(user, addDailyTodoRequest.getCategoryId());
                doReturn(AddDailyTodoResponse.builder().todoId(1L).build()).when(dailyPlannerService).addDailyTodo(user, category, addDailyTodoRequest);

                //when
                final AddDailyTodoResponse addDailyTodoResponse = settingPlannerService.addDailyTodo(user, addDailyTodoRequest);

                //then
                assertThat(addDailyTodoResponse.getTodoId()).isNotNull().isEqualTo(1L);

                //verify
                verify(plannerSettingService, times(1)).getCategory(any(User.class), any(Long.class));
                verify(dailyPlannerService, times(1)).addDailyTodo(any(User.class), any(Category.class), any(AddDailyTodoRequest.class));
            }

        }

        @Nested
        class 일일플래너할일수정 {

            final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                    .date(date)
                    .todoId(1L)
                    .todoContent(todoContent)
                    .categoryId(0L)
                    .todoStatus("완료")
                    .build();


            @Test
            void 실패_유효하지않은카테고리() {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date(date)
                        .todoId(1L)
                        .todoContent(todoContent)
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();
                doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY)).when(plannerSettingService).getCategory(user, updateDailyTodoRequest.getCategoryId());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> settingPlannerService.updateDailyTodo(user, updateDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
            }

            @Test
            void 실패_유효하지않은할일상태값() {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date(date)
                        .todoId(1L)
                        .todoContent(todoContent)
                        .categoryId(1L)
                        .todoStatus("??")
                        .build();
                doReturn(null).when(plannerSettingService).getCategory(user, updateDailyTodoRequest.getCategoryId());
                doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO_STATUS)).when(dailyPlannerService).updateDailyTodo(user, null, updateDailyTodoRequest);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> settingPlannerService.updateDailyTodo(user, updateDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO_STATUS);
            }


            @Test
            void 실패_유효하지않은일일플래너() {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date(date)
                        .todoId(1L)
                        .todoContent(todoContent)
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();
                doReturn(null).when(plannerSettingService).getCategory(user, updateDailyTodoRequest.getCategoryId());
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerService).updateDailyTodo(user, null, updateDailyTodoRequest);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> settingPlannerService.updateDailyTodo(user, updateDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            void 실패_유효하지않은할일() {
                //given
                doReturn(null).when(plannerSettingService).getCategory(user, updateDailyTodoRequest.getCategoryId());
                doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO)).when(dailyPlannerService).updateDailyTodo(user, null, updateDailyTodoRequest);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> settingPlannerService.updateDailyTodo(user, updateDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }

            @Test
            void 성공() {
                //given
                doReturn(category).when(plannerSettingService).getCategory(user, updateDailyTodoRequest.getCategoryId());

                //when
                settingPlannerService.updateDailyTodo(user, updateDailyTodoRequest);

                //then

                //verify
                verify(plannerSettingService, times(1)).getCategory(any(User.class), any(Long.class));
                verify(dailyPlannerService, times(1)).updateDailyTodo(any(User.class), any(Category.class), any(UpdateDailyTodoRequest.class));
            }

        }

    }

}
