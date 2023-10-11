package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyPlannerTodoResponse {

    private long weeklyTodoId;
    private String weeklyTodoContent;
    private boolean weeklyTodoStatus;

}
