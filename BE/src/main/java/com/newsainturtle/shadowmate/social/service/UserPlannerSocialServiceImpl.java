package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.planner.dto.request.ShareSocialRequest;
import com.newsainturtle.shadowmate.planner.dto.response.ShareSocialResponse;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerService;
import com.newsainturtle.shadowmate.social.dto.response.SearchSocialPlannerResponse;
import com.newsainturtle.shadowmate.social.exception.SocialErrorResult;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPlannerSocialServiceImpl implements UserPlannerSocialService {

    private final SocialService socialService;
    private final DailyPlannerService dailyPlannerService;
    private final UserService userService;

    @Override
    @Transactional
    public ShareSocialResponse shareSocial(final User user, final ShareSocialRequest shareSocialRequest) {
        if (!user.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC)) {
            throw new SocialException(SocialErrorResult.FAILED_SHARE_SOCIAL);
        }
        return socialService.shareSocial(user, dailyPlannerService.getDailyPlanner(user, shareSocialRequest.getDate()), shareSocialRequest.getSocialImage());
    }

    @Override
    public SearchSocialPlannerResponse getSocial(final String sort, final int pageNumber, final String nickname, final String startDate, final String endDate) {
        if (nickname == null || nickname.isEmpty())
            return socialService.getSocial(sort, pageNumber, startDate, endDate);
        return socialService.getSocial(sort, pageNumber, userService.getUserByNicknameAndScopePublic(nickname), startDate, endDate);
    }
}