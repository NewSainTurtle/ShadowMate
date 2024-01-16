package com.newsainturtle.shadowmate.planner_setting.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetRoutineResponse {

    private Long routineId;
    private String routineContent;
    private String startDay;
    private String endDay;
    private List<String> days;
    private GetCategoryResponse category;

    @Builder
    public GetRoutineResponse(Long routineId, String routineContent, String startDay, String endDay, List<String> days, GetCategoryResponse category) {
        this.routineId = routineId;
        this.routineContent = routineContent;
        this.startDay = startDay;
        this.endDay = endDay;
        this.days = days;
        this.category = category;
    }
}
