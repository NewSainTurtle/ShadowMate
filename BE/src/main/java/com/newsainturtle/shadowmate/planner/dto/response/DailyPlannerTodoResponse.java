package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyPlannerTodoResponse {

    private Long todoId;
    private DailyPlannerTodoCategoryResponse category;
    private String todoContent;
    private String todoStatus;
    private Double todoIndex;
    private List<DailyPlannerTodoTimeTableResponse> timeTables;

}
