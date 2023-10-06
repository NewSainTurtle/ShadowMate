package com.newsainturtle.shadowmate.planner_setting.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoveCategoryRequest {

    @NotNull
    private Long categoryId;

    @Builder
    public RemoveCategoryRequest(Long categoryId) {
        this.categoryId = categoryId;
    }
}