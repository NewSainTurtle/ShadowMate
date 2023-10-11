package com.newsainturtle.shadowmate.planner.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchDailyPlannerResponse {

    private String date;
    private String plannerAccessScope;
    private String dday;
    private String todayGoal;
    private String retrospection;
    private String retrospectionImage;
    private String tomorrowGoal;
    private boolean like;
    private long likeCount;
    private int studyTimeHour;
    private int studyTimeMinute;
    private List<DailyPlannerTodo> dailyTodos;

}
