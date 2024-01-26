package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CalendarDayTotalResponse {

    private List<CalendarDayResponse> calendarDayResponseList;
    private int todoTotal;
    private int todoIncomplete;
    private long plannerLikeCount;

}
