package com.newsainturtle.shadowmate.planner_setting.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDdayResponse {
    private Long ddayId;
    private String ddayTitle;
    private String ddayDate;
}
