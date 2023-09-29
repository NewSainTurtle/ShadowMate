package com.newsainturtle.shadowmate.yn.planner;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.planner.controller.PlannerController;
import com.newsainturtle.shadowmate.planner.dto.*;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PlannerControllerTest {

    @InjectMocks
    private PlannerController plannerController;

    @Mock
    private DailyPlannerServiceImpl dailyPlannerServiceImpl;

    @Mock
    private AuthService authServiceImpl;

    private MockMvc mockMvc;
    private Gson gson;
    final Long userId = 1L;


    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(plannerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 일일플래너할일 {
        final String url = "/api/planners/{userId}/daily/todos";

        @Nested
        class 할일등록 {
            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .date("2023-09-25")
                        .build();
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
            public void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .date("2023.09.25")
                        .build();

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
            public void 실패_할일내용_길이초과() throws Exception {
                //given
                final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                        .todoContent("012345678901234567890123456789012345678901234567891")
                        .categoryId(1L)
                        .date("2023-09-25")
                        .build();

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
            public void 실패_날짜Null() throws Exception {
                //given
                final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .date(null)
                        .build();

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
            public void 실패_할일내용Null() throws Exception {
                //given
                final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                        .todoContent(null)
                        .categoryId(1L)
                        .date("2023-09-25")
                        .build();

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
            public void 실패_카테고리Null() throws Exception {
                //given
                final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(null)
                        .date("2023-09-25")
                        .build();

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
            public void 실패_유효하지않은카테고리() throws Exception {
                //given
                final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .date("2023-09-25")
                        .build();
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
            public void 성공() throws Exception {
                //given
                final AddDailyTodoRequest addDailyTodoRequest = AddDailyTodoRequest.builder()
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .date("2023-09-25")
                        .build();

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
        class 할일삭제 {
            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final RemoveDailyTodoRequest removeDailyTodoRequest = RemoveDailyTodoRequest.builder()
                        .todoId(1L)
                        .date("2023-09-25")
                        .build();
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
            public void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final RemoveDailyTodoRequest removeDailyTodoRequest = RemoveDailyTodoRequest.builder()
                        .todoId(1L)
                        .date("2023.09.25")
                        .build();

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
            public void 실패_날짜Null() throws Exception {
                //given
                final RemoveDailyTodoRequest removeDailyTodoRequest = RemoveDailyTodoRequest.builder()
                        .todoId(1L)
                        .date(null)
                        .build();

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
            public void 실패_할일ID_Null() throws Exception {
                //given
                final RemoveDailyTodoRequest removeDailyTodoRequest = RemoveDailyTodoRequest.builder()
                        .todoId(null)
                        .date("2023-09-25")
                        .build();

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
            public void 실패_유효하지않은플래너() throws Exception {
                //given
                final RemoveDailyTodoRequest removeDailyTodoRequest = RemoveDailyTodoRequest.builder()
                        .todoId(1L)
                        .date("2023-09-25")
                        .build();
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
            public void 성공() throws Exception {
                //given
                final RemoveDailyTodoRequest removeDailyTodoRequest = RemoveDailyTodoRequest.builder()
                        .todoId(1L)
                        .date("2023-09-25")
                        .build();

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
    class 일일플래너수정 {
        @Nested
        class 오늘의다짐편집 {
            final String url = "/api/planners/{userId}/daily/today-goals";

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final UpdateTodayGoalRequest updateTodayGoalRequest = UpdateTodayGoalRequest.builder()
                        .date("2023-09-26")
                        .todayGoal("🎧 Dreams Come True - NCT127")
                        .build();
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
            public void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final UpdateTodayGoalRequest updateTodayGoalRequest = UpdateTodayGoalRequest.builder()
                        .date("2023.09.26")
                        .todayGoal("🎧 Dreams Come True - NCT127")
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTodayGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 실패_오늘의다짐_길이초과() throws Exception {
                //given
                final UpdateTodayGoalRequest updateTodayGoalRequest = UpdateTodayGoalRequest.builder()
                        .date("2023-09-26")
                        .todayGoal("0123456789012345678901234567890123456789012345678901")
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTodayGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 실패_날짜Null() throws Exception {
                //given
                final UpdateTodayGoalRequest updateTodayGoalRequest = UpdateTodayGoalRequest.builder()
                        .date(null)
                        .todayGoal("🎧 Dreams Come True - NCT127")
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTodayGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 실패_오늘의다짐Null() throws Exception {
                //given
                final UpdateTodayGoalRequest updateTodayGoalRequest = UpdateTodayGoalRequest.builder()
                        .date("2023-09-26")
                        .todayGoal(null)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTodayGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 성공() throws Exception {
                //given
                final UpdateTodayGoalRequest updateTodayGoalRequest = UpdateTodayGoalRequest.builder()
                        .date("2023-09-26")
                        .todayGoal("🎧 Dreams Come True - NCT127")
                        .build();

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

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final UpdateTomorrowGoalRequest updateTomorrowGoalRequest = UpdateTomorrowGoalRequest.builder()
                        .date("2023-09-26")
                        .tomorrowGoal("이제는 더이상 물러나 곳이 없다.")
                        .build();
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
            public void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final UpdateTomorrowGoalRequest updateTomorrowGoalRequest = UpdateTomorrowGoalRequest.builder()
                        .date("2023.09.26")
                        .tomorrowGoal("이제는 더이상 물러나 곳이 없다.")
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTomorrowGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 실패_내일의다짐_길이초과() throws Exception {
                //given
                final UpdateTomorrowGoalRequest updateTomorrowGoalRequest = UpdateTomorrowGoalRequest.builder()
                        .date("2023-09-26")
                        .tomorrowGoal("012345678901234567890123456789012345678901234567890")
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTomorrowGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 실패_날짜Null() throws Exception {
                //given
                final UpdateTomorrowGoalRequest updateTomorrowGoalRequest = UpdateTomorrowGoalRequest.builder()
                        .date(null)
                        .tomorrowGoal("이제는 더이상 물러나 곳이 없다.")
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTomorrowGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 실패_내일의다짐Null() throws Exception {
                //given
                final UpdateTomorrowGoalRequest updateTomorrowGoalRequest = UpdateTomorrowGoalRequest.builder()
                        .date("2023-09-26")
                        .tomorrowGoal(null)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateTomorrowGoalRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 성공() throws Exception {
                //given
                final UpdateTomorrowGoalRequest updateTomorrowGoalRequest = UpdateTomorrowGoalRequest.builder()
                        .date("2023-09-26")
                        .tomorrowGoal("이제는 더이상 물러나 곳이 없다.")
                        .build();

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
    }

    @Nested
    class 좋아요 {
        final String url = "/api/planners/{userId}/daily/likes";

        @Nested
        class 좋아요등록 {

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(1L)
                        .build();

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            public void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                        .date("2023.09.28")
                        .anotherUserId(1L)
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
            public void 실패_날짜Null() throws Exception {
                //given
                final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                        .date(null)
                        .anotherUserId(1L)
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
            public void 실패_ID_Null() throws Exception {
                //given
                final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(null)
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
            public void 실패_자신플래너에좋아요() throws Exception {
                //given
                final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(1L)
                        .build();

                doThrow(new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER)).when(dailyPlannerServiceImpl).addDailyLike(any(), any(AddDailyLikeRequest.class));

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
            public void 실패_유효하지않은플래너() throws Exception {
                //given
                final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(1L)
                        .build();

                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerServiceImpl).addDailyLike(any(), any(AddDailyLikeRequest.class));

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
            public void 실패_이전에좋아요를이미누름() throws Exception {
                //given
                final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(1L)
                        .build();

                doThrow(new PlannerException(PlannerErrorResult.ALREADY_ADDED_LIKE)).when(dailyPlannerServiceImpl).addDailyLike(any(), any(AddDailyLikeRequest.class));

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
            public void 성공() throws Exception {
                //given
                final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(1L)
                        .build();

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

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(1L)
                        .build();

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDailyLikeRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            public void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                        .date("2023.09.28")
                        .anotherUserId(1L)
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
            public void 실패_날짜Null() throws Exception {
                //given
                final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                        .date(null)
                        .anotherUserId(1L)
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
            public void 실패_ID_Null() throws Exception {
                //given
                final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(null)
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
            public void 실패_자신플래너에좋아요취소() throws Exception {
                //given
                final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(1L)
                        .build();

                doThrow(new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER)).when(dailyPlannerServiceImpl).removeDailyLike(any(), any(RemoveDailyLikeRequest.class));

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
            public void 실패_유효하지않은플래너() throws Exception {
                //given
                final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(1L)
                        .build();

                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerServiceImpl).removeDailyLike(any(), any(RemoveDailyLikeRequest.class));

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
            public void 성공() throws Exception {
                //given
                final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                        .date("2023-09-28")
                        .anotherUserId(1L)
                        .build();

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
}
