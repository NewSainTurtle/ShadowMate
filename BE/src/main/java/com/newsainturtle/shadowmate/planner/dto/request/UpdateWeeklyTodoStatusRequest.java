package com.newsainturtle.shadowmate.planner.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.newsainturtle.shadowmate.common.constant.CommonConstant.DATE_PATTERN;
import static com.newsainturtle.shadowmate.common.constant.CommonConstant.DATE_PATTERN_ERROR_MESSAGE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateWeeklyTodoStatusRequest {

    @NotNull
    @Pattern(regexp = DATE_PATTERN, message = DATE_PATTERN_ERROR_MESSAGE)
    private String startDate;

    @NotNull
    @Pattern(regexp = DATE_PATTERN, message = DATE_PATTERN_ERROR_MESSAGE)
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
