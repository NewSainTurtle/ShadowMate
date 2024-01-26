package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.social.dto.response.SearchSocialPlannerResponse;

public interface SocialService {

    SearchSocialPlannerResponse getSocial(final String sort, final int pageNumber, final String nickname, final String startDate, final String endDate);

    void deleteSocial(final long socialId);
}
