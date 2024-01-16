package com.newsainturtle.shadowmate.planner_setting.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoveRoutineRequest {

    @NotNull
    private Long routineId;

    @NotNull
    private Integer order;

    @Builder
    public RemoveRoutineRequest(Long routineId, Integer order) {
        this.routineId = routineId;
        this.order = order;
    }
}
