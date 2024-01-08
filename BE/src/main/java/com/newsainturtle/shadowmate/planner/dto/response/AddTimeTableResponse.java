package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddTimeTableResponse {

    private Long timeTableId;

    @Builder
    public AddTimeTableResponse(Long timeTableId) {
        this.timeTableId = timeTableId;
    }
}
