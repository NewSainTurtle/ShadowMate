package com.newsainturtle.shadowmate.planner_setting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddDdayRequest {

    @NotNull
    @Size(max = 40)
    private String ddayTitle;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date ddayDate;

    @Builder
    public AddDdayRequest(String ddayTitle, Date ddayDate) {
        this.ddayTitle = ddayTitle;
        this.ddayDate = ddayDate;
    }
}