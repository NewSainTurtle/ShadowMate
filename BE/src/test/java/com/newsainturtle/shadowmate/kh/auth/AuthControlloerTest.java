package com.newsainturtle.shadowmate.kh.auth;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.controller.AuthController;
import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControlloerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthServiceImpl authServiceImpl;

    @Mock
    private UserRepository userRepository;


    private MockMvc mockMvc;
    private Gson gson;

    final User user = User.builder()
            .email("test1234@naver.com")
            .password("12345")
            .socialLogin(false)
            .nickname("거북이")
            .withdrawal(false)
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();

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

        final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder().email("test1234@naver.com").build();
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

        final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder().email(null).build();

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
    public void 실패_이메일형식이아님() throws Exception {
        //given
        final String url = "/api/auth/email-certificated";

        final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder().email("Hi").build();

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
    public void 성공_이메일중복아님() throws Exception {
        //given
        final String url = "/api/auth/email-certificated";

        final CertifyEmailRequest certifyEmailRequest = CertifyEmailRequest.builder().email("test1234@naver.com").build();

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(certifyEmailRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void 성공_회원가입() throws Exception {
        //given
        final String url = "/api/auth/join";
        final JoinRequest joinRequest =
                JoinRequest.builder()
                        .email("test@test.com")
                        .password("123456")
                        .nickname("닉네임")
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
