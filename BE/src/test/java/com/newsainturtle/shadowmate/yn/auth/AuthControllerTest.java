package com.newsainturtle.shadowmate.yn.auth;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.controller.AuthController;
import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
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

    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 이메일인증 {
        final String url = "/api/auth/email-certificated";

        @Test
        public void 실패_이메일중복() throws Exception {
            //given
            final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder()
                    .email("test1234@naver.com")
                    .build();
            doThrow(new AuthException(AuthErrorResult.DUPLICATED_EMAIL)).when(authServiceImpl).certifyEmail(any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(certifyEmailRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        public void 실패_이메일Null() throws Exception {
            //given
            final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder()
                    .email(null)
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(certifyEmailRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        public void 실패_이메일형식아님() throws Exception {
            //given
            final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder()
                    .email("test1234@")
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(certifyEmailRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        public void 실패_이메일전송실패() throws Exception {
            //given
            final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder()
                    .email("test1234@test.com")
                    .build();
            doThrow(new AuthException(AuthErrorResult.FAIL_SEND_EMAIL)).when(authServiceImpl).certifyEmail(any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(certifyEmailRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isInternalServerError());
        }

        @Test
        public void 성공_이메일중복아님() throws Exception {
            //given
            final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder()
                    .email("test1234@naver.com")
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(certifyEmailRequest))
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
        public void 실패_잘못된이메일형식_null() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email(null)
                    .password("test1234")
                    .nickname("테스트중")
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
        public void 실패_잘못된이메일형식() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email("test1234")
                    .password("test1234")
                    .nickname("테스트중")
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
        public void 실패_잘못된비밀번호형식_null() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email("test1234@naver.com")
                    .password(null)
                    .nickname("테스트중")
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
        public void 실패_잘못된비밀번호형식_길이() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email("test1234@naver.com")
                    .password("t")
                    .nickname("테스트중")
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
        public void 실패_잘못된닉네임형식_null() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email("test1234@naver.com")
                    .password("test1234")
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
        public void 실패_잘못된닉네임형식_길이() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email("test1234@naver.com")
                    .password("test1234")
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
        public void 실패_중복이메일() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email("test1234@naver.com")
                    .password("test1234")
                    .nickname("테스트중")
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
        public void 성공_회원가입() throws Exception {
            //given
            final JoinRequest joinRequest = JoinRequest.builder()
                    .email("test1234@naver.com")
                    .password("test1234")
                    .nickname("테스트중")
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.post(url)
                            .content(gson.toJson(joinRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            //then
            resultActions.andExpect(status().isOk());
        }

    }
}