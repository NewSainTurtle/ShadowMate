package com.newsainturtle.shadowmate.yn.planner;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.planner.controller.PlannerController;
import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoRequest;
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
    }
}
