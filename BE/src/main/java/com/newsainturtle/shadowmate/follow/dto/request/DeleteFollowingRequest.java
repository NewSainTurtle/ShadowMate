package com.newsainturtle.shadowmate.follow.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteFollowingRequest {

    @NotNull
    private Long followingId;

    @Builder
    public DeleteFollowingRequest(Long followingId) {
        this.followingId = followingId;
    }
}
