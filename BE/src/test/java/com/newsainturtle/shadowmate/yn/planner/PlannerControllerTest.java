package com.newsainturtle.shadowmate.yn.planner;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.planner.controller.PlannerController;
import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.MonthlyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.SearchPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.WeeklyPlannerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PlannerControllerTest {

    @InjectMocks
    private PlannerController plannerController;

    @Mock
    private DailyPlannerServiceImpl dailyPlannerServiceImpl;

    @Mock
    private WeeklyPlannerServiceImpl weeklyPlannerServiceImpl;

    @Mock
    private SearchPlannerServiceImpl searchPlannerServiceImpl;

    @Mock
    private MonthlyPlannerServiceImpl monthlyPlannerServiceImpl;

    @Mock
    private AuthService authServiceImpl;

    private MockMvc mockMvc;
    private Gson gson;
    final Long userId = 1L;
    private final String date = "2023-09-25";

    @BeforeEach
    void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(plannerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 일일플래너할일 {
        final String url = "/api/planners/{userId}/daily/todos";
        final String todoContent = "수능완성 수학 과목별 10문제";

        @Nested
        class 할일등록 {
            final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                    .todoContent(todoContent)
                    .categoryId(1L)
                    .date(date)
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_유효하지않은카테고리() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_CATEGORY)).when(dailyPlannerServiceImpl).addDailyTodo(any(), any(AddDailyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 할일수정 {
            final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                    .date(date)
                    .todoId(1L)
                    .todoContent(todoContent)
                    .categoryId(1L)
                    .todoStatus("완료")
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_유효하지않은할일상태값() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO_STATUS)).when(dailyPlannerServiceImpl).updateDailyTodo(any(), any(UpdateDailyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은일일플래너() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerServiceImpl).updateDailyTodo(any(), any(UpdateDailyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은카테고리() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_CATEGORY)).when(dailyPlannerServiceImpl).updateDailyTodo(any(), any(UpdateDailyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은할일() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO)).when(dailyPlannerServiceImpl).updateDailyTodo(any(), any(UpdateDailyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );


                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 할일삭제 {
            final RemoveDailyTodoRequest removeDailyTodoRequest = RemoveDailyTodoRequest.builder()
                    .todoId(1L)
                    .date(date)
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_유효하지않은플래너() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerServiceImpl).removeDailyTodo(any(), any(RemoveDailyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은할일() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO)).when(dailyPlannerServiceImpl).removeDailyTodo(any(), any(RemoveDailyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class 일일플래너할일_실패케이스모음_유효하지않은요청값 {

        final String url = "/api/planners/{userId}/daily/todos";
        final String todoContent = "수능완성 수학 과목별 10문제";

        @ParameterizedTest
        @MethodSource("invalidAddDailyTodoRequest")
        void 할일등록_실패(final AddDailyTodoRequest addDailyTodoRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addDailyTodoRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidAddDailyTodoRequest() {
            return Stream.of(
                    // 올바르지 않은 날짜 형식
                    Arguments.of(AddDailyTodoRequest.builder()
                            .todoContent(todoContent)
                            .categoryId(1L)
                            .date("2023.09.25")
                            .build()),
                    // 할일 내용 길이초과
                    Arguments.of(AddDailyTodoRequest.builder()
                            .todoContent("012345678901234567890123456789012345678901234567891")
                            .categoryId(1L)
                            .date(date)
                            .build()),
                    // 날짜 Null
                    Arguments.of(AddDailyTodoRequest.builder()
                            .todoContent(todoContent)
                            .categoryId(1L)
                            .date(null)
                            .build()),
                    // 할일 내용 Null
                    Arguments.of(AddDailyTodoRequest.builder()
                            .todoContent(null)
                            .categoryId(1L)
                            .date(date)
                            .build()),
                    // 카테고리 Null
                    Arguments.of(AddDailyTodoRequest.builder()
                            .todoContent(todoContent)
                            .categoryId(null)
                            .date(date)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidUpdateDailyTodoRequest")
        void 할일수정_실패(final UpdateDailyTodoRequest updateDailyTodoRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateDailyTodoRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidUpdateDailyTodoRequest() {
            final String todoStatus = "완료";
            return Stream.of(
                    // 올바르지 않은 날짜 형식
                    Arguments.of(UpdateDailyTodoRequest.builder()
                            .date("2023.09.25")
                            .todoId(1L)
                            .todoContent(todoContent)
                            .categoryId(1L)
                            .todoStatus(todoStatus)
                            .build()),
                    // 할일 내용 길이초과
                    Arguments.of(UpdateDailyTodoRequest.builder()
                            .date(date)
                            .todoId(1L)
                            .todoContent("012345678901234567890123456789012345678901234567891")
                            .categoryId(1L)
                            .todoStatus(todoStatus)
                            .build()),
                    // 날짜 Null
                    Arguments.of(UpdateDailyTodoRequest.builder()
                            .date(null)
                            .todoId(1L)
                            .todoContent(todoContent)
                            .categoryId(1L)
                            .todoStatus(todoStatus)
                            .build()),
                    // 할일 ID Null
                    Arguments.of(UpdateDailyTodoRequest.builder()
                            .date(date)
                            .todoId(null)
                            .todoContent(todoContent)
                            .categoryId(1L)
                            .todoStatus(todoStatus)
                            .build()),
                    // 할일 내용 Null
                    Arguments.of(UpdateDailyTodoRequest.builder()
                            .date(date)
                            .todoId(1L)
                            .todoContent(null)
                            .categoryId(1L)
                            .todoStatus(todoStatus)
                            .build()),
                    // 카테고리 ID Null
                    Arguments.of(UpdateDailyTodoRequest.builder()
                            .date(date)
                            .todoId(1L)
                            .todoContent(todoContent)
                            .categoryId(null)
                            .todoStatus(todoStatus)
                            .build()),
                    // 할일 상태 Null
                    Arguments.of(UpdateDailyTodoRequest.builder()
                            .date(date)
                            .todoId(1L)
                            .todoContent(todoContent)
                            .categoryId(1L)
                            .todoStatus(null)
                            .build()),
                    // 할일 상태 길이 초과
                    Arguments.of(UpdateDailyTodoRequest.builder()
                            .date(date)
                            .todoId(1L)
                            .todoContent(todoContent)
                            .categoryId(1L)
                            .todoStatus("????")
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidRemoveDailyTodoRequest")
        void 할일삭제_실패(final RemoveDailyTodoRequest removeDailyTodoRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, userId)
                            .content(gson.toJson(removeDailyTodoRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidRemoveDailyTodoRequest() {
            return Stream.of(
                    // 올바르지 않은 날짜 형식
                    Arguments.of(RemoveDailyTodoRequest.builder()
                            .todoId(1L)
                            .date("2023.09.25")
                            .build()),
                    // 날짜 Null
                    Arguments.of(RemoveDailyTodoRequest.builder()
                            .todoId(1L)
                            .date(null)
                            .build()),
                    // 할일 ID Null
                    Arguments.of(RemoveDailyTodoRequest.builder()
                            .todoId(null)
                            .date(date)
                            .build())
            );
        }
    }

    @Nested
    class 일일플래너할일_순서변경 {
        final String url = "/api/planners/{userId}/daily/todo-sequence";
        final ChangeDailyTodoSequenceRequest changeDailyTodoSequenceRequest = ChangeDailyTodoSequenceRequest.builder()
                .date(date)
                .todoId(1L)
                .upperTodoId(2L)
                .build();

        @Test
        void 실패_없는사용자() throws Exception {
            //given
            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(changeDailyTodoSequenceRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        void 실패_유효하지않은할일() throws Exception {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO)).when(dailyPlannerServiceImpl).changeDailyTodoSequence(any(), any(ChangeDailyTodoSequenceRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(changeDailyTodoSequenceRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_유효하지않은일일플래너() throws Exception {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerServiceImpl).changeDailyTodoSequence(any(), any(ChangeDailyTodoSequenceRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(changeDailyTodoSequenceRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공() throws Exception {
            //given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(changeDailyTodoSequenceRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );


            //then
            resultActions.andExpect(status().isOk());
        }
    }

    @Nested
    class 일일플래너수정 {
        @Nested
        class 오늘의다짐편집 {
            final String url = "/api/planners/{userId}/daily/today-goals";
            final UpdateTodayGoalRequest updateTodayGoalRequest = UpdateTodayGoalRequest.builder()
                    .date(date)
                    .todayGoal("🎧 Dreams Come True - NCT127")
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTodayGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTodayGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 내일의다짐편집 {
            final String url = "/api/planners/{userId}/daily/tomorrow-goals";
            final UpdateTomorrowGoalRequest updateTomorrowGoalRequest = UpdateTomorrowGoalRequest.builder()
                    .date(date)
                    .tomorrowGoal("이제는 더이상 물러나 곳이 없다.")
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTomorrowGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTomorrowGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 오늘의회고편집 {
            final String url = "/api/planners/{userId}/daily/retrospections";
            final UpdateRetrospectionRequest updateRetrospectionRequest = UpdateRetrospectionRequest.builder()
                    .date(date)
                    .retrospection("오늘 계획했던 일을 모두 끝냈다!!! 신남~~")
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 오늘의회고사진업로드 {
            final String url = "/api/planners/{userId}/daily/retrospection-images";
            final String imageUrl = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";
            final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                    .date(date)
                    .retrospectionImage(imageUrl)
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionImageRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 성공_오늘의회고사진Null() throws Exception {
                //given
                final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                        .date(date)
                        .retrospectionImage(null)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionImageRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionImageRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class 일일플래너수정_실패케이스모음_유효하지않은요청값 {

        @ParameterizedTest
        @MethodSource("invalidUpdateTodayGoalRequest")
        void 오늘의다짐_실패(final UpdateTodayGoalRequest updateTodayGoalRequest) throws Exception {
            // given
            final String url = "/api/planners/{userId}/daily/today-goals";

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateTodayGoalRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidUpdateTodayGoalRequest() {
            final String todayGoal = "🎧 Dreams Come True - NCT127";
            return Stream.of(
                    // 올바르지 않은 날짜 형식
                    Arguments.of(UpdateTodayGoalRequest.builder()
                            .date("2023.09.26")
                            .todayGoal(todayGoal)
                            .build()),
                    // 오늘의 다짐 길이초과
                    Arguments.of(UpdateTodayGoalRequest.builder()
                            .date(date)
                            .todayGoal("0123456789012345678901234567890123456789012345678901")
                            .build()),
                    // 날짜 Null
                    Arguments.of(UpdateTodayGoalRequest.builder()
                            .date(null)
                            .todayGoal(todayGoal)
                            .build()),
                    // 오늘의 다짐 내용 Null
                    Arguments.of(UpdateTodayGoalRequest.builder()
                            .date(date)
                            .todayGoal(null)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidUpdateTomorrowGoalRequest")
        void 내일의다짐_실패(final UpdateTomorrowGoalRequest updateTomorrowGoalRequest) throws Exception {
            // given
            final String url = "/api/planners/{userId}/daily/tomorrow-goals";

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateTomorrowGoalRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidUpdateTomorrowGoalRequest() {
            final String tomorrowGoal = "이제는 더이상 물러나 곳이 없다.";
            return Stream.of(
                    // 올바르지 않은 날짜 형식
                    Arguments.of(UpdateTomorrowGoalRequest.builder()
                            .date("2023.09.26")
                            .tomorrowGoal(tomorrowGoal)
                            .build()),
                    // 내일의 다짐 길이초과
                    Arguments.of(UpdateTomorrowGoalRequest.builder()
                            .date(date)
                            .tomorrowGoal("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                            .build()),
                    // 날짜 Null
                    Arguments.of(UpdateTomorrowGoalRequest.builder()
                            .date(null)
                            .tomorrowGoal(tomorrowGoal)
                            .build()),
                    // 내일의 다짐 내용 Null
                    Arguments.of(UpdateTomorrowGoalRequest.builder()
                            .date(date)
                            .tomorrowGoal(null)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidUpdateRetrospectionRequest")
        void 오늘의회고_실패(final UpdateRetrospectionRequest updateRetrospectionRequest) throws Exception {
            // given
            final String url = "/api/planners/{userId}/daily/retrospections";

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateRetrospectionRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidUpdateRetrospectionRequest() {
            final String retrospection = "오늘 계획했던 일을 모두 끝냈다!!! 신남~~";
            return Stream.of(
                    // 올바르지 않은 날짜 형식
                    Arguments.of(UpdateRetrospectionRequest.builder()
                            .date("2023.09.26")
                            .retrospection(retrospection)
                            .build()),
                    // 오늘의 회고 길이초과
                    Arguments.of(UpdateRetrospectionRequest.builder()
                            .date("2023-09-26")
                            .retrospection("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                                    "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                                    "012345678901234567890123456789012345678901234567890")
                            .build()),
                    // 날짜 Null
                    Arguments.of(UpdateRetrospectionRequest.builder()
                            .date(null)
                            .retrospection(retrospection)
                            .build()),
                    // 오늘의 회고 내용 Null
                    Arguments.of(UpdateRetrospectionRequest.builder()
                            .date("2023-09-26")
                            .retrospection(null)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidUpdateRetrospectionImageRequest")
        void 오늘의회고사진업로드_실패(final UpdateRetrospectionImageRequest updateRetrospectionImageRequest) throws Exception {
            // given
            final String url = "/api/planners/{userId}/daily/retrospection-images";

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateRetrospectionImageRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidUpdateRetrospectionImageRequest() {
            final String imageUrl = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";

            return Stream.of(
                    // 올바르지 않은 날짜 형식
                    Arguments.of(UpdateRetrospectionImageRequest.builder()
                            .date("2023.09.26")
                            .retrospectionImage(imageUrl)
                            .build()),
                    // 날짜 Null
                    Arguments.of(UpdateRetrospectionImageRequest.builder()
                            .date(null)
                            .retrospectionImage(imageUrl)
                            .build())
            );
        }

    }

    @Nested
    class 좋아요 {
        final String url = "/api/planners/{userId}/daily/likes";

        @Nested
        class 좋아요등록 {

            final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                    .date(date)
                    .build();

            @Test
            void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                        .date("2023.09.28")
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_날짜Null() throws Exception {
                //given
                final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                        .date(null)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_자신플래너에좋아요() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER)).when(dailyPlannerServiceImpl).addDailyLike(any(), any(Long.class), any(AddDailyLikeRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은사용자() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.INVALID_USER)).when(dailyPlannerServiceImpl).addDailyLike(any(), any(Long.class), any(AddDailyLikeRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은플래너() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerServiceImpl).addDailyLike(any(), any(Long.class), any(AddDailyLikeRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_이전에좋아요를이미누름() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.ALREADY_ADDED_LIKE)).when(dailyPlannerServiceImpl).addDailyLike(any(), any(Long.class), any(AddDailyLikeRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }

        }

        @Nested
        class 좋아요취소 {
            final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                    .date(date)
                    .build();

            @Test
            void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                        .date("2023.09.28")
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_날짜Null() throws Exception {
                //given
                final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                        .date(null)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_자신플래너에좋아요취소() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER)).when(dailyPlannerServiceImpl).removeDailyLike(any(), any(Long.class), any(RemoveDailyLikeRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은사용자() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.INVALID_USER)).when(dailyPlannerServiceImpl).removeDailyLike(any(), any(Long.class), any(RemoveDailyLikeRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은플래너() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerServiceImpl).removeDailyLike(any(), any(Long.class), any(RemoveDailyLikeRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }

        }
    }

    @Nested
    class 주간플래너 {
        final String url = "/api/planners/{userId}/weekly/todos";

        @Nested
        class 주차별할일등록 {
            private final String startDay = "2023-10-09";
            private final String endDay = "2023-10-15";
            private final String weeklyTodoContent = "자기소개서 제출하기";

            final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                    .startDate(startDay)
                    .endDate(endDay)
                    .weeklyTodoContent(weeklyTodoContent)
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addWeeklyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_올바르지않은날짜_시작요일이월요일이아님() throws Exception {
                //given
                final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                        .startDate("2023-10-10")
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .build();
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE)).when(weeklyPlannerServiceImpl).addWeeklyTodo(any(), any(AddWeeklyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addWeeklyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_올바르지않은날짜_일주일간격이아님() throws Exception {
                //given
                final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                        .startDate(startDay)
                        .endDate("2023-10-16")
                        .weeklyTodoContent(weeklyTodoContent)
                        .build();
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE)).when(weeklyPlannerServiceImpl).addWeeklyTodo(any(), any(AddWeeklyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addWeeklyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addWeeklyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 주차별할일내용수정 {
            final String startDay = "2023-10-09";
            final String endDay = "2023-10-15";
            final String weeklyTodoContent = "자기소개서 제출하기";
            final Long weeklyTodoId = 1L;

            final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                    .startDate(startDay)
                    .endDate(endDay)
                    .weeklyTodoContent(weeklyTodoContent)
                    .weeklyTodoId(weeklyTodoId)
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoContentRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_올바르지않은날짜_시작요일이월요일이아님() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate("2023-10-10")
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .weeklyTodoId(weeklyTodoId)
                        .build();

                doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE)).when(weeklyPlannerServiceImpl).updateWeeklyTodoContent(any(), any(UpdateWeeklyTodoContentRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoContentRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_올바르지않은날짜_일주일간격이아님() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate(startDay)
                        .endDate("2023-10-16")
                        .weeklyTodoContent(weeklyTodoContent)
                        .weeklyTodoId(weeklyTodoId)
                        .build();

                doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE)).when(weeklyPlannerServiceImpl).updateWeeklyTodoContent(any(), any(UpdateWeeklyTodoContentRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoContentRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은위클리() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.INVALID_WEEKLY_PLANNER)).when(weeklyPlannerServiceImpl).updateWeeklyTodoContent(any(), any(UpdateWeeklyTodoContentRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoContentRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은위클리할일() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO)).when(weeklyPlannerServiceImpl).updateWeeklyTodoContent(any(), any(UpdateWeeklyTodoContentRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoContentRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoContentRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 주차별할일상태수정 {
            final String url = "/api/planners/{userId}/weekly/todos-status";
            final String startDay = "2023-10-09";
            final String endDay = "2023-10-15";
            final Long weeklyTodoId = 1L;

            final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                    .startDate(startDay)
                    .endDate(endDay)
                    .weeklyTodoId(weeklyTodoId)
                    .weeklyTodoStatus(true)
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoStatusRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_올바르지않은날짜_시작요일이월요일이아님() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate("2023-10-10")
                        .endDate(endDay)
                        .weeklyTodoId(weeklyTodoId)
                        .weeklyTodoStatus(true)
                        .build();

                doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE)).when(weeklyPlannerServiceImpl).updateWeeklyTodoStatus(any(), any(UpdateWeeklyTodoStatusRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoStatusRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_올바르지않은날짜_일주일간격이아님() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate(startDay)
                        .endDate("2023-10-16")
                        .weeklyTodoId(weeklyTodoId)
                        .weeklyTodoStatus(true)
                        .build();

                doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE)).when(weeklyPlannerServiceImpl).updateWeeklyTodoStatus(any(), any(UpdateWeeklyTodoStatusRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoStatusRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은위클리() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.INVALID_WEEKLY_PLANNER)).when(weeklyPlannerServiceImpl).updateWeeklyTodoStatus(any(), any(UpdateWeeklyTodoStatusRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoStatusRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은위클리할일() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO)).when(weeklyPlannerServiceImpl).updateWeeklyTodoStatus(any(), any(UpdateWeeklyTodoStatusRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoStatusRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateWeeklyTodoStatusRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 주차별할일삭제 {
            private final String startDay = "2023-10-09";
            private final String endDay = "2023-10-15";
            final RemoveWeeklyTodoRequest removeWeeklyTodoRequest = RemoveWeeklyTodoRequest.builder()
                    .startDate(startDay)
                    .endDate(endDay)
                    .weeklyTodoId(1L)
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeWeeklyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_올바르지않은날짜_시작요일이월요일이아님() throws Exception {
                //given
                final RemoveWeeklyTodoRequest removeWeeklyTodoRequest = RemoveWeeklyTodoRequest.builder()
                        .startDate("2023-10-10")
                        .endDate(endDay)
                        .weeklyTodoId(1L)
                        .build();
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE)).when(weeklyPlannerServiceImpl).removeWeeklyTodo(any(), any(RemoveWeeklyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeWeeklyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_올바르지않은날짜_일주일간격이아님() throws Exception {
                //given
                final RemoveWeeklyTodoRequest removeWeeklyTodoRequest = RemoveWeeklyTodoRequest.builder()
                        .startDate(startDay)
                        .endDate("2023-10-16")
                        .weeklyTodoId(1L)
                        .build();
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE)).when(weeklyPlannerServiceImpl).removeWeeklyTodo(any(), any(RemoveWeeklyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeWeeklyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은위클리() throws Exception {
                //given

                doThrow(new PlannerException(PlannerErrorResult.INVALID_WEEKLY_PLANNER)).when(weeklyPlannerServiceImpl).removeWeeklyTodo(any(), any(RemoveWeeklyTodoRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeWeeklyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeWeeklyTodoRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class 주간플래너_실패케이스모음_유효하지않은요청값 {

        final String url = "/api/planners/{userId}/weekly/todos";
        final String startDay = "2023-10-09";
        final String endDay = "2023-10-15";
        final Long weeklyTodoId = 1L;
        final String weeklyTodoContent = "자기소개서 제출하기";

        @ParameterizedTest
        @MethodSource("invalidAddWeeklyTodoRequest")
        void 주차별할일등록_실패(final AddWeeklyTodoRequest addWeeklyTodoRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addWeeklyTodoRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidAddWeeklyTodoRequest() {
            return Stream.of(
                    // 올바르지 않은 시작 날짜 형식
                    Arguments.of(AddWeeklyTodoRequest.builder()
                            .startDate("2023.10.09")
                            .endDate(endDay)
                            .weeklyTodoContent(weeklyTodoContent)
                            .build()),
                    // 올바르지 않은 끝 날짜 형식
                    Arguments.of(AddWeeklyTodoRequest.builder()
                            .startDate(startDay)
                            .endDate("2023.10.15")
                            .weeklyTodoContent(weeklyTodoContent)
                            .build()),
                    // 할일 내용 길이초과
                    Arguments.of(AddWeeklyTodoRequest.builder()
                            .startDate(startDay)
                            .endDate(endDay)
                            .weeklyTodoContent("012345678901234567890123456789012345678901234567891")
                            .build()),
                    // 시작 날짜 Null
                    Arguments.of(AddWeeklyTodoRequest.builder()
                            .startDate(null)
                            .endDate(endDay)
                            .weeklyTodoContent(weeklyTodoContent)
                            .build()),
                    // 끝 날짜 Null
                    Arguments.of(AddWeeklyTodoRequest.builder()
                            .startDate(startDay)
                            .endDate(null)
                            .weeklyTodoContent(weeklyTodoContent)
                            .build()),
                    // 할일 내용 Null
                    Arguments.of(AddWeeklyTodoRequest.builder()
                            .startDate(startDay)
                            .endDate(endDay)
                            .weeklyTodoContent(null)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidUpdateWeeklyTodoContentRequest")
        void 주차별할일내용수정_실패(final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateWeeklyTodoContentRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidUpdateWeeklyTodoContentRequest() {
            return Stream.of(
                    // 올바르지 않은 시작 날짜 형식
                    Arguments.of(UpdateWeeklyTodoContentRequest.builder()
                            .startDate("2023.10.09")
                            .endDate(endDay)
                            .weeklyTodoContent(weeklyTodoContent)
                            .weeklyTodoId(weeklyTodoId)
                            .build()),
                    // 올바르지 않은 끝 날짜 형식
                    Arguments.of(UpdateWeeklyTodoContentRequest.builder()
                            .startDate(startDay)
                            .endDate("2023.10.15")
                            .weeklyTodoContent(weeklyTodoContent)
                            .weeklyTodoId(weeklyTodoId)
                            .build()),
                    // 할일 내용 길이초과
                    Arguments.of(UpdateWeeklyTodoContentRequest.builder()
                            .startDate(startDay)
                            .endDate(endDay)
                            .weeklyTodoContent("012345678901234567890123456789012345678901234567891")
                            .weeklyTodoId(weeklyTodoId)
                            .build()),
                    // 시작 날짜 Null
                    Arguments.of(UpdateWeeklyTodoContentRequest.builder()
                            .startDate(null)
                            .endDate(endDay)
                            .weeklyTodoContent(weeklyTodoContent)
                            .weeklyTodoId(weeklyTodoId)
                            .build()),
                    // 끝 날짜 Null
                    Arguments.of(UpdateWeeklyTodoContentRequest.builder()
                            .startDate(startDay)
                            .endDate(null)
                            .weeklyTodoContent(weeklyTodoContent)
                            .weeklyTodoId(weeklyTodoId)
                            .build()),
                    // 할일 내용 Null
                    Arguments.of(UpdateWeeklyTodoContentRequest.builder()
                            .startDate(startDay)
                            .endDate(endDay)
                            .weeklyTodoContent(null)
                            .weeklyTodoId(weeklyTodoId)
                            .build()),
                    // 할일 ID Null
                    Arguments.of(UpdateWeeklyTodoContentRequest.builder()
                            .startDate(startDay)
                            .endDate(endDay)
                            .weeklyTodoContent(weeklyTodoContent)
                            .weeklyTodoId(null)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidUpdateWeeklyTodoStatusRequest")
        void 주차별할일상태수정_실패(final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest) throws Exception {
            // given
            final String url = "/api/planners/{userId}/weekly/todos-status";

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateWeeklyTodoStatusRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidUpdateWeeklyTodoStatusRequest() {
            return Stream.of(
                    // 올바르지 않은 시작 날짜 형식
                    Arguments.of(UpdateWeeklyTodoStatusRequest.builder()
                            .startDate("2023.10.09")
                            .endDate(endDay)
                            .weeklyTodoId(weeklyTodoId)
                            .weeklyTodoStatus(true)
                            .build()),
                    // 올바르지 않은 끝 날짜 형식
                    Arguments.of(UpdateWeeklyTodoStatusRequest.builder()
                            .startDate(startDay)
                            .endDate("2023.10.15")
                            .weeklyTodoId(weeklyTodoId)
                            .weeklyTodoStatus(true)
                            .build()),
                    // 시작 날짜 Null
                    Arguments.of(UpdateWeeklyTodoStatusRequest.builder()
                            .startDate(null)
                            .endDate(endDay)
                            .weeklyTodoId(weeklyTodoId)
                            .weeklyTodoStatus(true)
                            .build()),
                    // 끝 날짜 Null
                    Arguments.of(UpdateWeeklyTodoStatusRequest.builder()
                            .startDate(startDay)
                            .endDate(null)
                            .weeklyTodoId(weeklyTodoId)
                            .weeklyTodoStatus(true)
                            .build()),
                    // 할일 ID Null
                    Arguments.of(UpdateWeeklyTodoStatusRequest.builder()
                            .startDate(startDay)
                            .endDate(endDay)
                            .weeklyTodoId(null)
                            .weeklyTodoStatus(true)
                            .build()),
                    // 할일 상태 Null
                    Arguments.of(UpdateWeeklyTodoStatusRequest.builder()
                            .startDate(startDay)
                            .endDate(endDay)
                            .weeklyTodoId(weeklyTodoId)
                            .weeklyTodoStatus(null)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidRemoveWeeklyTodoRequest")
        void 주차별할일삭제_실패(final RemoveWeeklyTodoRequest removeWeeklyTodoRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, userId)
                            .content(gson.toJson(removeWeeklyTodoRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidRemoveWeeklyTodoRequest() {
            return Stream.of(
                    // 올바르지 않은 시작 날짜 형식
                    Arguments.of(RemoveWeeklyTodoRequest.builder()
                            .startDate("2023.10.09")
                            .endDate(endDay)
                            .weeklyTodoId(weeklyTodoId)
                            .build()),
                    // 올바르지 않은 끝 날짜 형식
                    Arguments.of(RemoveWeeklyTodoRequest.builder()
                            .startDate(startDay)
                            .endDate("2023.10.15")
                            .weeklyTodoId(weeklyTodoId)
                            .build()),
                    // 시작 날짜 Null
                    Arguments.of(RemoveWeeklyTodoRequest.builder()
                            .startDate(null)
                            .endDate(endDay)
                            .weeklyTodoId(weeklyTodoId)
                            .build()),
                    // 끝 날짜 Null
                    Arguments.of(RemoveWeeklyTodoRequest.builder()
                            .startDate(startDay)
                            .endDate(null)
                            .weeklyTodoId(weeklyTodoId)
                            .build()),
                    // 할일 ID Null
                    Arguments.of(RemoveWeeklyTodoRequest.builder()
                            .startDate(startDay)
                            .endDate(endDay)
                            .weeklyTodoId(null)
                            .build())
            );
        }
    }

    @Nested
    class 타임테이블 {
        final String url = "/api/planners/{userId}/daily/timetables";
        final String date = "2023-10-06";
        final String startTime = "2023-10-06 23:50";
        final String endTime = "2023-10-07 01:30";

        @Nested
        class 타임테이블등록 {

            final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                    .date(date)
                    .startTime(startTime)
                    .endTime(endTime)
                    .todoId(1L)
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addTimeTableRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_잘못된시간값() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_TIME)).when(dailyPlannerServiceImpl).addTimeTable(any(), any(AddTimeTableRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addTimeTableRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은플래너() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerServiceImpl).addTimeTable(any(), any(AddTimeTableRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addTimeTableRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은할일() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO)).when(dailyPlannerServiceImpl).addTimeTable(any(), any(AddTimeTableRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addTimeTableRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_타임테이블등록_불가상태() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.FAILED_ADDED_TIMETABLE)).when(dailyPlannerServiceImpl).addTimeTable(any(), any(AddTimeTableRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addTimeTableRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addTimeTableRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }

        }

        @Nested
        class 타임테이블삭제 {

            final RemoveTimeTableRequest removeTimeTableRequest = RemoveTimeTableRequest.builder()
                    .date(date)
                    .todoId(1L)
                    .timeTableId(1L)
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeTimeTableRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_유효하지않은플래너() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerServiceImpl).removeTimeTable(any(), any(RemoveTimeTableRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeTimeTableRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은할일() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_TODO)).when(dailyPlannerServiceImpl).removeTimeTable(any(), any(RemoveTimeTableRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeTimeTableRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeTimeTableRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }

        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class 타임테이블_실패케이스모음_유효하지않은요청값 {

        final String url = "/api/planners/{userId}/daily/timetables";
        final String date = "2023-10-06";
        final String startTime = "2023-10-06 23:50";
        final String endTime = "2023-10-07 01:30";
        final long todoId = 1L;

        @ParameterizedTest
        @MethodSource("invalidAddTimeTableRequest")
        void 타임테이블등록_실패(final AddTimeTableRequest addTimeTableRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addTimeTableRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidAddTimeTableRequest() {
            return Stream.of(
                    // 올바르지 않은 날짜 형식
                    Arguments.of(AddTimeTableRequest.builder()
                            .date("2023.10.06")
                            .startTime(startTime)
                            .endTime(endTime)
                            .todoId(todoId)
                            .build()),
                    // 올바르지 않은 시작 날짜 시간 형식
                    Arguments.of(AddTimeTableRequest.builder()
                            .date(date)
                            .startTime("2023-10-06 23:59")
                            .endTime(endTime)
                            .todoId(todoId)
                            .build()),
                    // 올바르지 않은 종료 날짜 시간 형식
                    Arguments.of(AddTimeTableRequest.builder()
                            .date(date)
                            .startTime(startTime)
                            .endTime("2023-10-0623:50")
                            .todoId(todoId)
                            .build()),
                    // 날짜 Null
                    Arguments.of(AddTimeTableRequest.builder()
                            .date(null)
                            .startTime(startTime)
                            .endTime(endTime)
                            .todoId(todoId)
                            .build()),
                    // 시작 날짜 시간 Null
                    Arguments.of(AddTimeTableRequest.builder()
                            .date(date)
                            .startTime(null)
                            .endTime(endTime)
                            .todoId(todoId)
                            .build()),
                    // 종료 날짜 시간 Null
                    Arguments.of(AddTimeTableRequest.builder()
                            .date(date)
                            .startTime(startTime)
                            .endTime(null)
                            .todoId(todoId)
                            .build()),
                    // 할일 ID Null
                    Arguments.of(AddTimeTableRequest.builder()
                            .date(date)
                            .startTime(startTime)
                            .endTime(endTime)
                            .todoId(null)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidRemoveTimeTableRequest")
        void 타임테이블삭제_실패(final RemoveTimeTableRequest removeTimeTableRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, userId)
                            .content(gson.toJson(removeTimeTableRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidRemoveTimeTableRequest() {
            return Stream.of(
                    // 올바르지 않은 날짜 형식
                    Arguments.of(RemoveTimeTableRequest.builder()
                            .date("2023.10.06")
                            .todoId(todoId)
                            .build()),
                    // 날짜 Null
                    Arguments.of(RemoveTimeTableRequest.builder()
                            .date(null)
                            .todoId(todoId)
                            .build()),
                    // 할일 ID Null
                    Arguments.of(RemoveTimeTableRequest.builder()
                            .date(date)
                            .todoId(null)
                            .build())
            );
        }
    }

    @Nested
    class 일일플래너조회 {
        final String url = "/api/planners/{userId}/daily";

        @Test
        void 실패_올바르지않은날짜형식() throws Exception {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT)).when(searchPlannerServiceImpl).searchDailyPlanner(any(), any(Long.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("date", "2023.10.10")
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_유효하지않은플래너작성자() throws Exception {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_USER)).when(searchPlannerServiceImpl).searchDailyPlanner(any(), any(Long.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("date", date)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공() throws Exception {
            //given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("date", date)
            );

            //then
            resultActions.andExpect(status().isOk());
        }
    }

    @Nested
    class 주간플래너조회 {
        final String url = "/api/planners/{userId}/weekly";
        private final String startDate = "2023-10-09";
        private final String endDate = "2023-10-15";

        @Test
        void 실패_올바르지않은날짜형식() throws Exception {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT)).when(searchPlannerServiceImpl).searchWeeklyPlanner(any(), any(Long.class), any(String.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("start-date", "2023.10.09")
                            .param("end-date", endDate)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_유효하지않은플래너작성자() throws Exception {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_USER)).when(searchPlannerServiceImpl).searchWeeklyPlanner(any(), any(Long.class), any(String.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("start-date", startDate)
                            .param("end-date", endDate)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_올바르지않은날짜() throws Exception {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE)).when(searchPlannerServiceImpl).searchWeeklyPlanner(any(), any(Long.class), any(String.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("start-date", "2023-10-10")
                            .param("end-date", endDate)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공() throws Exception {
            //given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("start-date", startDate)
                            .param("end-date", endDate)
            );

            //then
            resultActions.andExpect(status().isOk());
        }
    }

    @Nested
    class 캘린더조회 {
        final String url = "/api/planners/{userId}/calendars";
        private final String date = "2023-10-01";

        @Test
        void 실패_올바르지않은날짜형식() throws Exception {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT)).when(searchPlannerServiceImpl).searchCalendar(any(), any(Long.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("date", "2023.10.01")
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_유효하지않은플래너작성자() throws Exception {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_USER)).when(searchPlannerServiceImpl).searchCalendar(any(), any(Long.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("date", date)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공() throws Exception {
            //given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("date", date)
            );

            //then
            resultActions.andExpect(status().isOk());
        }
    }

    @Nested
    class 소셜공유 {
        final String url = "/api/planners/{userId}/daily/social";
        final String socialImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";
        final ShareSocialRequest shareSocialRequest = ShareSocialRequest.builder()
                .date(date)
                .socialImage(socialImage)
                .build();

        @Test
        void 실패_없는사용자() throws Exception {
            //given

            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(shareSocialRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        void 실패_공개상태가아닌경우() throws Exception {
            //given

            doThrow(new PlannerException(PlannerErrorResult.FAILED_SHARE_SOCIAL)).when(dailyPlannerServiceImpl).shareSocial(any(), any(ShareSocialRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(shareSocialRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_유효하지않은플래너() throws Exception {
            //given

            doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerServiceImpl).shareSocial(any(), any(ShareSocialRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(shareSocialRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공() throws Exception {
            //given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(shareSocialRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isAccepted());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class 소셜공유_실패케이스모음_유효하지않은요청값 {

        final String url = "/api/planners/{userId}/daily/social";
        final String socialImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";

        @ParameterizedTest
        @MethodSource("invalidShareSocialRequest")
        void 소셜공유_실패(final ShareSocialRequest shareSocialRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(shareSocialRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidShareSocialRequest() {
            return Stream.of(
                    // 올바르지 않은 날짜 형식
                    Arguments.of(ShareSocialRequest.builder()
                            .date("2023/10/16")
                            .socialImage(socialImage)
                            .build()),
                    // 날짜 Null
                    Arguments.of(ShareSocialRequest.builder()
                            .date(null)
                            .socialImage(socialImage)
                            .build()),
                    // 소셜 이미지 Null
                    Arguments.of(ShareSocialRequest.builder()
                            .date(date)
                            .socialImage(null)
                            .build())
            );
        }

    }

    @Nested
    class 방명록 {
        final String url = "/api/planners/{userId}/monthly/visitor-books";

        @Nested
        class 방명록추가 {
            private final String visitorBookContent = "왔다가유 @--";
            final AddVisitorBookRequest addVisitorBookRequest = AddVisitorBookRequest.builder()
                    .visitorBookContent(visitorBookContent)
                    .build();

            @Test
            void 실패_방명록내용Null() throws Exception {
                //given
                final AddVisitorBookRequest addVisitorBookRequest = AddVisitorBookRequest.builder()
                        .visitorBookContent(null)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addVisitorBookRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_방명록내용_길이초과() throws Exception {
                //given
                final AddVisitorBookRequest addVisitorBookRequest = AddVisitorBookRequest.builder()
                        .visitorBookContent("0123456789012345678901234567891")
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addVisitorBookRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은사용자() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_USER)).when(monthlyPlannerServiceImpl).addVisitorBook(any(), any(Long.class), any(AddVisitorBookRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addVisitorBookRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_자신플래너에방명록추가() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.FAILED_SELF_VISITOR_BOOK_WRITING)).when(monthlyPlannerServiceImpl).addVisitorBook(any(), any(Long.class), any(AddVisitorBookRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addVisitorBookRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addVisitorBookRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }

        }

        @Nested
        class 방명록삭제 {
            final RemoveVisitorBookRequest removeVisitorBookRequest = RemoveVisitorBookRequest.builder()
                    .visitorBookId(1L)
                    .build();

            @Test
            void 실패_방명록IDNull() throws Exception {
                //given
                final RemoveVisitorBookRequest removeVisitorBookRequest = RemoveVisitorBookRequest.builder()
                        .visitorBookId(null)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeVisitorBookRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은방명록() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_VISITOR_BOOK)).when(monthlyPlannerServiceImpl).removeVisitorBook(any(), any(Long.class), any(RemoveVisitorBookRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeVisitorBookRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_삭제권한이없는사용자() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.NO_PERMISSION_TO_REMOVE_VISITOR_BOOK)).when(monthlyPlannerServiceImpl).removeVisitorBook(any(), any(Long.class), any(RemoveVisitorBookRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeVisitorBookRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeVisitorBookRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }

        }

        @Nested
        class 방명록조회 {

            @Test
            void 실패_유효하지않은사용자() throws Exception {
                //given
                doThrow(new PlannerException(PlannerErrorResult.INVALID_USER)).when(monthlyPlannerServiceImpl).searchVisitorBook(any(), any(Long.class), any(Long.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                                .param("last", "0")
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공_처음() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                                .param("last", "0")
                );

                //then
                resultActions.andExpect(status().isOk());
            }

            @Test
            void 성공_연속() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                                .param("last", "100")
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }
    }
}