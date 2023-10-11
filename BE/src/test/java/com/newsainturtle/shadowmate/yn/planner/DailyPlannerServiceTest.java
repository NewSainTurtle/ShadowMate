package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.dto.response.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.response.SearchDailyPlannerResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.DailyPlannerLike;
import com.newsainturtle.shadowmate.planner.entity.TimeTable;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerLikeRepository;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TimeTableRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    @Mock
    private TimeTableRepository timeTableRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DdayRepository ddayRepository;

    @Mock
    private FollowRepository followRepository;

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

    private LocalDateTime stringToLocalDateTime(String timeStr) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(timeStr, formatter);
    }

    @Nested
    class 일일플래너할일 {

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
        class 일일플래너할일수정 {

            @Test
            public void 실패_유효하지않은할일상태값() {
                //given
                final UpdateDailyTodoRequest request = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("??")
                        .build();

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.updateDailyTodo(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO_STATUS);
            }


            @Test
            public void 실패_유효하지않은일일플래너() {
                //given
                final UpdateDailyTodoRequest request = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();

                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.updateDailyTodo(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            public void 실패_유효하지않은카테고리() {
                //given
                final UpdateDailyTodoRequest request = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();

                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(null).when(categoryRepository).findByUserAndId(user, request.getCategoryId());

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.updateDailyTodo(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_CATEGORY);
            }

            @Test
            public void 실패_유효하지않은할일() {
                //given
                final UpdateDailyTodoRequest request = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(0L)
                        .todoStatus("완료")
                        .build();

                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(null).when(todoRepository).findByIdAndDailyPlanner(request.getTodoId(), dailyPlanner);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.updateDailyTodo(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }

            @Test
            public void 성공() {
                //given
                final UpdateDailyTodoRequest request = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(0L)
                        .todoStatus("완료")
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
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(request.getTodoId(), dailyPlanner);

                //when
                dailyPlannerServiceImpl.updateDailyTodo(user, request);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
                verify(todoRepository, times(1)).findByIdAndDailyPlanner(any(Long.class), any());
                verify(todoRepository, times(1)).save(any(Todo.class));
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
            public void 실패_유효하지않은할일() {
                //given
                final RemoveDailyTodoRequest request = RemoveDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .build();

                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(null).when(todoRepository).findByIdAndDailyPlanner(request.getTodoId(), dailyPlanner);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyTodo(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }

            @Test
            public void 성공() {
                //given
                final RemoveDailyTodoRequest request = RemoveDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
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

                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(request.getTodoId(), dailyPlanner);

                //when
                dailyPlannerServiceImpl.removeDailyTodo(user, request);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
                verify(todoRepository, times(1)).deleteByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));
            }

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

        @Test
        public void 오늘의회고편집() {
            //given
            final UpdateRetrospectionRequest updateRetrospectionRequest = UpdateRetrospectionRequest.builder()
                    .date("2023-09-26")
                    .retrospection("오늘 계획했던 일을 모두 끝냈다!!! 신남~~")
                    .build();
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .retrospection(updateRetrospectionRequest.getRetrospection())
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
            doReturn(changeDailyPlanner).when(dailyPlannerRepository).save(any(DailyPlanner.class));

            //when
            dailyPlannerServiceImpl.updateRetrospection(user, updateRetrospectionRequest);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
            verify(dailyPlannerRepository, times(1)).save(any(DailyPlanner.class));
        }

        @Test
        public void 오늘의회고사진업로드_null() {
            //given
            final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                    .date("2023-09-26")
                    .retrospectionImage(null)
                    .build();
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(updateRetrospectionImageRequest.getRetrospectionImage())
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
            doReturn(changeDailyPlanner).when(dailyPlannerRepository).save(any(DailyPlanner.class));

            //when
            dailyPlannerServiceImpl.updateRetrospectionImage(user, updateRetrospectionImageRequest);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
            verify(dailyPlannerRepository, times(1)).save(any(DailyPlanner.class));
        }

        @Test
        public void 오늘의회고사진업로드() {
            //given
            final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                    .date("2023-09-26")
                    .retrospectionImage("https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg")
                    .build();
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(updateRetrospectionImageRequest.getRetrospectionImage())
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
            doReturn(changeDailyPlanner).when(dailyPlannerRepository).save(any(DailyPlanner.class));

            //when
            dailyPlannerServiceImpl.updateRetrospectionImage(user, updateRetrospectionImageRequest);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
            verify(dailyPlannerRepository, times(1)).save(any(DailyPlanner.class));
        }

    }

    @Nested
    class 좋아요 {
        final Long plannerWriterId = 1L;
        final User user2 = User.builder()
                .id(2L)
                .email("test2@test.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("토끼")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build();

        @Nested
        class 좋아요등록 {
            final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                    .date("2023-09-28")
                    .build();

            @Test
            public void 실패_자신플래너에좋아요() {
                //given

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
            }

            @Test
            public void 실패_유효하지않은사용자() {
                //given
                doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user2, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
            }

            @Test
            public void 실패_유효하지않은플래너() {
                //given
                doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user2, plannerWriterId, addDailyLikeRequest));

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
                doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(dailyPlannerLike).when(dailyPlannerLikeRepository).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user2, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.ALREADY_ADDED_LIKE);
            }

            @Test
            public void 성공() {
                //given
                doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(null).when(dailyPlannerLikeRepository).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));

                //when
                dailyPlannerServiceImpl.addDailyLike(user2, plannerWriterId, addDailyLikeRequest);

                //then

                //verify
                verify(userRepository, times(1)).findByIdAndWithdrawalIsFalse(any(Long.class));
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(Date.class));
                verify(dailyPlannerLikeRepository, times(1)).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));
                verify(dailyPlannerLikeRepository, times(1)).save(any(DailyPlannerLike.class));

            }
        }

        @Nested
        class 좋아요취소 {
            final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                    .date("2023-09-28")
                    .build();

            @Test
            public void 실패_자신플래너에좋아요취소() {
                //given

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyLike(user, plannerWriterId, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
            }

            @Test
            public void 실패_유효하지않은사용자() {
                //given
                doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyLike(user2, plannerWriterId, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
            }

            @Test
            public void 실패_유효하지않은플래너() {
                //given
                doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyLike(user2, plannerWriterId, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            public void 성공() {
                //given
                doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));

                //when
                dailyPlannerServiceImpl.removeDailyLike(user2, plannerWriterId, removeDailyLikeRequest);

                //then

                //verify
                verify(userRepository, times(1)).findByIdAndWithdrawalIsFalse(any(Long.class));
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(Date.class));
                verify(dailyPlannerLikeRepository, times(1)).deleteByUserAndDailyPlanner(any(), any(DailyPlanner.class));
            }
        }
    }

    @Nested
    class 타임테이블 {
        final String date = "2023-09-25";
        final String startTime = "2023-09-25 23:50";
        final String endTime = "2023-09-26 01:30";
        final Todo todo = Todo.builder()
                .id(1L)
                .category(null)
                .todoContent("수능완성 수학 과목별 10문제")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build();
        final TimeTable timeTable = TimeTable.builder()
                .id(1L)
                .todo(todo)
                .startTime(stringToLocalDateTime(startTime))
                .endTime(stringToLocalDateTime(endTime))
                .build();

        @Nested
        class 타임테이블등록 {

            @Test
            public void 실패_잘못된시간값_끝시간이_시간시간보다_빠름() {
                //given
                final AddTimeTableRequest request = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime("2023-09-25 14:10")
                        .todoId(todo.getId())
                        .build();

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TIME);
            }

            @Test
            public void 실패_잘못된시간값_그날의시간에포함되지않음_과거() {
                //given
                final AddTimeTableRequest request = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime("2023-09-25 03:50")
                        .endTime(endTime)
                        .todoId(todo.getId())
                        .build();

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TIME);
            }

            @Test
            public void 실패_잘못된시간값_그날의시간에포함되지않음_미래() {
                //given
                final AddTimeTableRequest request = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime("2023-09-26 04:10")
                        .todoId(todo.getId())
                        .build();

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TIME);
            }

            @Test
            public void 실패_유효하지않은플래너() {
                //given
                final AddTimeTableRequest request = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(todo.getId())
                        .build();
                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            public void 실패_유효하지않은할일() {
                //given
                final AddTimeTableRequest request = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(todo.getId())
                        .build();
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(null).when(todoRepository).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));
                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }

            @Test
            public void 실패_이미타임테이블시간이존재() {
                //given
                final AddTimeTableRequest request = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(todo.getId())
                        .build();
                final Todo todo = Todo.builder()
                        .id(1L)
                        .category(null)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .todoStatus(TodoStatus.EMPTY)
                        .dailyPlanner(dailyPlanner)
                        .timeTable(timeTable)
                        .build();
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.ALREADY_ADDED_TIME_TABLE);
            }

            @Test
            public void 성공() {
                final AddTimeTableRequest request = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(todo.getId())
                        .build();
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));

                //when
                dailyPlannerServiceImpl.addTimeTable(user, request);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(Date.class));
                verify(todoRepository, times(1)).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));
            }

        }

        @Nested
        class 타임테이블삭제 {

            @Test
            public void 실패_유효하지않은플래너() {
                //given
                final RemoveTimeTableRequest request = RemoveTimeTableRequest.builder()
                        .date(date)
                        .todoId(todo.getId())
                        .build();
                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeTimeTable(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            public void 실패_유효하지않은할일() {
                //given
                final RemoveTimeTableRequest request = RemoveTimeTableRequest.builder()
                        .date(date)
                        .todoId(todo.getId())
                        .build();
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(null).when(todoRepository).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeTimeTable(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }

            @Test
            public void 실패_타임테이블값없음() {
                //given
                final RemoveTimeTableRequest request = RemoveTimeTableRequest.builder()
                        .date(date)
                        .todoId(todo.getId())
                        .build();
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeTimeTable(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TIME_TABLE);
            }

            @Test
            public void 성공() {
                //given
                final Todo todo = Todo.builder()
                        .id(1L)
                        .category(null)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .todoStatus(TodoStatus.EMPTY)
                        .dailyPlanner(dailyPlanner)
                        .timeTable(timeTable)
                        .build();
                final RemoveTimeTableRequest request = RemoveTimeTableRequest.builder()
                        .date(date)
                        .todoId(todo.getId())
                        .build();

                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));

                //when
                dailyPlannerServiceImpl.removeTimeTable(user, request);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(Date.class));
                verify(todoRepository, times(1)).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));
                verify(timeTableRepository, times(1)).deleteById(any(Long.class));
            }

        }
    }

    @Nested
    class 일일플래너조회 {
        final long plannerWriterId = 1L;
        final String today = "2023-10-10";

        @Test
        public void 실패_유효하지않은날짜형식() {
            //given
            final String invalidToday = "2023.10.10";

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.searchDailyPlanner(user, plannerWriterId, invalidToday));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        public void 실패_유효하지않은플래너작성자() {
            //given
            doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.searchDailyPlanner(user, plannerWriterId, today));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
        }

        @Test
        public void 성공_플래너없을때() {
            //given
            doReturn(user).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(any(), any(Date.class));

            //when
            final SearchDailyPlannerResponse searchDailyPlannerResponse = dailyPlannerServiceImpl.searchDailyPlanner(user, plannerWriterId, today);

            //then
            assertThat(searchDailyPlannerResponse).isNotNull();
            assertThat(searchDailyPlannerResponse.getDate()).isEqualTo(today);
            assertThat(searchDailyPlannerResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.PUBLIC.getScope());
            assertThat(searchDailyPlannerResponse.getDday()).isNull();
            assertThat(searchDailyPlannerResponse.getTodayGoal()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospection()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospectionImage()).isNull();
            assertThat(searchDailyPlannerResponse.getTomorrowGoal()).isNull();
            assertThat(searchDailyPlannerResponse.isLike()).isFalse();
            assertThat(searchDailyPlannerResponse.getLikeCount()).isEqualTo(0);
            assertThat(searchDailyPlannerResponse.getStudyTimeHour()).isEqualTo(0);
            assertThat(searchDailyPlannerResponse.getStudyTimeMinute()).isEqualTo(0);
            assertThat(searchDailyPlannerResponse.getDailyTodos()).isNull();
        }

        @Test
        public void 성공_플래너있을때_비공개() {
            //given
            final User user2 = User.builder()
                    .id(2L)
                    .email("test@test.com")
                    .password("123456")
                    .socialLogin(SocialType.BASIC)
                    .nickname("거북이")
                    .plannerAccessScope(PlannerAccessScope.PRIVATE)
                    .withdrawal(false)
                    .build();
            final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                    .id(2L)
                    .dailyPlannerDay(Date.valueOf("2023-09-25"))
                    .user(user2)
                    .build();

            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());
            doReturn(dailyPlanner2).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(any(), any(Date.class));

            //when
            final SearchDailyPlannerResponse searchDailyPlannerResponse = dailyPlannerServiceImpl.searchDailyPlanner(user, 2L, today);

            //then
            assertThat(searchDailyPlannerResponse).isNotNull();
            assertThat(searchDailyPlannerResponse.getDate()).isEqualTo(today);
            assertThat(searchDailyPlannerResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.PRIVATE.getScope());
            assertThat(searchDailyPlannerResponse.getDday()).isNull();
            assertThat(searchDailyPlannerResponse.getTodayGoal()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospection()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospectionImage()).isNull();
            assertThat(searchDailyPlannerResponse.getTomorrowGoal()).isNull();
            assertThat(searchDailyPlannerResponse.isLike()).isFalse();
            assertThat(searchDailyPlannerResponse.getLikeCount()).isEqualTo(0);
            assertThat(searchDailyPlannerResponse.getStudyTimeHour()).isEqualTo(0);
            assertThat(searchDailyPlannerResponse.getStudyTimeMinute()).isEqualTo(0);
            assertThat(searchDailyPlannerResponse.getDailyTodos()).isNull();
        }

        @Test
        public void 성공_플래너있을때_친구공개_친구아님() {
            //given
            final User user2 = User.builder()
                    .id(2L)
                    .email("test@test.com")
                    .password("123456")
                    .socialLogin(SocialType.BASIC)
                    .nickname("거북이")
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();
            final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                    .id(2L)
                    .dailyPlannerDay(Date.valueOf("2023-09-25"))
                    .user(user2)
                    .build();

            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());
            doReturn(dailyPlanner2).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(null).when(followRepository).findByFollowerIdAndFollowingId(any(), any());
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(any(), any(Date.class));

            //when
            final SearchDailyPlannerResponse searchDailyPlannerResponse = dailyPlannerServiceImpl.searchDailyPlanner(user, 2L, today);

            //then
            assertThat(searchDailyPlannerResponse).isNotNull();
            assertThat(searchDailyPlannerResponse.getDate()).isEqualTo(today);
            assertThat(searchDailyPlannerResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.FOLLOW.getScope());
            assertThat(searchDailyPlannerResponse.getDday()).isNull();
            assertThat(searchDailyPlannerResponse.getTodayGoal()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospection()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospectionImage()).isNull();
            assertThat(searchDailyPlannerResponse.getTomorrowGoal()).isNull();
            assertThat(searchDailyPlannerResponse.isLike()).isFalse();
            assertThat(searchDailyPlannerResponse.getLikeCount()).isEqualTo(0);
            assertThat(searchDailyPlannerResponse.getStudyTimeHour()).isEqualTo(0);
            assertThat(searchDailyPlannerResponse.getStudyTimeMinute()).isEqualTo(0);
            assertThat(searchDailyPlannerResponse.getDailyTodos()).isNull();
        }

        @Test
        public void 성공_플래너있을때_친구공개_친구() {
            //given
            final User user2 = User.builder()
                    .id(2L)
                    .email("test@test.com")
                    .password("123456")
                    .socialLogin(SocialType.BASIC)
                    .nickname("거북이")
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();
            final Follow follow = Follow.builder()
                    .id(1L)
                    .followerId(user)
                    .followingId(user2)
                    .build();
            final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                    .id(2L)
                    .dailyPlannerDay(Date.valueOf(today))
                    .user(user2)
                    .build();
            final Date birthday = Date.valueOf(LocalDate.now());
            final Dday dday = Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(birthday)
                    .user(user)
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
            final List<Todo> todoList = new ArrayList<>();
            todoList.add(Todo.builder()
                    .id(1L)
                    .category(category)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .timeTable(TimeTable.builder()
                            .startTime(stringToLocalDateTime("2023-10-10 22:50"))
                            .endTime(stringToLocalDateTime("2023-10-11 01:30"))
                            .build())
                    .build());

            doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any());
            doReturn(dailyPlanner2).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(follow).when(followRepository).findByFollowerIdAndFollowingId(any(), any());
            doReturn(dday).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(dailyPlannerLikeRepository).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));
            doReturn(127L).when(dailyPlannerLikeRepository).countByDailyPlanner(any(DailyPlanner.class));
            doReturn(todoList).when(todoRepository).findAllByDailyPlanner(any(DailyPlanner.class));

            //when
            final SearchDailyPlannerResponse searchDailyPlannerResponse = dailyPlannerServiceImpl.searchDailyPlanner(user, plannerWriterId, today);

            //then
            assertThat(searchDailyPlannerResponse).isNotNull();
            assertThat(searchDailyPlannerResponse.getDate()).isEqualTo(today);
            assertThat(searchDailyPlannerResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.FOLLOW.getScope());
            assertThat(searchDailyPlannerResponse.getDday()).isEqualTo(birthday.toString());
            assertThat(searchDailyPlannerResponse.getTodayGoal()).isEqualTo(dailyPlanner.getTodayGoal());
            assertThat(searchDailyPlannerResponse.getRetrospection()).isEqualTo(dailyPlanner.getRetrospection());
            assertThat(searchDailyPlannerResponse.getRetrospectionImage()).isEqualTo(dailyPlanner.getRetrospectionImage());
            assertThat(searchDailyPlannerResponse.getTomorrowGoal()).isEqualTo(dailyPlanner.getTomorrowGoal());
            assertThat(searchDailyPlannerResponse.isLike()).isFalse();
            assertThat(searchDailyPlannerResponse.getLikeCount()).isEqualTo(127L);
            assertThat(searchDailyPlannerResponse.getStudyTimeHour()).isEqualTo(2);
            assertThat(searchDailyPlannerResponse.getStudyTimeMinute()).isEqualTo(40);
            assertThat(searchDailyPlannerResponse.getDailyTodos()).isNotNull();
            assertThat(searchDailyPlannerResponse.getDailyTodos().size()).isEqualTo(1);
        }

        @Test
        public void 성공_플래너있을때_전체공개() {
            //given
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(1L)
                    .dailyPlannerDay(Date.valueOf(today))
                    .user(user)
                    .build();
            final Date birthday = Date.valueOf(LocalDate.now());
            final Dday dday = Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(birthday)
                    .user(user)
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
            final List<Todo> todoList = new ArrayList<>();
            todoList.add(Todo.builder()
                    .id(1L)
                    .category(category)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .timeTable(TimeTable.builder()
                            .startTime(stringToLocalDateTime("2023-10-10 22:50"))
                            .endTime(stringToLocalDateTime("2023-10-11 01:30"))
                            .build())
                    .build());

            doReturn(user).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(dday).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(dailyPlannerLikeRepository).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));
            doReturn(127L).when(dailyPlannerLikeRepository).countByDailyPlanner(any(DailyPlanner.class));
            doReturn(todoList).when(todoRepository).findAllByDailyPlanner(any(DailyPlanner.class));


            //when
            final SearchDailyPlannerResponse searchDailyPlannerResponse = dailyPlannerServiceImpl.searchDailyPlanner(user, plannerWriterId, today);

            //then
            assertThat(searchDailyPlannerResponse).isNotNull();
            assertThat(searchDailyPlannerResponse.getDate()).isEqualTo(today);
            assertThat(searchDailyPlannerResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.PUBLIC.getScope());
            assertThat(searchDailyPlannerResponse.getDday()).isEqualTo(birthday.toString());
            assertThat(searchDailyPlannerResponse.getTodayGoal()).isEqualTo(dailyPlanner.getTodayGoal());
            assertThat(searchDailyPlannerResponse.getRetrospection()).isEqualTo(dailyPlanner.getRetrospection());
            assertThat(searchDailyPlannerResponse.getRetrospectionImage()).isEqualTo(dailyPlanner.getRetrospectionImage());
            assertThat(searchDailyPlannerResponse.getTomorrowGoal()).isEqualTo(dailyPlanner.getTomorrowGoal());
            assertThat(searchDailyPlannerResponse.isLike()).isFalse();
            assertThat(searchDailyPlannerResponse.getLikeCount()).isEqualTo(127L);
            assertThat(searchDailyPlannerResponse.getStudyTimeHour()).isEqualTo(2);
            assertThat(searchDailyPlannerResponse.getStudyTimeMinute()).isEqualTo(40);
            assertThat(searchDailyPlannerResponse.getDailyTodos()).isNotNull();
            assertThat(searchDailyPlannerResponse.getDailyTodos().size()).isEqualTo(1);
        }
    }
}
