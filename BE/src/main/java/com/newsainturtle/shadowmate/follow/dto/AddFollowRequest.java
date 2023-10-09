package com.newsainturtle.shadowmate.follow.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class AddFollowRequest {

    @NotNull
    private Long followingId;
}
