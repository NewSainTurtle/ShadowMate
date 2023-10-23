package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalendarDayResponse {

    private String date;
    private int todoCount;
    private int dayStatus;

}
