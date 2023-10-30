package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.social.dto.SearchPublicDailyPlannerResponse;
import com.newsainturtle.shadowmate.social.entity.Social;

import java.util.List;

public interface SocialService {

    SearchPublicDailyPlannerResponse searchPublicDailyPlanner(final String sort, final long pageNumber);
}
