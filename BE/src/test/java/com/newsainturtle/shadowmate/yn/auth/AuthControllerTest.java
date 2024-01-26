package com.newsainturtle.shadowmate.yn.auth;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.controller.AuthController;
import com.newsainturtle.shadowmate.auth.dto.RemoveTokenRequest;
import com.newsainturtle.shadowmate.auth.dto.SendEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.dto.CheckEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
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
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthServiceImpl authServiceImpl;

    private MockMvc mockMvc;
    private Gson gson;
    private final String email = "yntest@shadowmate.com";
    private final String password = "yntest1234";
    private final String nickname = "거북이";

    @BeforeEach
    void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 이메일인증 {
        final String url = "/api/auth/email-authentication";

        @Test
        void 실패_이메일중복() throws Exception {
            //given
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .build();
            doThrow(new AuthException(AuthErrorResult.DUPLICATED_EMAIL)).when(authServiceImpl).sendEmailAuthenticationCode(any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(sendEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_이메일Null() throws Exception {
            //given
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                    .email(null)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(sendEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_이메일형식아님() throws Exception {
            //given
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                    .email("test1234@")
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(sendEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_이미인증된이메일() throws Exception {
            //given
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .build();
            doThrow(new AuthException(AuthErrorResult.ALREADY_AUTHENTICATED_EMAIL)).when(authServiceImpl).sendEmailAuthenticationCode(any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(sendEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_이메일전송실패() throws Exception {
            //given
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .build();
            doThrow(new AuthException(AuthErrorResult.FAIL_SEND_EMAIL)).when(authServiceImpl).sendEmailAuthenticationCode(any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(sendEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isInternalServerError());
        }

        @Test
        void 성공_이메일중복아님() throws Exception {
            //given
            final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest = SendEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(sendEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isOk());
        }
    }

    @Nested
    class 이메일인증코드확인 {
        final String url = "/api/auth/email-authentication/check";
        final String code = "code127";

        @Test
        void 실패_이메일중복() throws Exception {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code(code)
                    .build();
            doThrow(new AuthException(AuthErrorResult.DUPLICATED_EMAIL)).when(authServiceImpl).checkEmailAuthenticationCode(any(CheckEmailAuthenticationCodeRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(checkEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_이메일Null() throws Exception {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(null)
                    .code(code)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(checkEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_코드Null() throws Exception {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code(null)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(checkEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_이메일형식아님() throws Exception {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email("test1234@")
                    .code(code)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(checkEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_이메일인증_유효시간지남() throws Exception {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code(code)
                    .build();
            doThrow(new AuthException(AuthErrorResult.EMAIL_AUTHENTICATION_TIME_OUT)).when(authServiceImpl).checkEmailAuthenticationCode(any(CheckEmailAuthenticationCodeRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(checkEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_이미인증된이메일사용() throws Exception {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code(code)
                    .build();
            doThrow(new AuthException(AuthErrorResult.ALREADY_AUTHENTICATED_EMAIL)).when(authServiceImpl).checkEmailAuthenticationCode(any(CheckEmailAuthenticationCodeRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(checkEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_인증코드틀림() throws Exception {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code(code)
                    .build();
            doThrow(new AuthException(AuthErrorResult.INVALID_EMAIL_AUTHENTICATION_CODE)).when(authServiceImpl).checkEmailAuthenticationCode(any(CheckEmailAuthenticationCodeRequest.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(checkEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공_이메일인증코드확인() throws Exception {
            //given
            final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest = CheckEmailAuthenticationCodeRequest.builder()
                    .email(email)
                    .code(code)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(checkEmailAuthenticationCodeRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isOk());
        }
    }

    @Nested
    class 회원가입 {
        final String url = "/api/auth/join";

        @Test
        void 실패_잘못된이메일형식_null() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(null)
                    .password(password)
                    .nickname(nickname)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());

        }

        @Test
        void 실패_잘못된이메일형식() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email("test1234")
                    .password(password)
                    .nickname(nickname)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_잘못된비밀번호형식_null() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(email)
                    .password(null)
                    .nickname(nickname)
                    .build();
            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_잘못된비밀번호형식_길이() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(email)
                    .password("t")
                    .nickname(nickname)
                    .build();
            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_잘못된닉네임형식_null() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(email)
                    .password(password)
                    .nickname(null)
                    .build();
            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_잘못된닉네임형식_길이() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(email)
                    .password(password)
                    .nickname("테")
                    .build();
            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_중복이메일() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .build();
            doThrow(new AuthException(AuthErrorResult.DUPLICATED_EMAIL)).when(authServiceImpl).join(any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_임시이메일_타임아웃() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .build();
            doThrow(new AuthException(AuthErrorResult.EMAIL_AUTHENTICATION_TIME_OUT)).when(authServiceImpl).join(any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_임시이메일_인증안됨() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .build();
            doThrow(new AuthException(AuthErrorResult.UNAUTHENTICATED_EMAIL)).when(authServiceImpl).join(any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공_회원가입() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isAccepted());
        }

    }

    @Nested
    class 자동로그인 {
        final String url = "/api/auth/auto-login";

        @Test
        void 실패_유저없음() throws Exception {
            //given
            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authServiceImpl).checkAutoLogin(any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Auto-Login", "key")
            );

            //then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        void 성공() throws Exception {
            //given

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Auto-Login", "key")
            );

            //then
            resultActions.andExpect(status().isOk());
        }

    }

    @Nested
    class 로그아웃 {
        final String url = "/api/auth/logout";
        final String type = "0123456789 1";
        final long userId = 1L;

        @Test
        void 실패_유저ID_null() throws Exception {
            //given
            final RemoveTokenRequest removeTokenRequest = RemoveTokenRequest.builder()
                    .userId(null)
                    .type(type)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Auto-Login", "key")
                            .content(gson.toJson(removeTokenRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());

        }

        @Test
        void 실패_type_null() throws Exception {
            //given
            final RemoveTokenRequest removeTokenRequest = RemoveTokenRequest.builder()
                    .userId(userId)
                    .type(null)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Auto-Login", "key")
                            .content(gson.toJson(removeTokenRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());

        }

        @Test
        void 성공() throws Exception {
            //given
            final RemoveTokenRequest removeTokenRequest = RemoveTokenRequest.builder()
                    .userId(userId)
                    .type(type)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .header("Auto-Login", "key")
                            .content(gson.toJson(removeTokenRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isOk());
        }

    }
}