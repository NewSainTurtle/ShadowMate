package com.newsainturtle.shadowmate.kh.user;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.user.controller.UserController;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import com.newsainturtle.shadowmate.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    private Gson gson;

    final User user = User.builder()
            .email("aa@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("닉네임임")
            .withdrawal(false)
            .profileImage("TestProfileURL")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();

    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 프로필TEST {
        final Long userId = 1L;
        final String url = "/api/users/{userId}/profiles";

        @Test
        void 실패_프로필조회() throws Exception {
            // given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_PROFILE)).when(userService).getProfile(any());

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId));

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        void 성공_프로필조회() throws Exception {
            // given

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url,userId));

            // then
            resultActions.andExpect(status().isOk());
        }
    }
}
