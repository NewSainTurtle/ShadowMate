package com.newsainturtle.shadowmate.planner.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.newsainturtle.shadowmate.common.constant.CommonConstant.DATE_PATTERN;
import static com.newsainturtle.shadowmate.common.constant.CommonConstant.DATE_PATTERN_ERROR_MESSAGE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateTomorrowGoalRequest {

    @NotNull
    @Pattern(regexp = DATE_PATTERN, message = DATE_PATTERN_ERROR_MESSAGE)
    private String date;

    @NotNull
    @Size(max = 100)
    private String tomorrowGoal;

    @Builder
    public UpdateTomorrowGoalRequest(String date, String tomorrowGoal) {
        this.date = date;
        this.tomorrowGoal = tomorrowGoal;
    }
}
