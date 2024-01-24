package com.newsainturtle.shadowmate.planner.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.newsainturtle.shadowmate.common.constant.CommonConstant.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddTimeTableRequest {

    @NotNull
    @Pattern(regexp = DATE_PATTERN, message = DATE_PATTERN_ERROR_MESSAGE)
    private String date;

    @NotNull
    private Long todoId;

    @NotNull
    @Pattern(regexp = DATETIME_PATTERN, message = DATETIME_PATTERN_ERROR_MESSAGE)
    private String startTime;

    @NotNull
    @Pattern(regexp = DATETIME_PATTERN, message = DATETIME_PATTERN_ERROR_MESSAGE)
    private String endTime;

    @Builder
    public AddTimeTableRequest(String date, Long todoId, String startTime, String endTime) {
        this.date = date;
        this.todoId = todoId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
