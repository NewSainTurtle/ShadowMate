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
public class UpdateDailyTodoRequest {

    @NotNull
    @Pattern(regexp = DATE_PATTERN, message = DATE_PATTERN_ERROR_MESSAGE)
    private String date;

    @NotNull
    private Long todoId;

    @NotNull
    @Size(max = 50)
    private String todoContent;

    @NotNull
    private Long categoryId;

    @NotNull
    @Size(max = 3)
    private String todoStatus;

    @Builder
    public UpdateDailyTodoRequest(String date, Long todoId, String todoContent, Long categoryId, String todoStatus) {
        this.date = date;
        this.todoId = todoId;
        this.todoContent = todoContent;
        this.categoryId = categoryId;
        this.todoStatus = todoStatus;
    }
}
