package com.newsainturtle.shadowmate.kh.user;

import com.newsainturtle.shadowmate.user.dto.ProfileResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import com.newsainturtle.shadowmate.user.service.UserServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    final User user = User.builder()
            .email("aa@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("닉네임임")
            .withdrawal(false)
            .profileImage("TestProfileURL")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();

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
            final Long userId = 1L;
            Optional<User> optUser = Optional.ofNullable(user);
            doReturn(optUser).when(userRepository).findById(userId);

            // when
            final ProfileResponse profileResponse = userService.getProfile(userId);

            // then
            assertThat(profileResponse.getEmail()).isEqualTo(user.getEmail());

        }
    }
}
