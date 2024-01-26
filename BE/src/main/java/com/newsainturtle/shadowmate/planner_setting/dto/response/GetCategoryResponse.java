package com.newsainturtle.shadowmate.planner_setting.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetCategoryResponse {
    private Long categoryId;
    private String categoryColorCode;
    private String categoryTitle;
    private String categoryEmoticon;
}
