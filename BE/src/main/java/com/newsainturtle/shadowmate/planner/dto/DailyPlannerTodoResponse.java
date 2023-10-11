package com.newsainturtle.shadowmate.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerTodoResponse {

    private Long todoId;
    private DailyPlannerTodoCategory category;
    private String todoContent;
    private String todoStatus;
    private DailyPlannerTodoTimeTableResponse timeTable;

}
