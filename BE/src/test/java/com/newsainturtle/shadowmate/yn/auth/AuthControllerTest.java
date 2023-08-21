package com.newsainturtle.shadowmate.yn.auth;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.controller.AuthController;
import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
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

    @Test
    public void 실패_이메일중복() throws Exception {
        //given
        final String url = "/api/auth/email-certificated";
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
        final String url = "/api/auth/email-certificated";
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
        final String url = "/api/auth/email-certificated";
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
        final String url = "/api/auth/email-certificated";
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
        final String url = "/api/auth/email-certificated";
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