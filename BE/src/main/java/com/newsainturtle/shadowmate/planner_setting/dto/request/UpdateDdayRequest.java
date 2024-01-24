package com.newsainturtle.shadowmate.planner_setting.dto.request;

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
public class UpdateDdayRequest {

    @NotNull
    private Long ddayId;

    @NotNull
    @Size(min = 1, max = 20)
    private String ddayTitle;

    @NotNull
    @Pattern(regexp = DATE_PATTERN, message = DATE_PATTERN_ERROR_MESSAGE)
    private String ddayDate;

    @Builder
    public UpdateDdayRequest(Long ddayId, String ddayTitle, String ddayDate) {
        this.ddayId = ddayId;
        this.ddayTitle = ddayTitle;
        this.ddayDate = ddayDate;
    }
}