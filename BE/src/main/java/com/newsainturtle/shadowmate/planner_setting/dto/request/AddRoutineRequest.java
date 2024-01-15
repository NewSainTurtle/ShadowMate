package com.newsainturtle.shadowmate.planner_setting.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddRoutineRequest {

    @NotNull
    @Size(max = 50)
    private String routineContent;

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$", message = "yyyy-MM-dd 형식의 올바른 날짜값이 아닙니다.")
    private String startDay;

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$", message = "yyyy-MM-dd 형식의 올바른 날짜값이 아닙니다.")
    private String endDay;

    @NotEmpty
    private List<String> days;

    @NotNull
    private Long categoryId;

    @Builder
    public AddRoutineRequest(String routineContent, String startDay, String endDay, List<String> days, Long categoryId) {
        this.routineContent = routineContent;
        this.startDay = startDay;
        this.endDay = endDay;
        this.days = days;
        this.categoryId = categoryId;
    }
}
