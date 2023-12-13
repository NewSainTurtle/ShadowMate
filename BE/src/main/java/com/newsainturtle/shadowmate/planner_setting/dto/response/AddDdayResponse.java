package com.newsainturtle.shadowmate.planner_setting.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddDdayResponse {

    private Long ddayId;

    @Builder
    public AddDdayResponse(Long ddayId) {
        this.ddayId = ddayId;
    }
}
