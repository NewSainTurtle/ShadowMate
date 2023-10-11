package com.newsainturtle.shadowmate.follow.service;

import com.newsainturtle.shadowmate.follow.dto.AddFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.FollowerResponse;
import com.newsainturtle.shadowmate.follow.dto.FollowingResponse;
import com.newsainturtle.shadowmate.user.entity.User;

import java.util.List;

public interface FollowService {

    List<FollowingResponse> getFollowing(final User user);

    List<FollowerResponse> getFollower(final User user);

    AddFollowResponse addFollow(final User user, final Long targetUserId);

    void deleteFollower(final User user, final Long targetUserId);

    void deleteFollowRequest(final User user, final Long targetUserId);
}
