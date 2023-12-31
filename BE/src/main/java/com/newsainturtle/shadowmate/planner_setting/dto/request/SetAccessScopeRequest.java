package com.newsainturtle.shadowmate.planner_setting.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetAccessScopeRequest {

    @NotNull
    private String plannerAccessScope;

    @Builder
    public SetAccessScopeRequest(String plannerAccessScope) {
        this.plannerAccessScope = plannerAccessScope;
    }
}
