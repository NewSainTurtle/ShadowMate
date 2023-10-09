package com.newsainturtle.shadowmate.yn.planner;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.planner.controller.PlannerController;
import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.UpdateRetrospectionRequest;
import com.newsainturtle.shadowmate.planner.dto.UpdateTodayGoalRequest;
import com.newsainturtle.shadowmate.planner.dto.UpdateTomorrowGoalRequest;
import com.newsainturtle.shadowmate.planner.dto.*;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.WeeklyPlannerServiceImpl;
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
    private WeeklyPlannerServiceImpl weeklyPlannerServiceImpl;

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
        class 할일수정 {
            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();

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
            public void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023.09.25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();

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
            public void 실패_할일내용_길이초과() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("012345678901234567890123456789012345678901234567891")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();

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
            public void 실패_날짜_Null() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date(null)
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();

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
            public void 실패_할일ID_Null() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(null)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();

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
            public void 실패_할일내용_Null() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent(null)
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();

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
            public void 실패_카테고리ID_Null() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(null)
                        .todoStatus("완료")
                        .build();

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
            public void 실패_할일상태_Null() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus(null)
                        .build();

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
            public void 실패_할일상태_길이초과() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("????")
                        .build();

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
            public void 실패_유효하지않은할일상태값() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();
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
            public void 실패_유효하지않은일일플래너() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();
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
            public void 실패_유효하지않은카테고리() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();
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
            public void 실패_유효하지않은할일() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();
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
            public void 성공() throws Exception {
                //given
                final UpdateDailyTodoRequest updateDailyTodoRequest = UpdateDailyTodoRequest.builder()
                        .date("2023-09-25")
                        .todoId(1L)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .categoryId(1L)
                        .todoStatus("완료")
                        .build();

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

        @Nested
        class 오늘의회고편집 {
            final String url = "/api/planners/{userId}/daily/retrospections";

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final UpdateRetrospectionRequest updateRetrospectionRequest = UpdateRetrospectionRequest.builder()
                        .date("2023-09-26")
                        .retrospection("오늘 계획했던 일을 모두 끝냈다!!! 신남~~")
                        .build();
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
            public void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final UpdateRetrospectionRequest updateRetrospectionRequest = UpdateRetrospectionRequest.builder()
                        .date("2023.09.26")
                        .retrospection("오늘 계획했던 일을 모두 끝냈다!!! 신남~~")
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 실패_오늘의회고_길이초과() throws Exception {
                //given
                final UpdateRetrospectionRequest updateRetrospectionRequest = UpdateRetrospectionRequest.builder()
                        .date("2023-09-26")
                        .retrospection("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 실패_날짜Null() throws Exception {
                //given
                final UpdateRetrospectionRequest updateRetrospectionRequest = UpdateRetrospectionRequest.builder()
                        .date(null)
                        .retrospection("오늘 계획했던 일을 모두 끝냈다!!! 신남~~")
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 실패_오늘의회고Null() throws Exception {
                //given
                final UpdateRetrospectionRequest updateRetrospectionRequest = UpdateRetrospectionRequest.builder()
                        .date("2023-09-26")
                        .retrospection(null)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 성공() throws Exception {
                //given
                final UpdateRetrospectionRequest updateRetrospectionRequest = UpdateRetrospectionRequest.builder()
                        .date("2023-09-26")
                        .retrospection("오늘 계획했던 일을 모두 끝냈다!!! 신남~~")
                        .build();

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

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                        .date("2023-09-26")
                        .retrospectionImage(imageUrl)
                        .build();
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
            public void 실패_올바르지않은날짜형식() throws Exception {
                //given
                final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                        .date("2023.09.26")
                        .retrospectionImage(imageUrl)
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionImageRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 실패_날짜Null() throws Exception {
                //given
                final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                        .date(null)
                        .retrospectionImage(imageUrl)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateRetrospectionImageRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            public void 성공_오늘의회고사진Null() throws Exception {
                //given
                final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                        .date("2023-09-26")
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
            public void 성공() throws Exception {
                //given
                final UpdateRetrospectionImageRequest updateRetrospectionImageRequest = UpdateRetrospectionImageRequest.builder()
                        .date("2023-09-26")
                        .retrospectionImage(imageUrl)
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

    @Nested
    class 주간플래너 {
        final String url = "/api/planners/{userId}/weekly/todos";

        @Nested
        class 주차별할일등록 {
            private final String startDay = "2023-10-09";
            private final String endDay = "2023-10-15";
            private final String weeklyTodoContent = "자기소개서 제출하기";

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .build();

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
            public void 실패_올바르지않은시작날짜형식() throws Exception {
                //given
                final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                        .startDate("2023.10.09")
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .build();


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
            public void 실패_올바르지않은끝날짜형식() throws Exception {
                //given
                final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                        .startDate(startDay)
                        .endDate("2023.10.15")
                        .weeklyTodoContent(weeklyTodoContent)
                        .build();


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
            public void 실패_할일내용_길이초과() throws Exception {
                //given
                final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoContent("012345678901234567890123456789012345678901234567891")
                        .build();


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
            public void 실패_시작날짜Null() throws Exception {
                //given
                final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                        .startDate(null)
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .build();


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
            public void 실패_끝날짜Null() throws Exception {
                //given
                final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                        .startDate(startDay)
                        .endDate(null)
                        .weeklyTodoContent(weeklyTodoContent)
                        .build();


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
            public void 실패_할일내용Null() throws Exception {
                //given
                final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoContent(null)
                        .build();


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
            public void 실패_올바르지않은날짜_시작요일이월요일이아님() throws Exception {
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
            public void 실패_올바르지않은날짜_일주일간격이아님() throws Exception {
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
            public void 성공() throws Exception {
                //given
                final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .build();


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

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .weeklyTodoId(weeklyTodoId)
                        .build();

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
            public void 실패_올바르지않은시작날짜형식() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate("2023.10.09")
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .weeklyTodoId(weeklyTodoId)
                        .build();

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
            public void 실패_올바르지않은끝날짜형식() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate(startDay)
                        .endDate("2023.10.15")
                        .weeklyTodoContent(weeklyTodoContent)
                        .weeklyTodoId(weeklyTodoId)
                        .build();

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
            public void 실패_할일내용_길이초과() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoContent("012345678901234567890123456789012345678901234567891")
                        .weeklyTodoId(weeklyTodoId)
                        .build();

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
            public void 실패_시작날짜Null() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate(null)
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .weeklyTodoId(weeklyTodoId)
                        .build();

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
            public void 실패_끝날짜Null() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate(startDay)
                        .endDate(null)
                        .weeklyTodoContent(weeklyTodoContent)
                        .weeklyTodoId(weeklyTodoId)
                        .build();

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
            public void 실패_할일내용Null() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoContent(null)
                        .weeklyTodoId(weeklyTodoId)
                        .build();

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
            public void 실패_할일ID_Null() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .weeklyTodoId(null)
                        .build();

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
            public void 실패_올바르지않은날짜_시작요일이월요일이아님() throws Exception {
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
            public void 실패_올바르지않은날짜_일주일간격이아님() throws Exception {
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
            public void 실패_유효하지않은위클리할일() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .weeklyTodoId(weeklyTodoId)
                        .build();

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
            public void 성공() throws Exception {
                //given
                final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoContent(weeklyTodoContent)
                        .weeklyTodoId(weeklyTodoId)
                        .build();

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

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoId(weeklyTodoId)
                        .weeklyTodoStatus(true)
                        .build();

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
            public void 실패_올바르지않은시작날짜형식() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate("2023.10.09")
                        .endDate(endDay)
                        .weeklyTodoId(weeklyTodoId)
                        .weeklyTodoStatus(true)
                        .build();

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
            public void 실패_올바르지않은끝날짜형식() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate(startDay)
                        .endDate("2023.10.15")
                        .weeklyTodoId(weeklyTodoId)
                        .weeklyTodoStatus(true)
                        .build();

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
            public void 실패_시작날짜Null() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate(null)
                        .endDate(endDay)
                        .weeklyTodoId(weeklyTodoId)
                        .weeklyTodoStatus(true)
                        .build();

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
            public void 실패_끝날짜Null() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate(startDay)
                        .endDate(null)
                        .weeklyTodoId(weeklyTodoId)
                        .weeklyTodoStatus(true)
                        .build();

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
            public void 실패_할일ID_Null() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoId(null)
                        .weeklyTodoStatus(true)
                        .build();

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
            public void 실패_할일상태Null() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoId(weeklyTodoId)
                        .weeklyTodoStatus(null)
                        .build();

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
            public void 실패_올바르지않은날짜_시작요일이월요일이아님() throws Exception {
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
            public void 실패_올바르지않은날짜_일주일간격이아님() throws Exception {
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
            public void 실패_유효하지않은위클리할일() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoId(weeklyTodoId)
                        .weeklyTodoStatus(true)
                        .build();

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
            public void 성공() throws Exception {
                //given
                final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                        .startDate(startDay)
                        .endDate(endDay)
                        .weeklyTodoId(weeklyTodoId)
                        .weeklyTodoStatus(true)
                        .build();

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
    }

    @Nested
    class 타임테이블 {
        final String url = "/api/planners/{userId}/daily/timetables";
        final String date = "2023-10-06";
        final String startTime = "2023-10-06 23:50";
        final String endTime = "2023-10-07 01:30";

        @Nested
        class 타임테이블등록 {

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(1L)
                        .build();
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
            public void 실패_올바르지않은날짜형식_date() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date("2023.10.06")
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(1L)
                        .build();

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
            public void 실패_올바르지않은날짜형식_startTime() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime("2023-10-06 23:59")
                        .endTime(endTime)
                        .todoId(1L)
                        .build();

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
            public void 실패_올바르지않은날짜형식_endTime() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime("2023-10-0623:50")
                        .todoId(1L)
                        .build();

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
            public void 실패_date_null() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(null)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(1L)
                        .build();

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
            public void 실패_startTime_null() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(null)
                        .endTime(endTime)
                        .todoId(1L)
                        .build();

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
            public void 실패_endTime_null() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(null)
                        .todoId(1L)
                        .build();

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
            public void 실패_todoId_null() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(null)
                        .build();

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
            public void 실패_잘못된시간값() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(1L)
                        .build();
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
            public void 실패_유효하지않은플래너() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(1L)
                        .build();
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
            public void 실패_유효하지않은할일() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(1L)
                        .build();
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
            public void 실패_이미타임테이블시간이존재() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(1L)
                        .build();
                doThrow(new PlannerException(PlannerErrorResult.ALREADY_ADDED_TIME_TABLE)).when(dailyPlannerServiceImpl).addTimeTable(any(), any(AddTimeTableRequest.class));

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
            public void 성공() throws Exception {
                //given
                final AddTimeTableRequest addTimeTableRequest = AddTimeTableRequest.builder()
                        .date(date)
                        .startTime(startTime)
                        .endTime(endTime)
                        .todoId(1L)
                        .build();

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

            @Test
            public void 실패_없는사용자() throws Exception {
                //given
                final RemoveTimeTableRequest removeTimeTableRequest = RemoveTimeTableRequest.builder()
                        .date(date)
                        .timeTableId(1L)
                        .build();
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
            public void 실패_올바르지않은날짜형식_date() throws Exception {
                //given
                final RemoveTimeTableRequest removeTimeTableRequest = RemoveTimeTableRequest.builder()
                        .date("2023.10.06")
                        .timeTableId(1L)
                        .build();
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
            public void 실패_date_null() throws Exception {
                //given
                final RemoveTimeTableRequest removeTimeTableRequest = RemoveTimeTableRequest.builder()
                        .date(null)
                        .timeTableId(1L)
                        .build();

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
            public void 실패_timeTableId_null() throws Exception {
                //given
                final RemoveTimeTableRequest removeTimeTableRequest = RemoveTimeTableRequest.builder()
                        .date(date)
                        .timeTableId(null)
                        .build();

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
            public void 실패_유효하지않은타임테이블() throws Exception {
                //given
                final RemoveTimeTableRequest removeTimeTableRequest = RemoveTimeTableRequest.builder()
                        .date(date)
                        .timeTableId(1L)
                        .build();
                doThrow(new PlannerException(PlannerErrorResult.INVALID_TIME_TABLE)).when(dailyPlannerServiceImpl).removeTimeTable(any(), any(RemoveTimeTableRequest.class));

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
            public void 실패_유효하지않은플래너() throws Exception {
                //given
                final RemoveTimeTableRequest removeTimeTableRequest = RemoveTimeTableRequest.builder()
                        .date(date)
                        .timeTableId(1L)
                        .build();
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
            public void 성공() throws Exception {
                //given
                final RemoveTimeTableRequest removeTimeTableRequest = RemoveTimeTableRequest.builder()
                        .date(date)
                        .timeTableId(1L)
                        .build();

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
}