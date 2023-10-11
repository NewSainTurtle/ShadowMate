package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchWeeklyPlannerResponse {

    private String plannerAccessScope;
    private String dday;
    private List<WeeklyPlannerTodoResponse> weeklyTodos;
    private List<WeeklyPlannerDailyResponse> dayList;

}
