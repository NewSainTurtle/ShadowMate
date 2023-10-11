package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchCalendarResponse {

    private String plannerAccessScope;
    private List<CalendarDayResponse> dayList;

}
