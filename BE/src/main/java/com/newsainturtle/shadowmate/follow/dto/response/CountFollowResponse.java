package com.newsainturtle.shadowmate.follow.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CountFollowResponse {
    private Long followerCount;
    private Long followingCount;
}
