package com.newsainturtle.shadowmate.follow.service;

import com.newsainturtle.shadowmate.follow.dto.response.AddFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.response.CountFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.response.SearchUserResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFollowServiceImpl implements UserFollowService {

    private final UserService userService;
    private final FollowService followService;

    @Override
    public AddFollowResponse addFollow(final User user, final Long targetUserId) {
        return followService.addFollow(user, userService.getUserById(targetUserId));
    }

    @Override
    public void deleteFollowing(final User user, final Long targetUserId) {
        followService.deleteFollowing(user, userService.getUserById(targetUserId));
    }

    @Override
    public void deleteFollower(final User user, final Long targetUserId) {
        followService.deleteFollower(user, userService.getUserById(targetUserId));
    }

    @Override
    public void deleteFollowRequest(final User user, final Long targetUserId) {
        followService.deleteFollowRequest(user, userService.getUserById(targetUserId));
    }

    @Override
    public String receiveFollow(final User user, final Long targetUserId, final boolean followReceive) {
        return followService.receiveFollow(user, userService.getUserById(targetUserId), followReceive);
    }

    @Override
    @Transactional(readOnly = true)
    public CountFollowResponse countFollow(final Long targetUserId) {
        return followService.countFollow(userService.getUserById(targetUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public SearchUserResponse searchNickname(final User user, final String nickname) {
        return followService.searchNickname(user, userService.getUserByNickname(nickname));
    }
}
