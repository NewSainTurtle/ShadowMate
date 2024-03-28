package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.follow.service.FollowService;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerService;
import com.newsainturtle.shadowmate.planner.service.MonthlyPlannerService;
import com.newsainturtle.shadowmate.planner_setting.dto.request.SetAccessScopeRequest;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.social.service.SocialService;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlannerAccessScopeServiceTest extends DateCommonService {

    @InjectMocks
    private UserPlannerSettingServiceImpl userPlannerSettingService;

    @Mock
    private FollowService followService;

    @Mock
    private UserService userService;

    @Mock
    private SocialService socialService;

    @Mock
    private DailyPlannerService dailyPlannerService;

    @Mock
    private MonthlyPlannerService monthlyPlannerService;

    private final User user = User.builder()
            .id(1L)
            .email("yntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @Test
    void 성공_회원탈퇴() {
        //given

        //when
        userPlannerSettingService.deleteUser(user);

        //then
        verify(followService, times(1)).deleteUser(any(User.class));
        verify(monthlyPlannerService, times(1)).deleteUser(any(User.class));
        verify(socialService, times(1)).updateDeleteTimeAll(any(LocalDateTime.class), any(List.class));
        verify(userService, times(1)).deleteUser(any(User.class));
    }

    @Nested
    class 플래너공개여부설정 {
        @Test
        void 실패_잘못된범위값() {
            //given
            final SetAccessScopeRequest setAccessScopeRequest = SetAccessScopeRequest.builder()
                    .plannerAccessScope("잘못된범위값")
                    .build();

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> userPlannerSettingService.setAccessScope(user, setAccessScopeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_PLANNER_ACCESS_SCOPE);
        }

        @Test
        void 성공_플래너공개여부설정_비공개에서_공개로() {
            //given
            final SetAccessScopeRequest setAccessScopeRequest = SetAccessScopeRequest.builder()
                    .plannerAccessScope("전체공개")
                    .build();
            final User user2 = User.builder()
                    .id(2L)
                    .email("jntest@shadowmate.com")
                    .password("yntest1234")
                    .socialLogin(SocialType.BASIC)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.PRIVATE)
                    .withdrawal(false)
                    .build();

            doReturn(new ArrayList<>()).when(dailyPlannerService).getDailyPlannerList(any(User.class));

            //when
            userPlannerSettingService.setAccessScope(user2, setAccessScopeRequest);

            //then
            verify(followService, times(1)).acceptAllFollowRequest(any(User.class));
            verify(dailyPlannerService, times(1)).getDailyPlannerList(any(User.class));
            verify(socialService, times(1)).updateDeleteTimeAll(any(), any(List.class));
            verify(userService, times(1)).updatePlannerAccessScope(any(Long.class), any(PlannerAccessScope.class));
        }


        @Test
        void 성공_플래너공개여부설정_공개에서_비공개() {
            //given
            final SetAccessScopeRequest setAccessScopeRequest = SetAccessScopeRequest.builder()
                    .plannerAccessScope("비공개")
                    .build();

            doReturn(new ArrayList<>()).when(dailyPlannerService).getDailyPlannerList(any(User.class));

            //when
            userPlannerSettingService.setAccessScope(user, setAccessScopeRequest);

            //then
            verify(dailyPlannerService, times(1)).getDailyPlannerList(any(User.class));
            verify(socialService, times(1)).updateDeleteTimeAll(any(), any(List.class));
            verify(userService, times(1)).updatePlannerAccessScope(any(Long.class), any(PlannerAccessScope.class));
        }
    }
}