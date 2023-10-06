package com.newsainturtle.shadowmate.planner.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddWeeklyTodoRequest {

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$", message = "yyyy-MM-dd 형식의 올바른 날짜값이 아닙니다.")
    private String startDate;

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$", message = "yyyy-MM-dd 형식의 올바른 날짜값이 아닙니다.")
    private String endDate;

    @NotNull
    @Size(max = 50)
    private String weeklyTodoContent;

    @Builder
    public AddWeeklyTodoRequest(String startDate, String endDate, String weeklyTodoContent) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.weeklyTodoContent = weeklyTodoContent;
    }
}
