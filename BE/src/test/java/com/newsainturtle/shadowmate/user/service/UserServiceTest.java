package com.newsainturtle.shadowmate.user.service;

import com.newsainturtle.shadowmate.auth.service.RedisService;
import com.newsainturtle.shadowmate.user.dto.request.UpdateIntroductionRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdatePasswordRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdateUserRequest;
import com.newsainturtle.shadowmate.user.dto.response.ProfileResponse;
import com.newsainturtle.shadowmate.user.dto.response.SearchIntroductionResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisService redisService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final User user = User.builder()
            .id(1L)
            .email("yntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .introduction("소개글")
            .build();
    private final Long userId = 1L;

    @Nested
    class 프로필수정 {
        final String newNickname = "NewNickName";
        final String newProfileImage = "NewProfileImage";
        final String newStatusMessage = "NewStatusMessage";
        final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .newNickname(newNickname)
                .newProfileImage(newProfileImage)
                .newStatusMessage(newStatusMessage)
                .build();

        @Test
        void 실패_닉네임검증Null() {
            //given
            doReturn(null).when(redisService).getNicknameData(newNickname);

            //when
            final UserException result = assertThrows(UserException.class, () -> userService.updateUser(user, updateUserRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.RETRY_NICKNAME);

        }

        @Test
        void 실패_닉네임검증false() {
            //given
            doReturn(false).when(redisService).getNicknameData(newNickname);

            //when
            final UserException result = assertThrows(UserException.class, () -> userService.updateUser(user, updateUserRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.RETRY_NICKNAME);

        }

        @Test
        void 성공_닉네임수정안함() {
            //given
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newNickname(user.getNickname())
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();

            //when
            userService.updateUser(user, updateUserRequest);

            //then
            verify(userRepository, times(1)).updateUser(any(), any(), any(), any(Long.class));

        }

        @Test
        void 성공_내정보수정_닉네임수정() {
            //given
            doReturn(true).when(redisService).getNicknameData(newNickname);

            //when
            userService.updateUser(user, updateUserRequest);

            //then
            verify(redisService, times(1)).deleteNicknameData(any());
            verify(userRepository, times(1)).updateUser(any(), any(), any(), any(Long.class));

        }

    }

    @Nested
    class 비밀번호수정 {
        final String newPassword = "NewPassword";
        final UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                .newPassword(newPassword)
                .oldPassword(user.getPassword())
                .build();

        @Test
        void 실패_비밀번호다름() {
            // given
            doReturn(false).when(bCryptPasswordEncoder).matches(any(), any());

            // when
            final UserException result = assertThrows(UserException.class, () -> userService.updatePassword(user, updatePasswordRequest));

            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.DIFFERENT_PASSWORD);
        }

        @Test
        void 성공_비밀번호수정() {
            // given
            doReturn(true).when(bCryptPasswordEncoder).matches(any(), any());

            // when
            userService.updatePassword(user, updatePasswordRequest);

            // then
            verify(userRepository, times(1)).updatePassword(any(), any(Long.class));
        }
    }

    @Nested
    class 소개글 {
        @Test
        void 성공_소개글조회() {
            // given
            doReturn(user).when(userRepository).findByIdAndWithdrawalIsFalse(userId);
            doReturn(user.getIntroduction()).when(userRepository).findIntroduction(userId);

            // when
            final SearchIntroductionResponse result = userService.searchIntroduction(userId);

            // then
            assertThat(result.getIntroduction()).isEqualTo(user.getIntroduction());
        }

        @Test
        void 성공_소개글수정() {
            // given
            final String newIntroduction = "새로운소개글";
            final UpdateIntroductionRequest updateIntroductionRequest = UpdateIntroductionRequest.builder()
                    .introduction(newIntroduction)
                    .build();

            // when
            userService.updateIntroduction(userId, updateIntroductionRequest);

            // then
            verify(userRepository, times(1)).updateIntroduction(any(), any(Long.class));

        }
    }

    @Nested
    class 회원ID조회 {
        @Test
        void 실패_프로필조회() {
            // given
            doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(userId);

            // when

            // then
            assertThrows(UserException.class, () -> {
                userService.getProfile(userId);
            });
        }

        @Test
        void 성공_프로필조회() {
            // given
            doReturn(user).when(userRepository).findByIdAndWithdrawalIsFalse(userId);

            // when
            final ProfileResponse profileResponse = userService.getProfile(userId);

            // then
            assertThat(profileResponse.getEmail()).isEqualTo(user.getEmail());

        }
    }

    @Nested
    class 회원닉네임조회 {
        @Test
        void 성공_닉네임검색() {
            //given
            doReturn(user).when(userRepository).findByNicknameAndWithdrawalIsFalse(any(String.class));

            //when
            final User findUser = userService.getUserByNickname(user.getNickname());

            //then
            assertThat(findUser).isNotNull();
        }

        @Test
        void 성공_공개된유저닉네임검색() {
            //given
            doReturn(user).when(userRepository).findByNicknameAndPlannerAccessScopeAndWithdrawalIsFalse(any(String.class), any(PlannerAccessScope.class));

            //when
            final User findUser = userService.getUserByNicknameAndScopePublic(user.getNickname());

            //then
            assertThat(findUser).isNotNull();
        }
    }

    @Test
    void 성공_플래너공개범위수정() {
        // given

        // when
        userService.updatePlannerAccessScope(1L, PlannerAccessScope.PRIVATE);

        // then
        verify(userRepository, times(1)).updatePlannerAccessScope(any(PlannerAccessScope.class), any(Long.class));
    }
}
