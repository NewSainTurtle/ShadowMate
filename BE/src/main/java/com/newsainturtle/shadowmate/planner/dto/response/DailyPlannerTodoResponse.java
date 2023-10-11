package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerTodoResponse {

    private Long todoId;
    private DailyPlannerTodoCategoryResponse category;
    private String todoContent;
    private String todoStatus;
    private DailyPlannerTodoTimeTableResponse timeTable;

}
