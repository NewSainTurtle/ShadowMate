package com.newsainturtle.shadowmate.user.dto;

import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long userId;

    private String email;

    private String nickname;

    private String profileImage;

    private String statusMessage;

    private PlannerAccessScope plannerAccessScope;

    private FollowStatus isFollow;
}
