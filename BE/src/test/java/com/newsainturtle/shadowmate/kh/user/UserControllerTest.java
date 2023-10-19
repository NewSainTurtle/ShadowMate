package com.newsainturtle.shadowmate.kh.user;

import com.google.gson.Gson;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.common.GlobalExceptionHandler;
import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.user.constant.UserConstant;
import com.newsainturtle.shadowmate.user.controller.UserController;
import com.newsainturtle.shadowmate.user.dto.UpdateProfileImageRequest;
import com.newsainturtle.shadowmate.user.dto.UserResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.service.UserServiceImpl;
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

import static com.newsainturtle.shadowmate.user.constant.UserConstant.SUCCESS_UPDATE_PROFILEIMAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private AuthServiceImpl authService;

    private MockMvc mockMvc;

    private Gson gson;

    final User user1 = User.builder()
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .withdrawal(false)
            .profileImage("TestProfileURL")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();

    final User user2 = User.builder()
            .email("test2@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이2")
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

        @Test
        void 실패_프로필사진수정_유저틀림() throws Exception {
            //given
            final String url = "/api/users/{userId}/images";
            final UpdateProfileImageRequest updateProfileImageRequest = UpdateProfileImageRequest.builder()
                    .newProfileImage("NewProfileImage")
                    .build();
            doThrow(new AuthException(AuthErrorResult.UNREGISTERED_USER)).when(authService).certifyUser(any(), any());

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url,userId)
                            .content(gson.toJson(updateProfileImageRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            //then
            resultActions.andExpect(status().isForbidden());
        }


        @Test
        void 성공_프로필사진수정() throws Exception {
            //given
            final String url = "/api/users/{userId}/images";
            final UpdateProfileImageRequest updateProfileImageRequest = UpdateProfileImageRequest.builder()
                    .newProfileImage("NewProfileImage")
                    .build();

            //when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.put(url,userId)
                            .content(gson.toJson(updateProfileImageRequest))
                            .contentType(MediaType.APPLICATION_JSON));

            //then
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SUCCESS_UPDATE_PROFILEIMAGE));
        }

    }

    @Nested
    class 회원TEST {

        final String url = "/api/users/{userId}/searches";

        final Long userId = 1L;

        @Test
        void 실패_회원없음() throws Exception {
            // given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_NICKNAME)).when(userService).searchNickname(any(), any());

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("nickname","없는닉네임"));

            // then
            resultActions.andExpect(status().isNotFound());

        }

        @Test
        void 성공_회원검색() throws Exception {
            // given
            UserResponse userResponse = UserResponse.builder()
                    .userId(user2.getId())
                    .email(user2.getEmail())
                    .profileImage(user2.getProfileImage())
                    .nickname(user2.getNickname())
                    .statusMessage(user2.getStatusMessage())
                    .plannerAccessScope(user2.getPlannerAccessScope())
                    .isFollow(FollowStatus.EMPTY)
                    .build();
            doReturn(userResponse).when(userService).searchNickname(any(), any());

            // when
            final ResultActions resultActions = mockMvc.perform(
                    MockMvcRequestBuilders.get(url, userId)
                            .param("nickname", user2.getNickname()));

            // then
            resultActions.andExpect(status().isOk());
        }
    }
}
