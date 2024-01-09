package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyPlannerDailyTodoResponse {

    private Long todoId;
    private DailyPlannerTodoCategoryResponse category;
    private String todoContent;
    private String todoStatus;

}
