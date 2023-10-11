package com.newsainturtle.shadowmate.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyPlannerDailyTodo {

    private Long todoId;
    private DailyPlannerTodoCategory category;
    private String todoContent;
    private String todoStatus;

}
