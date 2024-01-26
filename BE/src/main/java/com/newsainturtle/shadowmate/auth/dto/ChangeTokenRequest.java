package com.newsainturtle.shadowmate.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeTokenRequest {

    @NotNull
    private String type;

    @Builder
    public ChangeTokenRequest(String type) {
        this.type = type;
    }
}
