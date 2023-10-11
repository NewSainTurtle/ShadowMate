package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerTodoCategoryResponse {

    private Long categoryId;
    private String categoryTitle;
    private String categoryColorCode;
    private String categoryEmoticon;

}
