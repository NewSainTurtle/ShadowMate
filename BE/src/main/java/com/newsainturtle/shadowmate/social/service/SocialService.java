package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.planner.dto.response.ShareSocialResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.social.dto.response.SearchSocialPlannerResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface SocialService {

    SearchSocialPlannerResponse getSocial(final String sort, final int pageNumber, final String nickname, final String startDate, final String endDate);
    void deleteSocial(final long socialId);
    ShareSocialResponse shareSocial(final User user, final DailyPlanner dailyPlanner, final String socialImage);
}
