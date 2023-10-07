package com.newsainturtle.shadowmate.kh.follow;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.follow.controller.FollowController;
import com.newsainturtle.shadowmate.follow.dto.FollowingResponse;
import com.newsainturtle.shadowmate.follow.exception.FollowErrorResult;
import com.newsainturtle.shadowmate.follow.exception.FollowException;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.service.FollowServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.assertj.core.api.Assertions;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class FollowControllerTest {

    @InjectMocks
    private FollowController followController;

    @Mock
    private FollowServiceImpl followService;

    @Mock
    private AuthServiceImpl authService;

    private MockMvc mockMvc;

    private Gson gson;

    final User user1 = User.builder()
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    final User user2 = User.builder()
            .email("test2@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이2")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(followController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class 팔로잉TEST {

        @Nested
        class 팔로잉조회TEST {
            final String url = "/api/follow/{userId}/following";
            final List<FollowingResponse> list = new ArrayList<>();
            final Long userId = 1L;

            @Test
            public void 실패_유저정보다름() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }


            @Test
            public void 실패_팔로잉조회Null() throws Exception {
                //given
                final List<FollowingResponse> followingResponses = new ArrayList<>();
                doReturn(followingResponses).when(followService).getFollowing(any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").isEmpty());

            }


            @Test
            public void 성공_팔로잉조회() throws Exception {
                //given
                list.add(FollowingResponse.builder()
                        .followId(1L)
                        .email(user2.getEmail())
                        .nickname(user2.getNickname())
                        .profileImage(user2.getProfileImage())
                        .followingId(user2.getId())
                        .build());
                doReturn(list).when(followService).getFollowing(any());

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
