package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.planner.dto.*;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.DailyPlannerLike;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerLikeRepository;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyPlannerServiceTest {

    @InjectMocks
    private DailyPlannerServiceImpl dailyPlannerServiceImpl;

    @Mock
    private DailyPlannerRepository dailyPlannerRepository;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private DailyPlannerLikeRepository dailyPlannerLikeRepository;

    final User user = User.builder()
            .id(1L)
            .email("test@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    final DailyPlanner dailyPlanner = DailyPlanner.builder()
            .id(1L)
            .dailyPlannerDay(Date.valueOf("2023-09-25"))
            .user(user)
            .build();

    @Nested
    class 일일플래너할일등록 {

        @Test
        public void 실패_유효하지않은카테고리ID() {
            //given
            final AddDailyTodoRequest request = AddDailyTodoRequest.builder()
                    .date("2023-09-25")
                    .categoryId(1L)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .build();
            doReturn(null).when(categoryRepository).findByUserAndId(user, request.getCategoryId());

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyTodo(user, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_CATEGORY);
        }

        @Test
        public void 성공_카테고리Null() {
            //given
            final AddDailyTodoRequest request = AddDailyTodoRequest.builder()
                    .date("2023-09-25")
                    .categoryId(0L)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .build();
            final Todo todo = Todo.builder()
                    .id(1L)
                    .category(null)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
            doReturn(todo).when(todoRepository).save(any(Todo.class));

            //when
            final AddDailyTodoResponse addDailyTodoResponse = dailyPlannerServiceImpl.addDailyTodo(user, request);

            //then
            assertThat(addDailyTodoResponse.getTodoId()).isNotNull();

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
            verify(todoRepository, times(1)).save(any(Todo.class));
        }

        @Test
        public void 성공() {
            //given
            final AddDailyTodoRequest request = AddDailyTodoRequest.builder()
                    .date("2023-09-25")
                    .categoryId(1L)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .build();
            final CategoryColor categoryColor = CategoryColor.builder()
                    .categoryColorCode("D9B5D9")
                    .build();
            final Category category = Category.builder()
                    .id(1L)
                    .categoryColor(categoryColor)
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build();
            final Todo todo = Todo.builder()
                    .id(1L)
                    .category(category)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
            doReturn(category).when(categoryRepository).findByUserAndId(any(), any(Long.class));
            doReturn(todo).when(todoRepository).save(any(Todo.class));

            //when
            final AddDailyTodoResponse addDailyTodoResponse = dailyPlannerServiceImpl.addDailyTodo(user, request);

            //then
            assertThat(addDailyTodoResponse.getTodoId()).isNotNull();

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
            verify(categoryRepository, times(1)).findByUserAndId(any(), any(Long.class));
            verify(todoRepository, times(1)).save(any(Todo.class));
        }

    }

    @Nested
    class 일일플래너수정 {
        @Test
        public void 오늘의다짐편집() {
            //given
            final UpdateTodayGoalRequest updateTodayGoalRequest = UpdateTodayGoalRequest.builder()
                    .date("2023-09-26")
                    .todayGoal("🎧 Dreams Come True - NCT127")
                    .build();
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .todayGoal(updateTodayGoalRequest.getTodayGoal())
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
            doReturn(changeDailyPlanner).when(dailyPlannerRepository).save(any(DailyPlanner.class));

            //when
            dailyPlannerServiceImpl.updateTodayGoal(user, updateTodayGoalRequest);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
            verify(dailyPlannerRepository, times(1)).save(any(DailyPlanner.class));
        }

        @Test
        public void 내일의다짐편집() {
            //given
            final UpdateTomorrowGoalRequest updateTomorrowGoalRequest = UpdateTomorrowGoalRequest.builder()
                    .date("2023-09-26")
                    .tomorrowGoal("이제는 더이상 물러나 곳이 없다.")
                    .build();
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal(updateTomorrowGoalRequest.getTomorrowGoal())
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
            doReturn(changeDailyPlanner).when(dailyPlannerRepository).save(any(DailyPlanner.class));

            //when
            dailyPlannerServiceImpl.updateTomorrowGoal(user, updateTomorrowGoalRequest);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
            verify(dailyPlannerRepository, times(1)).save(any(DailyPlanner.class));
        }

    }

    @Nested
    class 일일플래너할일삭제 {

        @Test
        public void 실패_유효하지않은일일플래너() {
            //given
            final RemoveDailyTodoRequest request = RemoveDailyTodoRequest.builder()
                    .date("2023-09-25")
                    .todoId(1L)
                    .build();
            doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyTodo(user, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }

        @Test
        public void 성공() {
            //given
            final RemoveDailyTodoRequest request = RemoveDailyTodoRequest.builder()
                    .date("2023-09-25")
                    .todoId(1L)
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));

            //when
            dailyPlannerServiceImpl.removeDailyTodo(user, request);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
            verify(todoRepository, times(1)).deleteByIdAndAndDailyPlanner(any(Long.class), any(DailyPlanner.class));
        }

    }

    @Nested
    class 좋아요 {
        @Nested
        class 좋아요등록 {
            final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                    .date("2023-09-28")
                    .anotherUserId(1L)
                    .build();
            final User user2 = User.builder()
                    .id(2L)
                    .email("test2@test.com")
                    .password("123456")
                    .socialLogin(SocialType.BASIC)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .withdrawal(false)
                    .build();

            @Test
            public void 실패_자신플래너에좋아요() {
                //given

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
            }

            @Test
            public void 실패_유효하지않은플래너() {
                //given
                doReturn(null).when(dailyPlannerRepository).findByUserIdAndDailyPlannerDay(any(Long.class), any(Date.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user2, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            public void 실패_이전에좋아요를이미누름() {
                //given
                final DailyPlannerLike dailyPlannerLike = DailyPlannerLike.builder()
                        .id(1L)
                        .dailyPlanner(dailyPlanner)
                        .user(user2)
                        .build();
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserIdAndDailyPlannerDay(any(Long.class), any(Date.class));
                doReturn(dailyPlannerLike).when(dailyPlannerLikeRepository).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user2, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.ALREADY_ADDED_LIKE);
            }

            @Test
            public void 성공() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserIdAndDailyPlannerDay(any(Long.class), any(Date.class));
                doReturn(null).when(dailyPlannerLikeRepository).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));

                //when
                dailyPlannerServiceImpl.addDailyLike(user2, addDailyLikeRequest);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserIdAndDailyPlannerDay(any(Long.class), any(Date.class));
                verify(dailyPlannerLikeRepository, times(1)).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));
                verify(dailyPlannerLikeRepository, times(1)).save(any(DailyPlannerLike.class));

            }
        }

        @Nested
        class 좋아요취소 {
            final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                    .date("2023-09-28")
                    .anotherUserId(1L)
                    .build();
            final User user2 = User.builder()
                    .id(2L)
                    .email("test2@test.com")
                    .password("123456")
                    .socialLogin(SocialType.BASIC)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .withdrawal(false)
                    .build();

            @Test
            public void 실패_자신플래너에좋아요취소() {
                //given

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyLike(user, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
            }

            @Test
            public void 실패_유효하지않은플래너() {
                //given
                doReturn(null).when(dailyPlannerRepository).findByUserIdAndDailyPlannerDay(any(Long.class), any(Date.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyLike(user2, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            public void 성공() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserIdAndDailyPlannerDay(any(Long.class), any(Date.class));

                //when
                dailyPlannerServiceImpl.removeDailyLike(user2, removeDailyLikeRequest);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserIdAndDailyPlannerDay(any(Long.class), any(Date.class));
                verify(dailyPlannerLikeRepository, times(1)).deleteByUserAndDailyPlanner(any(), any(DailyPlanner.class));
            }
        }
    }
}
