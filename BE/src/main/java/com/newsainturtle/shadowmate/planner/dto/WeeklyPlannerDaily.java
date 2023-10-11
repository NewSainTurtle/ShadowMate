package com.newsainturtle.shadowmate.planner.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WeeklyPlannerDaily {

    private String date;
    private String retrospection;
    private List<WeeklyPlannerDailyTodo> dailyTodos;

}
