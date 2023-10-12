package com.newsainturtle.shadowmate.follow.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteFollowerRequest {

    @NotNull
    private Long followerId;

    @Builder
    public DeleteFollowerRequest(Long followerId) {
        this.followerId = followerId;
    }
}
