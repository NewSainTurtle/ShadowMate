package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.ShareSocialRequest;
import com.newsainturtle.shadowmate.planner.dto.response.ShareSocialResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import com.newsainturtle.shadowmate.social.exception.SocialErrorResult;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.social.service.SocialServiceImpl;
import com.newsainturtle.shadowmate.social.service.UserPlannerSocialServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UserPlannerSocialServiceTest extends DateCommonService {

    @InjectMocks
    private UserPlannerSocialServiceImpl userPlannerSocialService;

    @Mock
    private DailyPlannerServiceImpl dailyPlannerService;

    @Mock
    private SocialServiceImpl socialService;

    private final String email = "yntest@shadowmate.com";
    private final String password = "yntest1234";
    private final String nickname = "거북이";
    private final SocialType socialType = SocialType.BASIC;
    private final PlannerAccessScope plannerAccessScope = PlannerAccessScope.PUBLIC;
    private final String date = "2023-09-25";

    private final User user = User.builder()
            .id(1L)
            .email(email)
            .password(password)
            .socialLogin(socialType)
            .nickname(nickname)
            .plannerAccessScope(plannerAccessScope)
            .withdrawal(false)
            .build();
    final DailyPlanner dailyPlanner = DailyPlanner.builder()
            .id(1L)
            .dailyPlannerDay(date)
            .user(user)
            .build();

    @Nested
    class 소셜공유 {
        final String socialImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";
        final ShareSocialRequest shareSocialRequest = ShareSocialRequest.builder()
                .date(date)
                .socialImage(socialImage)
                .build();

        @Test
        void 실패_공개상태가아닌경우() {
            //given
            final User user = User.builder()
                    .id(1L)
                    .email(email)
                    .password(password)
                    .socialLogin(socialType)
                    .nickname(nickname)
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();

            //when
            final SocialException result = assertThrows(SocialException.class, () -> userPlannerSocialService.shareSocial(user, shareSocialRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.FAILED_SHARE_SOCIAL);
        }

        @Test
        void 실패_유효하지않은플래너() {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerService).getDailyPlanner(any(User.class), any(String.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerSocialService.shareSocial(user, shareSocialRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }

        @Test
        void 실패_이미공유한_소셜재공유() {
            //given
            doReturn(dailyPlanner).when(dailyPlannerService).getDailyPlanner(any(User.class), any(String.class));
            doThrow(new SocialException(SocialErrorResult.ALREADY_SHARED_SOCIAL)).when(socialService).shareSocial(any(User.class), any(DailyPlanner.class), any(String.class));

            //when
            final SocialException result = assertThrows(SocialException.class, () -> userPlannerSocialService.shareSocial(user, shareSocialRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.ALREADY_SHARED_SOCIAL);
        }

        @Test
        void 성공_소셜공유() {
            //given
            doReturn(dailyPlanner).when(dailyPlannerService).getDailyPlanner(any(User.class), any(String.class));
            doReturn(ShareSocialResponse.builder().socialId(1L).build()).when(socialService).shareSocial(any(User.class), any(DailyPlanner.class), any(String.class));

            //when
            ShareSocialResponse shareSocialResponse = userPlannerSocialService.shareSocial(user, shareSocialRequest);

            //then
            assertThat(shareSocialResponse).isNotNull();
            assertThat(shareSocialResponse.getSocialId()).isEqualTo(1L);
        }
    }
}
