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
public class ChangeDailyTodoSequenceRequest {

    @NotNull
    @Pattern(regexp = DATE_PATTERN, message = DATE_PATTERN_ERROR_MESSAGE)
    private String date;

    @NotNull
    private Long todoId;

    private Long upperTodoId;

    @Builder
    public ChangeDailyTodoSequenceRequest(String date, Long todoId, Long upperTodoId) {
        this.date = date;
        this.todoId = todoId;
        this.upperTodoId = upperTodoId;
    }
}
