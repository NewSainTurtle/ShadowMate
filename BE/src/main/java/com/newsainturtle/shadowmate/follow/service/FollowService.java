package com.newsainturtle.shadowmate.follow.service;

import com.newsainturtle.shadowmate.follow.dto.response.*;
import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.user.entity.User;

import java.util.List;

public interface FollowService {
    List<FollowingResponse> getFollowing(final User user);
    List<FollowerResponse> getFollower(final User user);
    List<FollowRequestResponse> getFollowRequestList(final User user);
    AddFollowResponse addFollow(final User user, final User targetUser);
    void deleteFollowing(final User user, final User targetUser);
    void deleteFollower(final User user, final User targetUser);
    void deleteFollowRequest(final User user, final User targetUser);
    String receiveFollow(final User user, final User targetUser, final boolean followReceive);
    FollowStatus isFollow(final User user, final User searchUser);
    CountFollowResponse countFollow(final User targetUser);
    SearchUserResponse searchNickname(final User user, final User searchUser);
    void acceptAllFollowRequest(final User user);
    void deleteUser(final User user);
    boolean havePermissionToSearch(final User user, final User plannerWriter);
}
