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

import static com.newsainturtle.shadowmate.common.constant.CommonConstant.DATE_PATTERN;
import static com.newsainturtle.shadowmate.common.constant.CommonConstant.DATE_PATTERN_ERROR_MESSAGE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddRoutineRequest {

    @NotNull
    @Size(max = 50)
    private String routineContent;

    @NotNull
    @Pattern(regexp = DATE_PATTERN, message = DATE_PATTERN_ERROR_MESSAGE)
    private String startDay;

    @NotNull
    @Pattern(regexp = DATE_PATTERN, message = DATE_PATTERN_ERROR_MESSAGE)
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
