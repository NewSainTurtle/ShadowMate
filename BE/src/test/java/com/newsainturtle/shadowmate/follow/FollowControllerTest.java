package com.newsainturtle.shadowmate.follow;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.follow.constant.FollowConstant;
import com.newsainturtle.shadowmate.follow.controller.FollowController;
import com.newsainturtle.shadowmate.follow.dto.request.*;
import com.newsainturtle.shadowmate.follow.dto.response.*;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.follow.exception.FollowErrorResult;
import com.newsainturtle.shadowmate.follow.exception.FollowException;
import com.newsainturtle.shadowmate.follow.service.FollowService;
import com.newsainturtle.shadowmate.follow.service.UserFollowService;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest {

    @InjectMocks
    private FollowController followController;

    @Mock
    private FollowService followService;

    @Mock
    private UserFollowService userFollowService;

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private Gson gson;
    private final Long userId = 1L;
    private final User user1 = User.builder()
            .id(1L)
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .email("test2@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이2")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @BeforeEach
    void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(followController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void 성공_팔로우개수조회() throws Exception {
        // given
        final String url = "/api/follow/{userId}/counts";
        final CountFollowResponse countFollowResponse = CountFollowResponse.builder()
                .followerCount(1L)
                .followingCount(10L)
                .build();
        doReturn(countFollowResponse).when(userFollowService).countFollow(any());

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url, userId)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FollowConstant.SUCCESS_FOLLOW_COUNT))
                .andExpect(jsonPath("$.data.followerCount").value(1L))
                .andExpect(jsonPath("$.data.followingCount").value(10L));
    }

    @Nested
    class 팔로잉 {
        final String url = "/api/follow/{userId}/following";

        @Nested
        class 팔로잉조회 {

            @Test
            void 실패_유저정보다름() throws Exception {
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
            void 성공_팔로잉조회Null() throws Exception {
                //given
                doReturn(new ArrayList<>()).when(followService).getFollowing(any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").isEmpty());

            }

            @Test
            void 성공() throws Exception {
                //given
                final List<FollowingResponse> list = new ArrayList<>();
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

        @Nested
        class 팔로잉삭제 {
            final DeleteFollowingRequest deleteFollowingRequest = DeleteFollowingRequest.builder().followingId(1L).build();

            @Test
            void 실패_팔로잉삭제유저아이디다름() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(deleteFollowingRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 실패_팔로잉삭제유저없음() throws Exception {
                //given
                doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userFollowService).deleteFollowing(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(deleteFollowingRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isNotFound());
            }

            @Test
            void 성공() throws Exception {
                // given

                // when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(deleteFollowingRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                // then
                resultActions.andExpect(status().isOk());
            }
        }
    }

    @Nested
    class 팔로워 {
        final String url = "/api/follow/{userId}/followers";

        @Nested
        class 팔로잉조회 {

            @Test
            void 실패_팔로워조회Null() throws Exception {
                //given
                doReturn(new ArrayList<>()).when(followService).getFollower(any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data").isEmpty());

            }

            @Test
            void 성공_팔로워조회() throws Exception {
                //given
                final List<FollowerResponse> list = new ArrayList<>();
                list.add(FollowerResponse.builder()
                        .followId(1L)
                        .email(user1.getEmail())
                        .nickname(user1.getNickname())
                        .profileImage(user1.getProfileImage())
                        .followerId(user1.getId())
                        .build());
                doReturn(list).when(followService).getFollower(any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 팔로잉삭제 {
            final DeleteFollowerRequest deleteFollowerRequest = DeleteFollowerRequest.builder().followerId(1L).build();

            @Test
            void 실패_팔로워삭제유저없음() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(deleteFollowerRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 성공() throws Exception {
                // given

                // when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(deleteFollowerRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                // then
                resultActions.andExpect(status().isOk());
            }
        }
    }

    @Nested
    class 팔로우 {
        final String url = "/api/follow/{userId}/requested";

        @Nested
        class 팔로우신청 {
            final AddFollowRequest addFollowRequest = AddFollowRequest
                    .builder()
                    .followingId(userId)
                    .build();

            @Test
            void 실패_중복신청() throws Exception {
                //given
                doThrow(new FollowException(FollowErrorResult.DUPLICATED_FOLLOW)).when(userFollowService).addFollow(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addFollowRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isBadRequest());
            }


            @Test
            void 실패_유저없음() throws Exception {
                //given
                doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userFollowService).addFollow(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addFollowRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isNotFound());
            }


            @Test
            void 성공_비공개() throws Exception {
                //given
                final AddFollowResponse addFollowResponse = AddFollowResponse
                        .builder()
                        .followId(1L)
                        .plannerAccessScope(PlannerAccessScope.PRIVATE)
                        .build();
                doReturn(addFollowResponse).when(userFollowService).addFollow(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addFollowRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.plannerAccessScope")
                                .value(equalTo(addFollowResponse.getPlannerAccessScope().toString())));

            }


            @Test
            void 성공_전체공개() throws Exception {
                final AddFollowRequest addFollowRequest = AddFollowRequest
                        .builder()
                        .followingId(userId)
                        .build();
                //given
                final AddFollowResponse addFollowResponse = AddFollowResponse
                        .builder()
                        .followId(1L)
                        .plannerAccessScope(PlannerAccessScope.PUBLIC)
                        .build();
                doReturn(addFollowResponse).when(userFollowService).addFollow(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(addFollowRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );
                //then
                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.plannerAccessScope")
                                .value(equalTo(addFollowResponse.getPlannerAccessScope().toString())));
            }
        }

        @Nested
        class 팔로우신청취소 {
            final DeleteFollowRequestRequest deleteFollowRequestRequest = DeleteFollowRequestRequest.builder().receiverId(1L).build();

            @Test
            void 실패_유저없음() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(deleteFollowRequestRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());
            }

            @Test
            void 성공() throws Exception {
                // given

                // when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.delete(url, userId)
                                .content(gson.toJson(deleteFollowRequestRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                // then
                resultActions.andExpect(status().isOk());
            }
        }

        @Nested
        class 팔로우신청응답 {
            final String url = "/api/follow/{userId}/receive";
            final ReceiveFollowRequest receiveFollowRequest = ReceiveFollowRequest.builder()
                    .requesterId(1L)
                    .followReceive(false)
                    .build();
            final Long userId = 2L;

            @Test
            void 실패_유저인증실패() throws Exception {
                //given
                doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(receiveFollowRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isForbidden());

            }

            @Test
            void 실패_유저없음() throws Exception {
                //given
                doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userFollowService).receiveFollow(any(), any(), any(boolean.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(receiveFollowRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isNotFound());

            }

            @Test
            void 실패_팔로우신청존재하지않음() throws Exception {
                //given
                doThrow(new FollowException(FollowErrorResult.NOT_FOUND_FOLLOW_REQUEST)).when(userFollowService).receiveFollow(any(), any(), any(boolean.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(receiveFollowRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isNotFound());

            }

            @Test
            void 성공_팔로우신청거절() throws Exception {
                //given
                doReturn(FollowConstant.SUCCESS_FOLLOW_RECEIVE_FALSE).when(userFollowService).receiveFollow(any(),
                        any(),
                        any(boolean.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(receiveFollowRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value(FollowConstant.SUCCESS_FOLLOW_RECEIVE_FALSE));

            }

            @Test
            void 성공_팔로우신청수락() throws Exception {
                //given
                doReturn(FollowConstant.SUCCESS_FOLLOW_RECEIVE_TRUE).when(userFollowService).receiveFollow(any(),
                        any(),
                        any(boolean.class));

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.post(url, userId)
                                .content(gson.toJson(receiveFollowRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                );

                //then
                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value(FollowConstant.SUCCESS_FOLLOW_RECEIVE_TRUE));

            }
        }

        @Nested
        class 팔로우신청목록조회 {
            final String url = "/api/follow/{userId}/receive-lists";

            @Test
            void 실패_팔로우신청목록조회_유저정보틀림() throws Exception {
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
            void 성공_팔로우신청목록조회() throws Exception {
                //given
                final FollowRequest followRequest = FollowRequest.builder()
                        .id(1L)
                        .requester(user2)
                        .receiver(user1)
                        .build();
                final List<FollowRequestResponse> followRequestResponseList = new ArrayList<>();
                followRequestResponseList.add(FollowRequestResponse.builder()
                        .followRequestId(followRequest.getId())
                        .requesterId(followRequest.getRequester().getId())
                        .email(followRequest.getRequester().getEmail())
                        .nickname(followRequest.getRequester().getNickname())
                        .profileImage(followRequest.getRequester().getProfileImage())
                        .statusMessage(followRequest.getRequester().getStatusMessage())
                        .plannerAccessScope(followRequest.getRequester().getPlannerAccessScope())
                        .build());
                doReturn(followRequestResponseList).when(followService).getFollowRequestList(any());

                //when
                final ResultActions resultActions = mockMvc.perform(
                        MockMvcRequestBuilders.get(url, userId)
                );

                //then
                resultActions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value(FollowConstant.SUCCESS_GET_FOLLOW_REQUEST_LIST));

            }
        }
    }

    @Nested
    class 회원검색 {
        final String url = "/api/follow/{userId}/searches";

        @Test
        void 성공_회원검색() throws Exception {
            // given
            SearchUserResponse userResponse = SearchUserResponse.builder()
                    .userId(user2.getId())
                    .email(user2.getEmail())
                    .profileImage(user2.getProfileImage())
                    .nickname(user2.getNickname())
                    .statusMessage(user2.getStatusMessage())
                    .plannerAccessScope(user2.getPlannerAccessScope())
                    .isFollow(FollowStatus.EMPTY)
                    .build();
            doReturn(userResponse).when(userFollowService).searchNickname(any(), any());

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("nickname", user2.getNickname()));

            // then
            resultActions.andExpect(status().isOk());
        }
    }

}
