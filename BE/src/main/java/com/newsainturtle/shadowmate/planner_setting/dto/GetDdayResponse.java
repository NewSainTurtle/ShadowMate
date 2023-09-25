package com.newsainturtle.shadowmate.planner_setting.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetDdayResponse {

    private Long ddayId;
    private String ddayTitle;
    private String ddayDate;

    @Builder
    public GetDdayResponse(Long ddayId, String ddayTitle, String ddayDate) {
        this.ddayId = ddayId;
        this.ddayTitle = ddayTitle;
        this.ddayDate = ddayDate;
    }
}
