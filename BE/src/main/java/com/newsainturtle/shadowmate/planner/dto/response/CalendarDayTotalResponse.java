package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CalendarDayTotalResponse {

    private List<CalendarDayResponse> calendarDayResponseList;
    private int todoTotal;
    private int todoIncomplete;
    private long plannerLikeCount;

    @Builder
    public CalendarDayTotalResponse(final List<CalendarDayResponse> calendarDayResponseList, final int todoTotal, final int todoIncomplete, final long plannerLikeCount) {
        this.calendarDayResponseList = calendarDayResponseList;
        this.todoTotal = todoTotal;
        this.todoIncomplete = todoIncomplete;
        this.plannerLikeCount = plannerLikeCount;
    }
}
