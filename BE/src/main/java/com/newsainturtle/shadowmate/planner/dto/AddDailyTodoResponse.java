package com.newsainturtle.shadowmate.planner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddDailyTodoResponse {

    private Long todoId;

    @Builder
    public AddDailyTodoResponse(Long todoId) {
        this.todoId = todoId;
    }
}
