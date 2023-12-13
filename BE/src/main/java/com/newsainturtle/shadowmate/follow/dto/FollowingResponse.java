package com.newsainturtle.shadowmate.follow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowingResponse {

    private Long followId;

    private Long followingId;

    private String email;

    private String nickname;

    private String profileImage;

    private String statusMessage;
}
