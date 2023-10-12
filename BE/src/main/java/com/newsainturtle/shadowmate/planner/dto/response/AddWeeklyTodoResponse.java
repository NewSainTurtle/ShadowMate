package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddWeeklyTodoResponse {

    private Long weeklyTodoId;

    @Builder
    public AddWeeklyTodoResponse(Long weeklyTodoId) {
        this.weeklyTodoId = weeklyTodoId;
    }
}
