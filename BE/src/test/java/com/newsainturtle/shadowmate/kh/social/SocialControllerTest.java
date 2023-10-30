package com.newsainturtle.shadowmate.kh.social;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.social.controller.SocialController;
import com.newsainturtle.shadowmate.social.dto.SearchPublicDailyPlannerResponse;
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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SocialControllerTest {

    @InjectMocks
    private SocialController socialController;

    @Mock
    private SocialServiceImpl socialService;

    @Mock
    private AuthServiceImpl authService;

    private MockMvc mockMvc;

    private Gson gson;

    final Long userId = 1L;

    final User user1 = User.builder()
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
            .dailyPlannerDay(Date.valueOf(date))
            .user(user1)
            .build();
    final Social social = Social.builder()
            .dailyPlanner(dailyPlanner)
            .socialImage(Image)
            .build();

    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(socialController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void 실패_유저정보다름() throws Exception {
        //given
        final String url = "/api/social/{userId}";
        final String sort = "latest";
        final Long pageNumber = 1L;
        doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url, userId)
                        .param("sort", sort)
                        .param("pageNumber", pageNumber.toString())
        );

        //then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void 성공_공개된플래너조회() throws Exception {
        // given
        final String url = "/api/social/{userId}";
        final String sort = "latest";
        final Long pageNumber = 1L;
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        SearchPublicDailyPlannerResponse searchPublicDailyPlannerResponse = SearchPublicDailyPlannerResponse.builder()
                .pageNumber(pageNumber)
                .totalPage(socialList.size())
                .sort(sort)
                .socialList(socialList)
                .build();
        doReturn(searchPublicDailyPlannerResponse).when(socialService).searchPublicDailyPlanner(sort, pageNumber);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url, userId)
                        .param("sort", sort)
                        .param("pageNumber", pageNumber.toString())
        );

        // then
        resultActions.andExpect(status().isOk());
    }
}
