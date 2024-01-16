package com.newsainturtle.shadowmate.yn.planner_setting;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.planner_setting.controller.PlannerSettingController;
import com.newsainturtle.shadowmate.planner_setting.dto.request.*;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingServiceImpl;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PlannerSettingControllerTest {

    @InjectMocks
    private PlannerSettingController plannerSettingController;

    @Mock
    private PlannerSettingServiceImpl plannerSettingServiceImpl;

    @Mock
    private AuthService authServiceImpl;

    private MockMvc mockMvc;
    private Gson gson;
    final Long userId = 1L;


    @BeforeEach
    void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(plannerSettingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 카테고리설정 {
        @Nested
        class 카테고리등록 {
            final String url = "/api/planner-settings/{userId}/categories";
            final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                    .categoryTitle("국어")
                    .categoryEmoticon("🍅")
                    .categoryColorId(1L)
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addCategoryRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_없는카테고리색상() throws Exception {
                //given
                doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY_COLOR)).when(plannerSettingServiceImpl).addCategory(any(), any(AddCategoryRequest.class));

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
            void 성공_이모티콘Null() throws Exception {
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
            void 성공_이모티콘Null아님() throws Exception {
                //given

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
        class 카테고리수정 {
            final String url = "/api/planner-settings/{userId}/categories";

            @Test
            void 실패_없는사용자() throws Exception {
                //given
                final UpdateCategoryRequest updateCategoryRequest = UpdateCategoryRequest.builder()
                        .categoryId(1L)
                        .categoryTitle("국어")
                        .categoryEmoticon("🍅")
                        .categoryColorId(1L)
                        .build();
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateCategoryRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());

            }

            @Test
            void 실패_없는카테고리() throws Exception {
                //given
                final UpdateCategoryRequest updateCategoryRequest = UpdateCategoryRequest.builder()
                        .categoryId(1L)
                        .categoryTitle("국어")
                        .categoryEmoticon("🍅")
                        .categoryColorId(1L)
                        .build();
                doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY)).when(plannerSettingServiceImpl).updateCategory(any(), any(UpdateCategoryRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateCategoryRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_없는카테고리색상번호() throws Exception {
                //given
                final UpdateCategoryRequest updateCategoryRequest = UpdateCategoryRequest.builder()
                        .categoryId(1L)
                        .categoryTitle("국어")
                        .categoryEmoticon("🍅")
                        .categoryColorId(1L)
                        .build();
                doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY_COLOR)).when(plannerSettingServiceImpl).updateCategory(any(), any(UpdateCategoryRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateCategoryRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공_이모티콘Null() throws Exception {
                //given
                final UpdateCategoryRequest updateCategoryRequest = UpdateCategoryRequest.builder()
                        .categoryId(1L)
                        .categoryTitle("국어")
                        .categoryEmoticon(null)
                        .categoryColorId(1L)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateCategoryRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isAccepted());
            }

            @Test
            void 성공_이모티콘있음() throws Exception {
                //given
                final UpdateCategoryRequest updateCategoryRequest = UpdateCategoryRequest.builder()
                        .categoryId(1L)
                        .categoryTitle("국어")
                        .categoryEmoticon("🍅")
                        .categoryColorId(1L)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateCategoryRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isAccepted());
            }

        }

        @Nested
        class 카테고리삭제 {
            final String url = "/api/planner-settings/{userId}/categories";

            @Test
            void 실패_없는사용자() throws Exception {
                //given
                final RemoveCategoryRequest removeCategoryRequest = RemoveCategoryRequest.builder()
                        .categoryId(1L)
                        .build();
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeCategoryRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_없는카테고리() throws Exception {
                //given
                final RemoveCategoryRequest removeCategoryRequest = RemoveCategoryRequest.builder()
                        .categoryId(1L)
                        .build();
                doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY)).when(plannerSettingServiceImpl).removeCategory(any(), any(RemoveCategoryRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeCategoryRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_카테고리번호Null() throws Exception {
                //given
                final RemoveCategoryRequest removeCategoryRequest = RemoveCategoryRequest.builder()
                        .categoryId(null)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeCategoryRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 성공() throws Exception {
                //given
                final RemoveCategoryRequest removeCategoryRequest = RemoveCategoryRequest.builder()
                        .categoryId(1L)
                        .build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeCategoryRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isAccepted());
            }

        }

        @Nested
        class 플래너설정_조회 {
            @Test
            void 성공_카테고리색상목록조회() throws Exception {
                //given
                final String url = "/api/planner-settings/{userId}/categories/colors";

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk());
            }

            @Test
            void 실패_카테고리목록조회_사용자없음() throws Exception {
                //given
                final String url = "/api/planner-settings/{userId}/categories";
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 성공_카테고리목록조회() throws Exception {
                //given
                final String url = "/api/planner-settings/{userId}/categories";

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class 카테고리설정_실패케이스모음_유효하지않은요청값 {

        final String url = "/api/planner-settings/{userId}/categories";
        final String categoryEmoticon = "🍅";
        final long categoryColorId = 1L;
        final long categoryId = 1L;

        @ParameterizedTest
        @MethodSource("invalidAddCategoryRequest")
        void 카테고리등록_실패(final AddCategoryRequest addCategoryRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addCategoryRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidAddCategoryRequest() {
            return Stream.of(
                    // 타이틀 Null
                    Arguments.of(AddCategoryRequest.builder()
                            .categoryTitle(null)
                            .categoryEmoticon(categoryEmoticon)
                            .categoryColorId(categoryColorId)
                            .build()),
                    // 유효길이가 아닌 타이틀
                    Arguments.of(AddCategoryRequest.builder()
                            .categoryTitle("일이삼사오육칠팔구십일")
                            .categoryEmoticon(categoryEmoticon)
                            .categoryColorId(categoryColorId)
                            .build()),
                    // 유효길이가 아닌 이모티콘
                    Arguments.of(AddCategoryRequest.builder()
                            .categoryTitle(null)
                            .categoryEmoticon("🍅🍅")
                            .categoryColorId(categoryColorId)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidUpdateCategoryRequest")
        void 카테고리수정_실패(final UpdateCategoryRequest updateCategoryRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateCategoryRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidUpdateCategoryRequest() {
            return Stream.of(
                    // 카테고리 번호 Null
                    Arguments.of(UpdateCategoryRequest.builder()
                            .categoryId(null)
                            .categoryTitle("국어")
                            .categoryEmoticon(categoryEmoticon)
                            .categoryColorId(categoryColorId)
                            .build()),
                    // 유효하지 않은 카테고리 타이틀
                    Arguments.of(UpdateCategoryRequest.builder()
                            .categoryId(categoryId)
                            .categoryTitle("일이삼사오육칠팔구십일")
                            .categoryEmoticon(categoryEmoticon)
                            .categoryColorId(categoryColorId)
                            .build()),
                    // 카테고리 타이틀 Null
                    Arguments.of(UpdateCategoryRequest.builder()
                            .categoryId(categoryId)
                            .categoryTitle(null)
                            .categoryEmoticon(categoryEmoticon)
                            .categoryColorId(categoryColorId)
                            .build()),
                    // 카테고리 색상 번호 Null
                    Arguments.of(UpdateCategoryRequest.builder()
                            .categoryId(categoryId)
                            .categoryTitle("국어")
                            .categoryEmoticon(categoryEmoticon)
                            .categoryColorId(null)
                            .build())
            );
        }

    }

    @Nested
    class 플래너공개여부설정 {
        final String url = "/api/planner-settings/{userId}/access-scopes";
        final SetAccessScopeRequest setAccessScopeRequest = SetAccessScopeRequest.builder()
                .plannerAccessScope("비공개")
                .build();

        @Test
        void 실패_없는사용자() throws Exception {
            //given
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
        void 실패_잘못된범위값() throws Exception {
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
        void 성공_플래너공개여부설정() throws Exception {
            //given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(setAccessScopeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isAccepted());
        }
    }

    @Nested
    class 디데이설정 {
        final String url = "/api/planner-settings/{userId}/d-days";

        @Nested
        class 디데이등록 {
            final AddDdayRequest addDdayRequest = AddDdayRequest.builder()
                    .ddayTitle("생일")
                    .ddayDate("2023-01-27")
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDdayRequest))
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
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addDdayRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 디데이조회 {
            @Test
            void 실패_없는사용자() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk());
            }

        }

        @Nested
        class 디데이삭제 {
            final RemoveDdayRequest removeDdayRequest = RemoveDdayRequest.builder().ddayId(1L).build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given

                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDdayRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_디데이ID_Null() throws Exception {
                //given
                final RemoveDdayRequest removeDdayRequest = RemoveDdayRequest.builder().ddayId(null).build();

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(removeDdayRequest))
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
                                .content(gson.toJson(removeDdayRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isAccepted());
            }

        }

        @Nested
        class 디데이수정 {
            final UpdateDdayRequest updateDdayRequest = UpdateDdayRequest.builder()
                    .ddayId(1L)
                    .ddayTitle("생일")
                    .ddayDate("2023-01-27")
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateDdayRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());

            }

            @Test
            void 실패_유효하지않은디데이() throws Exception {
                //given
                doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_DDAY)).when(plannerSettingServiceImpl).updateDday(any(), any(UpdateDdayRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.put(url, userId)
                                .content(gson.toJson(updateDdayRequest))
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
                                .content(gson.toJson(updateDdayRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isAccepted());
            }

        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class 디데이설정_실패케이스모음_유효하지않은요청값 {

        final String url = "/api/planner-settings/{userId}/d-days";
        final String ddayDate = "2023-01-27";
        final String ddayTitle = "생일";

        @ParameterizedTest
        @MethodSource("invalidAddDdayRequest")
        void 디데이등록_실패(final AddDdayRequest addDdayRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url, userId)
                            .content(gson.toJson(addDdayRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidAddDdayRequest() {
            return Stream.of(
                    // 타이틀 Null
                    Arguments.of(AddDdayRequest.builder()
                            .ddayTitle(null)
                            .ddayDate(ddayDate)
                            .build()),
                    // 유효길이가 아닌 타이틀
                    Arguments.of(AddDdayRequest.builder()
                            .ddayTitle("12345678901234567890123456789012345678901")
                            .ddayDate(ddayDate)
                            .build()),
                    // 유효하지 않은 날짜
                    Arguments.of(AddDdayRequest.builder()
                            .ddayTitle(ddayTitle)
                            .ddayDate("2023-13-27")
                            .build()),
                    // 잘못된 날짜 포맷
                    Arguments.of(AddDdayRequest.builder()
                            .ddayTitle(ddayTitle)
                            .ddayDate("2023.01.27")
                            .build()),
                    // 날짜 Null
                    Arguments.of(AddDdayRequest.builder()
                            .ddayTitle(ddayTitle)
                            .ddayDate(null)
                            .build())
            );
        }

        @ParameterizedTest
        @MethodSource("invalidUpdateDdayRequest")
        void 디데이수정_실패(final UpdateDdayRequest updateDdayRequest) throws Exception {
            // given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateDdayRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        private Stream<Arguments> invalidUpdateDdayRequest() {
            return Stream.of(
                    // 디데이 ID Null
                    Arguments.of(UpdateDdayRequest.builder()
                            .ddayId(null)
                            .ddayTitle(ddayTitle)
                            .ddayDate(ddayDate)
                            .build()),
                    // 디데이 타이틀 Null
                    Arguments.of(UpdateDdayRequest.builder()
                            .ddayId(1L)
                            .ddayTitle(null)
                            .ddayDate("2023-01-27")
                            .build()),
                    // 유효길이가 아닌 타이틀
                    Arguments.of(UpdateDdayRequest.builder()
                            .ddayId(1L)
                            .ddayTitle("12345678901234567890123456789012345678901")
                            .ddayDate(ddayDate)
                            .build()),
                    // 날짜 Null
                    Arguments.of(UpdateDdayRequest.builder()
                            .ddayId(1L)
                            .ddayTitle(ddayTitle)
                            .ddayDate(null)
                            .build()),
                    // 유효하지 않은 날짜
                    Arguments.of(UpdateDdayRequest.builder()
                            .ddayId(1L)
                            .ddayTitle(ddayTitle)
                            .ddayDate("2023-13-27")
                            .build()),
                    // 잘못된 날짜 포맷
                    Arguments.of(UpdateDdayRequest.builder()
                            .ddayId(1L)
                            .ddayTitle(ddayTitle)
                            .ddayDate("2023.01.27")
                            .build())
            );
        }

    }

    @Nested
    class 루틴설정 {
        final String url = "/api/planner-settings/{userId}/routines";

        @Nested
        class 루틴등록 {
            final String startDay = "2023-12-25";
            final String endDay = "2024-01-09";
            final String routineContent = "아침운동하기";
            final String[] days = new String[]{"월", "수"};
            final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                    .routineContent(routineContent)
                    .startDay(startDay)
                    .endDay(endDay)
                    .categoryId(0L)
                    .days(Arrays.asList(days))
                    .build();

            @Test
            void 실패_없는사용자() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_루틴내용_null() throws Exception {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(null)
                        .startDay(startDay)
                        .endDay(endDay)
                        .categoryId(0L)
                        .days(Arrays.asList(days))
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_루틴내용_길이초과() throws Exception {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent("123456789012345678901234567890123456789012345678901")
                        .startDay(startDay)
                        .endDay(endDay)
                        .categoryId(0L)
                        .days(Arrays.asList(days))
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_잘못된_시작날짜_포맷() throws Exception {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routineContent)
                        .startDay("2024.01.15")
                        .endDay(endDay)
                        .categoryId(0L)
                        .days(Arrays.asList(days))
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_시작날짜_null() throws Exception {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routineContent)
                        .startDay(null)
                        .endDay(endDay)
                        .categoryId(0L)
                        .days(Arrays.asList(days))
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_잘못된_종료날짜_포맷() throws Exception {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routineContent)
                        .startDay(startDay)
                        .endDay("2024.01.15")
                        .categoryId(0L)
                        .days(Arrays.asList(days))
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_종료날짜_null() throws Exception {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routineContent)
                        .startDay(startDay)
                        .endDay(null)
                        .categoryId(0L)
                        .days(Arrays.asList(days))
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_요일리스트_null() throws Exception {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routineContent)
                        .startDay(startDay)
                        .endDay(endDay)
                        .categoryId(0L)
                        .days(null)
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_요일리스트_비어있음() throws Exception {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routineContent)
                        .startDay(startDay)
                        .endDay(endDay)
                        .categoryId(0L)
                        .days(new ArrayList<>())
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_카테고리ID_null() throws Exception {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routineContent)
                        .startDay(startDay)
                        .endDay(endDay)
                        .categoryId(null)
                        .days(Arrays.asList(days))
                        .build();
                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_시작날짜보다_과거인종료날짜() throws Exception {
                //given
                doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_DATE)).when(plannerSettingServiceImpl).addRoutine(any(), any(AddRoutineRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }

            @Test
            void 실패_유효하지않은요일이나중복요일() throws Exception {
                //given
                doThrow(new PlannerSettingException(PlannerSettingErrorResult.INVALID_ROUTINE_DAY)).when(plannerSettingServiceImpl).addRoutine(any(), any(AddRoutineRequest.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addRoutineRequest))
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
                                .content(gson.toJson(addRoutineRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 루틴조회 {

            @Test
            void 실패_없는사용자() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).certifyUser(any(Long.class), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 성공() throws Exception {
                //given

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }
    }

}
