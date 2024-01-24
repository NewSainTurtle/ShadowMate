package com.newsainturtle.shadowmate.yn.social;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.social.controller.SocialController;
import com.newsainturtle.shadowmate.social.dto.response.SearchSocialPlannerResponse;
import com.newsainturtle.shadowmate.social.dto.response.SearchSocialResponse;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.exception.SocialErrorResult;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.social.service.SocialServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SocialControllerTest {

    @InjectMocks
    private SocialController socialController;

    @Mock
    private SocialServiceImpl socialService;

    @Mock
    private AuthServiceImpl authService;

    private MockMvc mockMvc;
    private Gson gson;
    private final String url = "/api/social/{userId}";
    private final String sort = "latest";
    private final int pageNumber = 1;
    private final String userNickname = "라미";
    private final Long userId = 1L;

    @BeforeEach
    void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(socialController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 일반_소셜조회 {
        @Test
        void 실패_없는사용자() throws Exception {
            //given
            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", sort)
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", "")
                            .param("start-date", "")
                            .param("end-date", "")
            );

            //then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        void 실패_정렬입력값이잘못됨() throws Exception {
            //given
            doThrow(new SocialException(SocialErrorResult.BAD_REQUEST_SORT)).when(socialService).getSocial(any(String.class), any(Integer.class), any(String.class), any(String.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", "late")
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", "")
                            .param("start-date", "")
                            .param("end-date", "")
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_날짜형식이잘못됨() throws Exception {
            //given
            doThrow(new SocialException(SocialErrorResult.INVALID_DATE_FORMAT)).when(socialService).getSocial(any(String.class), any(Integer.class), any(String.class), any(String.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", "late")
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", "")
                            .param("start-date", "2023.12.25")
                            .param("end-date", "2023.12.26")
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_기간이잘못됨() throws Exception {
            //given
            doThrow(new SocialException(SocialErrorResult.INVALID_DATE_PERIOD)).when(socialService).getSocial(any(String.class), any(Integer.class), any(String.class), any(String.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", "late")
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", "")
                            .param("start-date", "2023-12-25")
                            .param("end-date", "2023-12-24")
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공() throws Exception {
            // given
            List<Social> socialList = new ArrayList<>();
            SearchSocialPlannerResponse searchPublicDailyPlannerResponse = SearchSocialPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(socialList.size())
                    .sort(sort)
                    .socialList(socialList.stream()
                            .map(social -> SearchSocialResponse.builder()
                                    .socialId(social.getId())
                                    .socialImage(social.getSocialImage())
                                    .dailyPlannerDay(social.getDailyPlanner().getDailyPlannerDay())
                                    .userId(social.getDailyPlanner().getUser().getId())
                                    .statusMessage(social.getDailyPlanner().getUser().getStatusMessage())
                                    .nickname(social.getDailyPlanner().getUser().getNickname())
                                    .profileImage(social.getDailyPlanner().getUser().getProfileImage())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
            doReturn(searchPublicDailyPlannerResponse).when(socialService).getSocial(sort, pageNumber, "", "", "");

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", sort)
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", "")
                            .param("start-date", "")
                            .param("end-date", "")
            );

            // then
            resultActions.andExpect(status().isOk());
        }

        @Test
        void 성공_기간() throws Exception {
            // given
            List<Social> socialList = new ArrayList<>();
            SearchSocialPlannerResponse searchPublicDailyPlannerResponse = SearchSocialPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(socialList.size())
                    .sort(sort)
                    .socialList(socialList.stream()
                            .map(social -> SearchSocialResponse.builder()
                                    .socialId(social.getId())
                                    .socialImage(social.getSocialImage())
                                    .dailyPlannerDay(social.getDailyPlanner().getDailyPlannerDay())
                                    .userId(social.getDailyPlanner().getUser().getId())
                                    .statusMessage(social.getDailyPlanner().getUser().getStatusMessage())
                                    .nickname(social.getDailyPlanner().getUser().getNickname())
                                    .profileImage(social.getDailyPlanner().getUser().getProfileImage())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
            doReturn(searchPublicDailyPlannerResponse).when(socialService).getSocial(sort, pageNumber, "", "2023-12-25", "2023-12-26");

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", sort)
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", "")
                            .param("start-date", "2023-12-25")
                            .param("end-date", "2023-12-26")
            );

            // then
            resultActions.andExpect(status().isOk());
        }
    }

    @Nested
    class 닉네임검색_소셜조회 {
        @Test
        void 실패_없는사용자() throws Exception {
            //given
            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", sort)
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", userNickname)
                            .param("start-date", "")
                            .param("end-date", "")
            );

            //then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        void 실패_정렬입력값이잘못됨() throws Exception {
            //given
            doThrow(new SocialException(SocialErrorResult.BAD_REQUEST_SORT)).when(socialService).getSocial(any(String.class), any(Integer.class), any(String.class), any(String.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", "late")
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", userNickname)
                            .param("start-date", "")
                            .param("end-date", "")
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_날짜형식이잘못됨() throws Exception {
            //given
            doThrow(new SocialException(SocialErrorResult.INVALID_DATE_FORMAT)).when(socialService).getSocial(any(String.class), any(Integer.class), any(String.class), any(String.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", sort)
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", userNickname)
                            .param("start-date", "2023.12.25")
                            .param("end-date", "2023.12.26")
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 실패_기간이잘못됨() throws Exception {
            //given
            doThrow(new SocialException(SocialErrorResult.INVALID_DATE_PERIOD)).when(socialService).getSocial(any(String.class), any(Integer.class), any(String.class), any(String.class), any(String.class));

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", sort)
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", userNickname)
                            .param("start-date", "2023-12-25")
                            .param("end-date", "2023-12-24")
            );

            //then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void 성공() throws Exception {
            // given
            List<Social> socialList = new ArrayList<>();
            SearchSocialPlannerResponse searchPublicDailyPlannerResponse = SearchSocialPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(socialList.size())
                    .sort(sort)
                    .socialList(socialList.stream()
                            .map(social -> SearchSocialResponse.builder()
                                    .socialId(social.getId())
                                    .socialImage(social.getSocialImage())
                                    .dailyPlannerDay(social.getDailyPlanner().getDailyPlannerDay())
                                    .userId(social.getDailyPlanner().getUser().getId())
                                    .statusMessage(social.getDailyPlanner().getUser().getStatusMessage())
                                    .nickname(social.getDailyPlanner().getUser().getNickname())
                                    .profileImage(social.getDailyPlanner().getUser().getProfileImage())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
            doReturn(searchPublicDailyPlannerResponse).when(socialService).getSocial(sort, pageNumber, userNickname, "", "");

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", sort)
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", userNickname)
                            .param("start-date", "")
                            .param("end-date", "")
            );

            // then
            resultActions.andExpect(status().isOk());
        }

        @Test
        void 성공_기간() throws Exception {
            // given
            List<Social> socialList = new ArrayList<>();
            SearchSocialPlannerResponse searchPublicDailyPlannerResponse = SearchSocialPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(socialList.size())
                    .sort(sort)
                    .socialList(socialList.stream()
                            .map(social -> SearchSocialResponse.builder()
                                    .socialId(social.getId())
                                    .socialImage(social.getSocialImage())
                                    .dailyPlannerDay(social.getDailyPlanner().getDailyPlannerDay())
                                    .userId(social.getDailyPlanner().getUser().getId())
                                    .statusMessage(social.getDailyPlanner().getUser().getStatusMessage())
                                    .nickname(social.getDailyPlanner().getUser().getNickname())
                                    .profileImage(social.getDailyPlanner().getUser().getProfileImage())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
            doReturn(searchPublicDailyPlannerResponse).when(socialService).getSocial(sort, pageNumber, userNickname, "2023-12-25", "2023-12-26");

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("sort", sort)
                            .param("page-number", String.valueOf(pageNumber))
                            .param("nickname", userNickname)
                            .param("start-date", "2023-12-25")
                            .param("end-date", "2023-12-26")
            );

            // then
            resultActions.andExpect(status().isOk());
        }
    }

}
