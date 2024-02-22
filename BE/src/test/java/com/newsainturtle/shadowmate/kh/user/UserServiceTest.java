package com.newsainturtle.shadowmate.kh.user;

import com.newsainturtle.shadowmate.auth.service.RedisServiceImpl;
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
import com.newsainturtle.shadowmate.user.service.UserServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

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
    private RedisServiceImpl redisService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    final User user1 = User.builder()
            .id(1L)
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .withdrawal(false)
            .profileImage("TestProfileURL")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();
    final Long userId1 = user1.getId();

    @Nested
    class 프로필TEST {

        @Test
        void 실패_프로필조회() {
            // given
            final Long userId = 1L;

            // when


            // then
            assertThrows(UserException.class, () -> {
                userService.getProfile(userId);
            });
        }

        @Test
        void 성공_프로필조회() {
            // given
            doReturn(user1).when(userRepository).findByIdAndWithdrawalIsFalse(userId1);

            // when
            final ProfileResponse profileResponse = userService.getProfile(userId1);

            // then
            assertThat(profileResponse.getEmail()).isEqualTo(user1.getEmail());

        }

        @Test
        void 실패_내정보수정_닉네임검증Null() {
            //given
            final String newNickname = "NewNickName";
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newNickname(newNickname)
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();
            doReturn(null).when(redisService).getNicknameData(newNickname);

            //when
            final UserException result = assertThrows(UserException.class, () -> userService.updateUser(user1, updateUserRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.RETRY_NICKNAME);

        }

        @Test
        void 실패_내정보수정_닉네임검증false() {
            //given
            final String newNickname = "NewNickName";
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newNickname(newNickname)
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();
            doReturn(false).when(redisService).getNicknameData(newNickname);

            //when
            final UserException result = assertThrows(UserException.class, () -> userService.updateUser(user1, updateUserRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.RETRY_NICKNAME);

        }

        @Test
        void 성공_내정보수정_닉네임수정안함() {
            //given
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newNickname(user1.getNickname())
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();

            //when
            userService.updateUser(user1, updateUserRequest);

            //then
            verify(userRepository, times(1)).updateUser(any(), any(), any(), any(Long.class));

        }

        @Test
        void 성공_내정보수정_닉네임수정() {
            //given
            final String newNickname = "NewNickName";
            final String newProfileImage = "NewProfileImage";
            final String newStatusMessage = "NewStatusMessage";
            final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .newNickname(newNickname)
                    .newProfileImage(newProfileImage)
                    .newStatusMessage(newStatusMessage)
                    .build();
            doReturn(true).when(redisService).getNicknameData(newNickname);

            //when
            userService.updateUser(user1, updateUserRequest);

            //then
            verify(redisService, times(1)).deleteNicknameData(any());
            verify(userRepository, times(1)).updateUser(any(), any(), any(), any(Long.class));

        }

        @Test
        void 실패_비밀번호수정_비밀번호다름() {
            // given
            final String newPassword = "NewPassword";
            final UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                    .newPassword(newPassword)
                    .oldPassword(user1.getPassword())
                    .build();
            doReturn(false).when(bCryptPasswordEncoder).matches(any(), any());

            // when
            final UserException result = assertThrows(UserException.class, () -> userService.updatePassword(user1, updatePasswordRequest));

            // then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.DIFFERENT_PASSWORD);
        }

        @Test
        void 성공_비밀번호수정() {
            // given
            final String newPassword = "NewPassword";
            final UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                    .newPassword(newPassword)
                    .oldPassword(user1.getPassword())
                    .build();
            doReturn(true).when(bCryptPasswordEncoder).matches(any(), any());

            // when
            userService.updatePassword(user1, updatePasswordRequest);

            // then
            verify(userRepository, times(1)).updatePassword(any(), any(Long.class));
        }

        @Test
        void 성공_소개글조회() {
            // given
            final String newIntroduction = "새로운소개글";
            doReturn(user1).when(userRepository).findByIdAndWithdrawalIsFalse(userId1);
            doReturn(newIntroduction).when(userRepository).findIntroduction(userId1);

            // when
            final SearchIntroductionResponse result = userService.searchIntroduction(userId1);

            // then
            assertThat(result.getIntroduction()).isEqualTo(newIntroduction);
        }

        @Test
        void 성공_소개글수정() {
            // given
            final String newIntroduction = "새로운소개글";
            final UpdateIntroductionRequest updateIntroductionRequest = UpdateIntroductionRequest.builder()
                    .introduction(newIntroduction)
                    .build();

            // when
            userService.updateIntroduction(userId1, updateIntroductionRequest);

            // then
            verify(userRepository, times(1)).updateIntroduction(any(), any(Long.class));

        }

    }

    @Nested
    class 회원TEST {

        @Test
        void 실패_아이디검색() {
            //given
            doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));

            //when
            final UserException result = assertThrows(UserException.class, () -> userService.getUserById(userId1));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
        }

        @Test
        void 성공_아이디검색() {
            //given
            doReturn(user1).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));

            //when
            final User user = userService.getUserById(userId1);

            //then
            assertThat(user).isNotNull();
        }

        @Test
        void 성공_닉네검색() {
            //given
            doReturn(user1).when(userRepository).findByNicknameAndWithdrawalIsFalse(any(String.class));

            //when
            final User user = userService.getUserByNickname(user1.getNickname());

            //then
            assertThat(user).isNotNull();
        }

        @Test
        void 성공_공개된유저닉네임검색() {
            //given
            doReturn(user1).when(userRepository).findByNicknameAndPlannerAccessScopeAndWithdrawalIsFalse(any(String.class), any(PlannerAccessScope.class));

            //when
            final User user = userService.getUserByNicknameAndScopePublic(user1.getNickname());

            //then
            assertThat(user).isNotNull();
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
}
