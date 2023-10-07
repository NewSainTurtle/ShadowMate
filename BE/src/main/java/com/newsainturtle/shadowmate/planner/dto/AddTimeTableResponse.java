package com.newsainturtle.shadowmate.planner.dto;

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
