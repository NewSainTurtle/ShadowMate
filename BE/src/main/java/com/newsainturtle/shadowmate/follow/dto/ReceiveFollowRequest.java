package com.newsainturtle.shadowmate.follow.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReceiveFollowRequest {

    @NotNull
    private Long requesterId;

    private boolean followReceive;

    @Builder
    public ReceiveFollowRequest(Long requesterId, boolean followReceive) {
        this.requesterId = requesterId;
        this.followReceive = followReceive;
    }
}
