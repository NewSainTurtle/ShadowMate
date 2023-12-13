package com.newsainturtle.shadowmate.planner_setting.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetDdayListResponse {
    private List<GetDdayResponse> ddayList;

    @Builder
    public GetDdayListResponse(List<GetDdayResponse> ddayList) {
        this.ddayList = ddayList;
    }
}
