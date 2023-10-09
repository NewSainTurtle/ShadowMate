package com.newsainturtle.shadowmate.follow.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddFollowRequest {

    @NotNull
    private Long followingId;

    @Builder
    public AddFollowRequest(Long followingId) {
        this.followingId = followingId;
    }
}
