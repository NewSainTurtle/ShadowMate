package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WeeklyPlannerDailyResponse {

    private String date;
    private String retrospection;
    private List<WeeklyPlannerDailyTodoResponse> dailyTodos;

}
