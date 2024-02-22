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
import com.newsainturtle.shadowmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPlannerSettingServiceImpl extends DateCommonService implements UserPlannerSettingService {

    private final FollowService followService;
    private final DailyPlannerService dailyPlannerService;
    private final SocialService socialService;
    private final UserService userService;
    private final MonthlyPlannerService monthlyPlannerService;

    @Override
    public void setAccessScope(final User user, final SetAccessScopeRequest setAccessScopeRequest) {
        final PlannerAccessScope accessScope = PlannerAccessScope.parsing(setAccessScopeRequest.getPlannerAccessScope());
        if (accessScope == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_PLANNER_ACCESS_SCOPE);
        }

        if (!user.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC) && accessScope.equals(PlannerAccessScope.PUBLIC)) {
            followService.acceptAllFollowRequest(user);
            socialService.updateDeleteTimeAll(null, dailyPlannerService.getDailyPlannerList(user));
        } else if (user.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC) && !accessScope.equals(PlannerAccessScope.PUBLIC)) {
            socialService.updateDeleteTimeAll(LocalDateTime.now(), dailyPlannerService.getDailyPlannerList(user));
        }
        userService.updatePlannerAccessScope(user.getId(), accessScope);
    }

    @Override
    public void deleteUser(final User user) {
        followService.deleteUser(user);
        monthlyPlannerService.deleteUser(user);
        socialService.updateDeleteTimeAll(LocalDateTime.now(), dailyPlannerService.getDailyPlannerList(user));
        userService.deleteUser(user);
    }
}
