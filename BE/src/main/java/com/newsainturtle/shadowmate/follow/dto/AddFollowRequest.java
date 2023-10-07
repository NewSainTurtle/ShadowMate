package com.newsainturtle.shadowmate.follow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddFollowRequest {

    private Long followingId;
}
