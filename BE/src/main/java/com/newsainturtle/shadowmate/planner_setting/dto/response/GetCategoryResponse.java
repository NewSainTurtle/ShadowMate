package com.newsainturtle.shadowmate.planner_setting.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetCategoryResponse {

    private Long categoryId;
    private String categoryColorCode;
    private String categoryTitle;
    private String categoryEmoticon;

    @Builder
    public GetCategoryResponse(Long categoryId, String categoryColorCode, String categoryTitle, String categoryEmoticon) {
        this.categoryId = categoryId;
        this.categoryColorCode = categoryColorCode;
        this.categoryTitle = categoryTitle;
        this.categoryEmoticon = categoryEmoticon;
    }
}
