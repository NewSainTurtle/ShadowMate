package com.newsainturtle.shadowmate.planner.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeDailyTodoSequenceRequest {

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$", message = "yyyy-MM-dd 형식의 올바른 날짜값이 아닙니다.")
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
