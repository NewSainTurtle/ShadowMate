package com.newsainturtle.shadowmate.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoveTokenRequest {

    @NotNull
    private Long userId;

    @NotNull
    private String type;

    @Builder
    public RemoveTokenRequest(final Long userId, final String type) {
        this.userId = userId;
        this.type = type;
    }
}
