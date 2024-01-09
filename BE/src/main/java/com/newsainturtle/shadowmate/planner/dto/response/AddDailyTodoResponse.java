package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddDailyTodoResponse {

    private Long todoId;
    private Double todoIndex;

}
