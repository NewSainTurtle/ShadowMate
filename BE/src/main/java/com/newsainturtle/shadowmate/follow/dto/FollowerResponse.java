package com.newsainturtle.shadowmate.follow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowerResponse {

    private Long followId;

    private Long followerId;

    private String email;

    private String nickname;

    private String profileImage;
}
