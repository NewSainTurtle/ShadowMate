package com.newsainturtle.shadowmate.user;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.planner_setting.service.UserPlannerSettingService;
import com.newsainturtle.shadowmate.user.controller.UserController;
import com.newsainturtle.shadowmate.user.dto.request.UpdateIntroductionRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdatePasswordRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdateUserRequest;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.service.UserService;
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

import static com.newsainturtle.shadowmate.user.constant.UserConstant.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Mock
    private UserPlannerSettingService userPlannerSettingService;

    private MockMvc mockMvc;
    private Gson gson;
    private final Long userId = 1L;
    private final User user = User.builder()
            .id(1L)
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .withdrawal(false)
            .profileImage("TestProfileURL")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();

    @BeforeEach
    void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 프로필조회 {
        final String url = "/api/users/{userId}/profiles";

        @Test
        void 실패() throws Exception {
            // given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_PROFILE)).when(userService).getProfile(any());

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId));

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        void 성공() throws Exception {
            // given

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId));

            // then
            resultActions.andExpect(status().isOk());
        }
    }

    @Nested
    class 프로필수정 {
        final String url = "/api/users/{userId}/mypages";
        final String newProfileImage = "NewProfileImage";
        final String newStatusMessage = "NewStatusMessage";
        final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .newNickname("NewNickName")
                .newProfileImage(newProfileImage)
                .newStatusMessage(newStatusMessage)
                .build();

        @Test
        void 실패_내정보수정_유저틀림() throws Exception {
            //given
            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateUserRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            //then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        void 실패_내정보수정_닉네임NULL() throws Exception {
            //given
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateUserRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공_내정보수정() throws Exception {
            //given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateUserRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            //then
            resultActions.andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.message").value(SUCCESS_UPDATE_USER));
        }
    }

    @Nested
    class 비밀번호수정 {
        final String url = "/api/users/{userId}/password";
        final UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .oldPassword(user.getPassword())
                .newPassword("NewPassword")
                .build();

        @Test
        void 실패_비밀번호수정_유저틀림() throws Exception {
            //given
            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updatePasswordRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            //then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        void 성공_비밀번호수정() throws Exception {
            //given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updatePasswordRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            //then
            resultActions.andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.message").value(SUCCESS_UPDATE_PASSWORD));
        }

    }

    @Nested
    class 소개글 {
        final String url = "/api/users/{userId}/introduction";
        final String newIntroduction = "새로운소개글";
        final UpdateIntroductionRequest updateIntroductionRequest = UpdateIntroductionRequest.builder()
                .introduction(newIntroduction)
                .build();

        @Test
        void 성공_소개글조회() throws Exception {
            // given

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SUCCESS_SEARCH_INTRODUCTION));
        }

        @Test
        void 실패_소개글_유저틀림() throws Exception {
            // given
            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateIntroductionRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        void 실패_소개글100자초과() throws Exception {
            // given
            final String newIntroduction = "소개글백자가넘습니다소개글백자가넘습니다소개글백자가넘습니다소개글백자가넘습니다소개글백자가넘습니다소개글백자가넘습니다소개글백자가넘습니다소개글백자가넘습니다소개글백자가넘습니다소개글백자가넘습니다소개글백자가넘습니다";
            final UpdateIntroductionRequest updateIntroductionRequest = UpdateIntroductionRequest.builder()
                    .introduction(newIntroduction)
                    .build();

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateIntroductionRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공_소개글수정() throws Exception {
            // given

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url, userId)
                            .content(gson.toJson(updateIntroductionRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SUCCESS_UPDATE_INTRODUCTION));
        }
    }

    @Nested
    class 회원탈퇴 {
        final String url = "/api/users/{userId}";

        @Test
        void 실패_회원탈퇴_유저틀림() throws Exception {
            //given
            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, userId));

            //then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        void 성공_회원탈퇴() throws Exception {
            //given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.delete(url, userId));

            //then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SUCCESS_DELETE_USER));
        }

    }
}
