package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.planner.dto.request.ShareSocialRequest;
import com.newsainturtle.shadowmate.planner.dto.response.ShareSocialResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface UserPlannerSocialService {
    ShareSocialResponse shareSocial(final User user, final ShareSocialRequest shareSocialRequest);
}
