package com.newsainturtle.shadowmate.follow.dto;

import com.newsainturtle.shadowmate.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowingResponse {

    private User followerId;

    private User followingId;
}
