package com.newsainturtle.shadowmate.kh.social;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.social.controller.SocialController;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.service.SocialServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SocialControllerTest{

    @InjectMocks
    private SocialController socialController;

    @Mock
    private SocialServiceImpl socialService;

    @Mock
    private AuthServiceImpl authService;

    private MockMvc mockMvc;

    private Gson gson;

    final Long userId = 9999L;

    final User user1 = User.builder()
            .id(9999L)
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    final String date = "2023-10-30";
    final String Image = "testImage";
    final DailyPlanner dailyPlanner = DailyPlanner.builder()
            .dailyPlannerDay(date)
            .user(user1)
            .build();
    final Social social = Social.builder()
            .id(9999L)
            .dailyPlanner(dailyPlanner)
            .socialImage(Image)
            .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
            .ownerId(user1.getId())
            .build();

    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(socialController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void 실패_공유된플래너삭제_유저정보다름() throws Exception {
        //given
        final String url = "/api/social/{userId}/{socialId}";
        doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, userId, social.getId())
        );

        //then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void 성공_공유된플래너삭제() throws Exception {
        // given
        final String url = "/api/social/{userId}/{socialId}";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url, userId, social.getId())
        );

        // then
        resultActions.andExpect(status().isAccepted());
    }
}
