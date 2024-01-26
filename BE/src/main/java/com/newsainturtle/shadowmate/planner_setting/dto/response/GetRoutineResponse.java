package com.newsainturtle.shadowmate.planner_setting.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetRoutineResponse {
    private Long routineId;
    private String routineContent;
    private String startDay;
    private String endDay;
    private List<String> days;
    private GetCategoryResponse category;
}
