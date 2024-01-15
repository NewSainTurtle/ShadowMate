package com.newsainturtle.shadowmate.planner_setting.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddRoutineResponse {

    private Long routineId;

    @Builder
    public AddRoutineResponse(Long routineId) {
        this.routineId = routineId;
    }
}
