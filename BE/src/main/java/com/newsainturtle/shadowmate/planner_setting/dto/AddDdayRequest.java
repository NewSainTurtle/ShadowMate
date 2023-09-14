package com.newsainturtle.shadowmate.planner_setting.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddDdayRequest {

    @NotNull
    @Size(max = 40)
    private String ddayTitle;

    @NotNull
    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$")
    private String ddayDate;

    @Builder
    public AddDdayRequest(String ddayTitle, String ddayDate) {
        this.ddayTitle = ddayTitle;
        this.ddayDate = ddayDate;
    }
}