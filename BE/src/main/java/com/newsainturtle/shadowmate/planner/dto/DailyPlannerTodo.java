package com.newsainturtle.shadowmate.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerTodo {

    private Long todoId;
    private DailyPlannerTodoCategory category;
    private String todoContent;
    private String todoStatus;
    private DailyPlannerTodoTimeTable timeTable;

}
