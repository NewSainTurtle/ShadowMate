package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.social.dto.SearchPublicDailyPlannerResponse;

public interface SocialService {

    SearchPublicDailyPlannerResponse searchPublicDailyPlanner(final String sort, final long pageNumber);
}
