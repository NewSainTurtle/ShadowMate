package com.newsainturtle.shadowmate.follow.dto;

import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
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

    private String statusMessage;

    private FollowStatus isFollow;
}
