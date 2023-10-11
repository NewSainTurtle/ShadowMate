package com.newsainturtle.shadowmate.planner.dto;

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
