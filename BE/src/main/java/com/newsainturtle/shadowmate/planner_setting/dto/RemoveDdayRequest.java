package com.newsainturtle.shadowmate.planner_setting.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoveDdayRequest {

    @NotNull
    private Long ddayId;

    @Builder
    public RemoveDdayRequest(Long ddayId) {
        this.ddayId = ddayId;
    }
}
