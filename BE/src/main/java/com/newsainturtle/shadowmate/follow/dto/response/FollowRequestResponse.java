package com.newsainturtle.shadowmate.follow.dto.response;

import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowRequestResponse {
    private Long followRequestId;
    private Long requesterId;
    private String email;
    private String nickname;
    private String profileImage;
    private String statusMessage;
    private PlannerAccessScope plannerAccessScope;
}
