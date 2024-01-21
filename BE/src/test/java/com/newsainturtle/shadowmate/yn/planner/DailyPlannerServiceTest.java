package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.dto.response.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.response.ShareSocialResponse;
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
import com.newsainturtle.shadowmate.planner_setting.entity.Routine;
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineTodo;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineTodoRepository;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
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

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyPlannerServiceTest extends DateCommonService {

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
    private SocialRepository socialRepository;

    @Mock
    private RoutineTodoRepository routineTodoRepository;

    private final String email = "yntest@shadowmate.com";
    private final String password = "yntest1234";
    private final String nickname = "거북이";
    private final SocialType socialType = SocialType.BASIC;
    private final PlannerAccessScope plannerAccessScope = PlannerAccessScope.PUBLIC;
    private final String date = "2023-09-25";
    private final String todoContent = "수능완성 수학 과목별 10문제";

    private final User user = User.builder()
            .id(1L)
            .email(email)
            .password(password)
            .socialLogin(socialType)
            .nickname(nickname)
            .plannerAccessScope(plannerAccessScope)
            .withdrawal(false)
            .build();
    final DailyPlanner dailyPlanner = DailyPlanner.builder()
            .id(1L)
            .dailyPlannerDay(date)
            .user(user)
            .build();

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
        final Todo todo = Todo.builder()
                .id(1L)
                .category(category)
                .todoContent(todoContent)
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .todoIndex(100000D)
                .build();

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
                doReturn(null).when(categoryRepository).findByUserAndId(user, addDailyTodoRequest.getCategoryId());

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyTodo(user, addDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_CATEGORY);
            }

            @Test
            void 성공_카테고리Null() {
                //given
                final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                        .date(date)
                        .categoryId(0L)
                        .todoContent(todoContent)
                        .build();
                final Todo todo = Todo.builder()
                        .id(1L)
                        .category(null)
                        .todoContent(todoContent)
                        .todoStatus(TodoStatus.EMPTY)
                        .dailyPlanner(dailyPlanner)
                        .todoIndex(100000D)
                        .build();
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
                doReturn(null).when(todoRepository).findTopByDailyPlannerOrderByTodoIndexDesc(any());
                doReturn(todo).when(todoRepository).save(any(Todo.class));

                //when
                final AddDailyTodoResponse addDailyTodoResponse = dailyPlannerServiceImpl.addDailyTodo(user, addDailyTodoRequest);

                //then
                assertThat(addDailyTodoResponse.getTodoId()).isNotNull();

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
                verify(todoRepository, times(1)).save(any(Todo.class));
            }

            @Test
            void 성공() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
                doReturn(category).when(categoryRepository).findByUserAndId(any(), any(Long.class));
                doReturn(null).when(todoRepository).findTopByDailyPlannerOrderByTodoIndexDesc(any());
                doReturn(todo).when(todoRepository).save(any(Todo.class));

                //when
                final AddDailyTodoResponse addDailyTodoResponse = dailyPlannerServiceImpl.addDailyTodo(user, addDailyTodoRequest);

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

            final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                    .date(date)
                    .todoId(1L)
                    .todoContent(todoContent)
                    .categoryId(0L)
                    .todoStatus("완료")
                    .build();

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

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.updateDailyTodo(user, updateDailyTodoRequest));

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

                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.updateDailyTodo(user, updateDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

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

                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(null).when(categoryRepository).findByUserAndId(user, updateDailyTodoRequest.getCategoryId());

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.updateDailyTodo(user, updateDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_CATEGORY);
            }

            @Test
            void 실패_유효하지않은할일() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(null).when(todoRepository).findByIdAndDailyPlanner(updateDailyTodoRequest.getTodoId(), dailyPlanner);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.updateDailyTodo(user, updateDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }

            @Test
            void 성공() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(updateDailyTodoRequest.getTodoId(), dailyPlanner);

                //when
                dailyPlannerServiceImpl.updateDailyTodo(user, updateDailyTodoRequest);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
                verify(todoRepository, times(1)).findByIdAndDailyPlanner(any(Long.class), any());
            }

        }

        @Nested
        class 일일플래너할일_순서변경 {

            final ChangeDailyTodoSequenceRequest changeDailyTodoSequenceRequest = ChangeDailyTodoSequenceRequest.builder()
                    .date(date)
                    .todoId(3L)
                    .upperTodoId(1L)
                    .build();


            @Test
            void 실패_유효하지않은일일플래너() {
                //given

                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.changeDailyTodoSequence(user, changeDailyTodoSequenceRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            void 실패_유효하지않은할일() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(null).when(todoRepository).findByIdAndDailyPlanner(changeDailyTodoSequenceRequest.getTodoId(), dailyPlanner);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.changeDailyTodoSequence(user, changeDailyTodoSequenceRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }

            @Test
            void 실패_유효하지않은아래쪽할일() {
                //given
                final ChangeDailyTodoSequenceRequest changeDailyTodoSequenceRequest = ChangeDailyTodoSequenceRequest.builder()
                        .date(date)
                        .todoId(3L)
                        .upperTodoId(null)
                        .build();

                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(changeDailyTodoSequenceRequest.getTodoId(), dailyPlanner);
                doReturn(null).when(todoRepository).findTopByDailyPlannerAndTodoIndexGreaterThanOrderByTodoIndex(dailyPlanner, 0);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.changeDailyTodoSequence(user, changeDailyTodoSequenceRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }


            @Test
            void 실패_유효하지않은위쪽할일() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(changeDailyTodoSequenceRequest.getTodoId(), dailyPlanner);
                doReturn(null).when(todoRepository).findByIdAndDailyPlanner(changeDailyTodoSequenceRequest.getUpperTodoId(), dailyPlanner);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.changeDailyTodoSequence(user, changeDailyTodoSequenceRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }

            @Test
            void 성공() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(changeDailyTodoSequenceRequest.getTodoId(), dailyPlanner);
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(changeDailyTodoSequenceRequest.getUpperTodoId(), dailyPlanner);
                doReturn(null).when(todoRepository).findTopByDailyPlannerAndTodoIndexGreaterThanOrderByTodoIndex(dailyPlanner, todo.getTodoIndex());

                //when
                dailyPlannerServiceImpl.changeDailyTodoSequence(user, changeDailyTodoSequenceRequest);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
                verify(todoRepository, times(2)).findByIdAndDailyPlanner(any(Long.class), any());
                verify(todoRepository, times(1)).findTopByDailyPlannerAndTodoIndexGreaterThanOrderByTodoIndex(any(), any(Double.class));
            }

        }

        @Nested
        class 일일플래너할일삭제 {

            final RemoveDailyTodoRequest removeDailyTodoRequest = RemoveDailyTodoRequest.builder()
                    .date(date)
                    .todoId(1L)
                    .build();

            @Test
            void 실패_유효하지않은일일플래너() {
                //given
                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyTodo(user, removeDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            void 실패_유효하지않은할일() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(null).when(todoRepository).findByIdAndDailyPlanner(removeDailyTodoRequest.getTodoId(), dailyPlanner);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyTodo(user, removeDailyTodoRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }

            @Test
            void 성공_관련루틴없음() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(removeDailyTodoRequest.getTodoId(), dailyPlanner);
                doReturn(null).when(routineTodoRepository).findByTodo(todo);

                //when
                dailyPlannerServiceImpl.removeDailyTodo(user, removeDailyTodoRequest);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
                verify(todoRepository, times(1)).deleteById(any(Long.class));
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

                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(removeDailyTodoRequest.getTodoId(), dailyPlanner);
                doReturn(routineTodo).when(routineTodoRepository).findByTodo(todo);

                //when
                dailyPlannerServiceImpl.removeDailyTodo(user, removeDailyTodoRequest);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
                verify(todoRepository, times(1)).deleteById(any(Long.class));
                verify(routineTodoRepository, times(1)).findByTodo(any(Todo.class));
                verify(routineTodoRepository, times(1)).deleteById(any(Long.class));
            }

        }
    }

    @Nested
    class 일일플래너수정 {
        @Test
        void 오늘의다짐편집() {
            //given
            final UpdateTodayGoalRequest updateTodayGoalRequest = UpdateTodayGoalRequest.builder()
                    .date(date)
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

            //when
            dailyPlannerServiceImpl.updateTodayGoal(user, updateTodayGoalRequest);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
        }

        @Test
        void 내일의다짐편집() {
            //given
            final UpdateTomorrowGoalRequest updateTomorrowGoalRequest = UpdateTomorrowGoalRequest.builder()
                    .date(date)
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

            //when
            dailyPlannerServiceImpl.updateTomorrowGoal(user, updateTomorrowGoalRequest);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
        }

        @Test
        void 오늘의회고편집() {
            //given
            final UpdateRetrospectionRequest updateRetrospectionRequest = UpdateRetrospectionRequest.builder()
                    .date(date)
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

            //when
            dailyPlannerServiceImpl.updateRetrospection(user, updateRetrospectionRequest);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
        }

        @Test
        void 오늘의회고사진업로드_null() {
            //given
            final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                    .date(date)
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

            //when
            dailyPlannerServiceImpl.updateRetrospectionImage(user, updateRetrospectionImageRequest);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
        }

        @Test
        void 오늘의회고사진업로드() {
            //given
            final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                    .date(date)
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

            //when
            dailyPlannerServiceImpl.updateRetrospectionImage(user, updateRetrospectionImageRequest);

            //then

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
        }

    }

    @Nested
    class 좋아요 {
        final Long plannerWriterId = 1L;
        final User user2 = User.builder()
                .id(2L)
                .email("jntest@shadowmate.com")
                .password(password)
                .socialLogin(socialType)
                .nickname("토끼")
                .plannerAccessScope(plannerAccessScope)
                .withdrawal(false)
                .build();

        @Nested
        class 좋아요등록 {
            final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                    .date(date)
                    .build();

            @Test
            void 실패_자신플래너에좋아요() {
                //given

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
            }

            @Test
            void 실패_유효하지않은사용자() {
                //given
                doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user2, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
            }

            @Test
            void 실패_유효하지않은플래너() {
                //given
                doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user2, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            void 실패_이전에좋아요를이미누름() {
                //given
                final DailyPlannerLike dailyPlannerLike = DailyPlannerLike.builder()
                        .id(1L)
                        .dailyPlanner(dailyPlanner)
                        .user(user2)
                        .build();
                doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(dailyPlannerLike).when(dailyPlannerLikeRepository).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyLike(user2, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.ALREADY_ADDED_LIKE);
            }

            @Test
            void 성공() {
                //given
                doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(null).when(dailyPlannerLikeRepository).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));

                //when
                dailyPlannerServiceImpl.addDailyLike(user2, plannerWriterId, addDailyLikeRequest);

                //then

                //verify
                verify(userRepository, times(1)).findByIdAndWithdrawalIsFalse(any(Long.class));
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(String.class));
                verify(dailyPlannerLikeRepository, times(1)).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));
                verify(dailyPlannerLikeRepository, times(1)).save(any(DailyPlannerLike.class));

            }
        }

        @Nested
        class 좋아요취소 {
            final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                    .date(date)
                    .build();

            @Test
            void 실패_자신플래너에좋아요취소() {
                //given

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyLike(user, plannerWriterId, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
            }

            @Test
            void 실패_유효하지않은사용자() {
                //given
                doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyLike(user2, plannerWriterId, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
            }

            @Test
            void 실패_유효하지않은플래너() {
                //given
                doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeDailyLike(user2, plannerWriterId, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            void 성공() {
                //given
                doReturn(user2).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));

                //when
                dailyPlannerServiceImpl.removeDailyLike(user2, plannerWriterId, removeDailyLikeRequest);

                //then

                //verify
                verify(userRepository, times(1)).findByIdAndWithdrawalIsFalse(any(Long.class));
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(String.class));
                verify(dailyPlannerLikeRepository, times(1)).deleteByUserAndDailyPlanner(any(), any(DailyPlanner.class));
            }
        }
    }

    @Nested
    class 타임테이블 {
        final String startTime = "2023-09-25 23:50";
        final String endTime = "2023-09-26 01:30";
        final Todo todo = Todo.builder()
                .id(1L)
                .category(null)
                .todoContent(todoContent)
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .todoIndex(100000D)
                .timeTables(new ArrayList<>())
                .build();
        final TimeTable timeTable = TimeTable.builder()
                .id(1L)
                .todo(todo)
                .startTime(stringToLocalDateTime(startTime))
                .endTime(stringToLocalDateTime(endTime))
                .build();

        @Nested
        class 타임테이블등록 {

            final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                    .date(date)
                    .startTime(startTime)
                    .endTime(endTime)
                    .todoId(todo.getId())
                    .build();

            @Test
            void 실패_잘못된시간값_끝시간이_시간시간보다_빠름() {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime("2023-09-25 14:10")
                        .todoId(todo.getId())
                        .build();

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, addTimeTableRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TIME);
            }

            @Test
            void 실패_잘못된시간값_그날의시간에포함되지않음_과거() {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime("2023-09-25 03:50")
                        .endTime(endTime)
                        .todoId(todo.getId())
                        .build();

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, addTimeTableRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TIME);
            }

            @Test
            void 실패_잘못된시간값_그날의시간에포함되지않음_미래() {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime("2023-09-26 04:10")
                        .todoId(todo.getId())
                        .build();

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, addTimeTableRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TIME);
            }

            @Test
            void 실패_유효하지않은플래너() {
                //given
                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, addTimeTableRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            void 실패_유효하지않은할일() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(null).when(todoRepository).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));
                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, addTimeTableRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
            }

            @Test
            void 실패_타임테이블등록_불가상태() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));
                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addTimeTable(user, addTimeTableRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.FAILED_ADDED_TIMETABLE);
            }

            @Test
            void 성공() {
                final Todo todo = Todo.builder()
                        .id(1L)
                        .category(null)
                        .todoContent(todoContent)
                        .todoStatus(TodoStatus.COMPLETE)
                        .dailyPlanner(dailyPlanner)
                        .todoIndex(100000D)
                        .timeTables(new ArrayList<>())
                        .build();
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(todo).when(todoRepository).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));
                doReturn(timeTable).when(timeTableRepository).save(any(TimeTable.class));

                //when
                dailyPlannerServiceImpl.addTimeTable(user, addTimeTableRequest);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(String.class));
                verify(todoRepository, times(1)).findByIdAndDailyPlanner(any(Long.class), any(DailyPlanner.class));
            }

        }

        @Nested
        class 타임테이블삭제 {

            final RemoveTimeTableRequest removeTimeTableRequest = RemoveTimeTableRequest.builder()
                    .date(date)
                    .todoId(todo.getId())
                    .timeTableId(timeTable.getId())
                    .build();

            @Test
            void 실패_유효하지않은플래너() {
                //given
                doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.removeTimeTable(user, removeTimeTableRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            void 성공() {
                //given
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(timeTable).when(timeTableRepository).findByIdAndTodoId(any(), any());

                //when
                dailyPlannerServiceImpl.removeTimeTable(user, removeTimeTableRequest);

                //then

                //verify
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(String.class));
                verify(timeTableRepository, times(1)).findByIdAndTodoId(any(Long.class), any(Long.class));
                verify(timeTableRepository, times(1)).deleteById(any(Long.class));
            }

        }
    }

    @Nested
    class 소셜공유 {
        final String socialImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";
        final ShareSocialRequest shareSocialRequest = ShareSocialRequest.builder()
                .date(date)
                .socialImage(socialImage)
                .build();

        @Test
        void 실패_공개상태가아닌경우() {
            //given
            final User user = User.builder()
                    .id(1L)
                    .email(email)
                    .password(password)
                    .socialLogin(socialType)
                    .nickname(nickname)
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.shareSocial(user, shareSocialRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.FAILED_SHARE_SOCIAL);
        }

        @Test
        void 실패_유효하지않은플래너() {
            //given
            doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.shareSocial(user, shareSocialRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }

        @Test
        void 실패_이미공유한_소셜재공유() {
            //given
            final Social social = Social.builder()
                    .id(1L)
                    .socialImage(socialImage)
                    .dailyPlanner(dailyPlanner)
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .ownerId(user.getId())
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
            doReturn(social).when(socialRepository).findByDailyPlanner(any(DailyPlanner.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.shareSocial(user, shareSocialRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.ALREADY_SHARED_SOCIAL);

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(String.class));
            verify(socialRepository, times(1)).findByDailyPlanner(any(DailyPlanner.class));
        }

        @Test
        void 성공_소셜공유() {
            //given
            final Social social = Social.builder()
                    .id(1L)
                    .socialImage(socialImage)
                    .dailyPlanner(dailyPlanner)
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .ownerId(user.getId())
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
            doReturn(null).when(socialRepository).findByDailyPlanner(any(DailyPlanner.class));
            doReturn(social).when(socialRepository).save(any(Social.class));

            //when
            ShareSocialResponse shareSocialResponse = dailyPlannerServiceImpl.shareSocial(user, shareSocialRequest);

            //then
            assertThat(shareSocialResponse).isNotNull();
            assertThat(shareSocialResponse.getSocialId()).isEqualTo(1L);

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(String.class));
            verify(socialRepository, times(1)).findByDailyPlanner(any(DailyPlanner.class));
            verify(socialRepository, times(1)).save(any(Social.class));

        }
    }
}
