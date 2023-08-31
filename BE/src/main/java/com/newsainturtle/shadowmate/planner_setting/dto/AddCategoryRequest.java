package com.newsainturtle.shadowmate.planner_setting.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddCategoryRequest {

    @NotNull
    @Size(min = 2, max = 10)
    private String categoryTitle;

    @NotNull
    private Long categoryColorId;

    @Size(max = 2)
    private String categoryEmoticon;

    @Builder
    public AddCategoryRequest(String categoryTitle, Long categoryColorId, String categoryEmoticon) {
        this.categoryTitle = categoryTitle;
        this.categoryColorId = categoryColorId;
        this.categoryEmoticon = categoryEmoticon;
    }
}
