package com.newsainturtle.shadowmate.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyPlannerTodo {

    private long weeklyTodoId;
    private String weeklyTodoContent;
    private boolean weeklyTodoStatus;

}
