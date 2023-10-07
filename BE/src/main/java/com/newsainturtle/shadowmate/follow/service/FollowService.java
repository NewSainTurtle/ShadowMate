package com.newsainturtle.shadowmate.follow.service;

import com.newsainturtle.shadowmate.follow.dto.AddFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.FollowingResponse;
import com.newsainturtle.shadowmate.user.entity.User;

import java.util.List;

public interface FollowService {

    List<FollowingResponse> getFollowing(final User user);

    AddFollowResponse addFollow(final User user, final Long targetUserId);
}
