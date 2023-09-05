package com.newsainturtle.shadowmate.yn.planner_setting;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.planner_setting.controller.PlannerSettingController;
import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.SetAccessScopeRequest;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingServiceImpl;
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
public class PlannerSettingControllerTest {

    @InjectMocks
    private PlannerSettingController plannerSettingController;

    @Mock
    private PlannerSettingServiceImpl plannerSettingServiceImpl;

    @Mock
    private AuthService authServiceImpl;

    private MockMvc mockMvc;
    private Gson gson;


    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(plannerSettingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 카테고리등록 {
        final Long userId = 1L;
        final String url = "/api/planner-settings/{userId}/categories";

        @Test
        public void 실패_타이틀Null() throws Exception {
            //given
            final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                    .categoryTitle(null)
                    .categoryEmoticon("🍅")
                    .categoryColorId(1L)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addCategoryRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        public void 실패_유효길이가아닌타이틀() throws Exception {
            //given
            final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                    .categoryTitle("국")
                    .categoryEmoticon("🍅")
                    .categoryColorId(1L)
                    .build();
            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addCategoryRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        public void 실패_유효길이가아닌이모티콘() throws Exception {
            //given
            final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                    .categoryTitle(null)
                    .categoryEmoticon("🍅🍅")
                    .categoryColorId(1L)
                    .build();
            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addCategoryRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        public void 실패_없는사용자() throws Exception {
            //given
            final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                    .categoryTitle("국어")
                    .categoryEmoticon("🍅")
                    .categoryColorId(1L)
                    .build();
            doThrow(new PlannerSettingException(PlannerSettingErrorResult.UNREGISTERED_USER)).when(plannerSettingServiceImpl).addCategory(any(Long.class), any(AddCategoryRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addCategoryRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        public void 실패_없는카테고리색상() throws Exception {
            //given
            final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                    .categoryTitle("국어")
                    .categoryEmoticon("🍅")
                    .categoryColorId(1L)
                    .build();
            doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY_COLOR)).when(plannerSettingServiceImpl).addCategory(any(Long.class), any(AddCategoryRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addCategoryRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        public void 성공_이모티콘Null() throws Exception {
            //given
            final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                    .categoryTitle("국어")
                    .categoryEmoticon(null)
                    .categoryColorId(1L)
                    .build();
            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addCategoryRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isOk());
        }

        @Test
        public void 성공_이모티콘Null아님() throws Exception {
            //given
            final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                    .categoryTitle("국어")
                    .categoryEmoticon("🍅")
                    .categoryColorId(1L)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addCategoryRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isOk());
        }
    }

    @Nested
    class 플래너설정_조회 {
        @Test
        public void 성공_카테고리색상목록조회() throws Exception {
            //given
            final Long userId = 1L;
            final String url = "/api/planner-settings/{userId}/categories/colors";

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
            );

            //then
            resultActions.andExpect(status().isOk());
        }

        @Test
        public void 실패_카테고리목록조회_사용자없음() throws Exception {
            //given
            final Long userId = 1L;
            final String url = "/api/planner-settings/{userId}/categories";
            doThrow(new PlannerSettingException(PlannerSettingErrorResult.UNREGISTERED_USER)).when(plannerSettingServiceImpl).getCategoryList(any(Long.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        public void 성공_카테고리목록조회() throws Exception {
            //given
            final Long userId = 1L;
            final String url = "/api/planner-settings/{userId}/categories";

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
            );

            //then
            resultActions.andExpect(status().isOk());
        }
    }

    @Nested
    class 플래너공개여부설정 {
        final Long userId = 1L;
        final String url = "/api/planner-settings/{userId}/access-scopes";

        @Test
        public void 실패_없는사용자() throws Exception {
            //given
            final SetAccessScopeRequest setAccessScopeRequest = SetAccessScopeRequest.builder()
                    .plannerAccessScope("비공개")
                    .build();
            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(setAccessScopeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        public void 실패_잘못된범위값() throws Exception {
            //given
            final SetAccessScopeRequest setAccessScopeRequest = SetAccessScopeRequest.builder()
                    .plannerAccessScope("잘못된범위값")
                    .build();
            doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_PLANNER_ACCESS_SCOPE)).when(plannerSettingServiceImpl).setAccessScope(any(), any(SetAccessScopeRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(setAccessScopeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        public void 성공_플래너공개여부설정() throws Exception {
            //given
            final SetAccessScopeRequest setAccessScopeRequest = SetAccessScopeRequest.builder()
                    .plannerAccessScope("비공개")
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(setAccessScopeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isOk());
        }
    }
}
