package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchCalendarResponse {

    private String plannerAccessScope;
    private List<CalendarDayResponse> dayList;
    private int todoTotal;
    private int todoComplete;
    private int todoIncomplete;
    private long plannerLikeCount;

    @Builder
    public SearchCalendarResponse(final String plannerAccessScope, final List<CalendarDayResponse> dayList, final int todoTotal, final int todoComplete, final int todoIncomplete, final long plannerLikeCount) {
        this.plannerAccessScope = plannerAccessScope;
        this.dayList = dayList;
        this.todoTotal = todoTotal;
        this.todoComplete = todoComplete;
        this.todoIncomplete = todoIncomplete;
        this.plannerLikeCount = plannerLikeCount;
    }
}
