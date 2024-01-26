package com.newsainturtle.shadowmate.follow.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteFollowRequestRequest {

    @NotNull
    private Long receiverId;

    @Builder
    public DeleteFollowRequestRequest(Long receiverId) {
        this.receiverId = receiverId;
    }
}
