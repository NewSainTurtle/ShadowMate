package com.newsainturtle.shadowmate.planner.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateWeeklyTodoStatusRequest {

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$", message = "yyyy-MM-dd 형식의 올바른 날짜값이 아닙니다.")
    private String startDate;

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$", message = "yyyy-MM-dd 형식의 올바른 날짜값이 아닙니다.")
    private String endDate;

    @NotNull
    private Long weeklyTodoId;

    @NotNull
    private Boolean weeklyTodoStatus;

    @Builder
    public UpdateWeeklyTodoStatusRequest(String startDate, String endDate, Long weeklyTodoId, Boolean weeklyTodoStatus) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.weeklyTodoId = weeklyTodoId;
        this.weeklyTodoStatus = weeklyTodoStatus;
    }
}
