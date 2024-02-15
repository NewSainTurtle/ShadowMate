package com.newsainturtle.shadowmate.follow.service;

import com.newsainturtle.shadowmate.follow.dto.response.AddFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.response.CountFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.response.SearchUserResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface UserFollowService {
    AddFollowResponse addFollow(final User user, final Long targetUserId);
    void deleteFollowing(final User user, final Long targetUserId);
    void deleteFollower(final User user, final Long targetUserId);
    void deleteFollowRequest(final User user, final Long targetUserId);
    String receiveFollow(final User user, final Long targetUserId, final boolean followReceive);
    CountFollowResponse countFollow(final Long targetUserId);
    SearchUserResponse searchNickname(final User user, final String nickname);
}
