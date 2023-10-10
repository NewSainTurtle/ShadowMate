package com.newsainturtle.shadowmate.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerTodoCategory {

    private Long categoryId;
    private String categoryTitle;
    private String categoryColorCode;
    private String categoryEmoticon;

}
