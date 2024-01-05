package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.social.dto.SearchPublicDailyPlannerResponse;

public interface SocialService {

    SearchPublicDailyPlannerResponse searchPublicDailyPlanner(final String sort, final int pageNumber, final String nickname);

    void deleteSocial(final long socialId);
}
