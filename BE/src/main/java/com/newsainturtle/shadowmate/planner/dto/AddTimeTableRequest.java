package com.newsainturtle.shadowmate.planner.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddTimeTableRequest {

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$", message = "yyyy-MM-dd 형식의 올바른 날짜값이 아닙니다.")
    private String date;

    @NotNull
    private Long todoId;

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])) (0[1-9]|1[0-9]|2[0-3]):(00|10|20|30|40|50)$", message = "yyyy-MM-dd HH:mm 형식의 올바른값이 아닙니다.")
    private String startTime;

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])) (0[1-9]|1[0-9]|2[0-3]):(00|10|20|30|40|50)$", message = "yyyy-MM-dd HH:mm 형식의 올바른값이 아닙니다.")
    private String endTime;

    @Builder
    public AddTimeTableRequest(String date, Long todoId, String startTime, String endTime) {
        this.date = date;
        this.todoId = todoId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}