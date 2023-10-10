package com.newsainturtle.shadowmate.user.dto;

import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long userId;

    private String email;

    private String nickname;

    private String prfileImage;

    private String statusMessage;

    private PlannerAccessScope plannerAccessScope;

    private boolean isFollow;
}
