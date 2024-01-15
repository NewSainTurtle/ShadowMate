package com.newsainturtle.shadowmate.planner_setting.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetRoutineListResponse {

    private List<GetRoutineResponse> routineList;

    @Builder
    public GetRoutineListResponse(List<GetRoutineResponse> routineList) {
        this.routineList = routineList;
    }
}
