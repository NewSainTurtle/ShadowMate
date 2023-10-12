package com.newsainturtle.shadowmate.follow.dto;

import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddFollowResponse {

    private Long followId;

    private PlannerAccessScope plannerAccessScope;
}
