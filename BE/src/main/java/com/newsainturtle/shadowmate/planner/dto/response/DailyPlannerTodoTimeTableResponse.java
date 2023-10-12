package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyPlannerTodoTimeTableResponse {

    private Long timeTableId;
    private String startTime;
    private String endTime;

}
