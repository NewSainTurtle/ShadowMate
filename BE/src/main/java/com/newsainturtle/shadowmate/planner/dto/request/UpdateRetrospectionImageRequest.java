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
public class UpdateRetrospectionImageRequest {

    @NotNull
    @Pattern(regexp = DATE_PATTERN, message = DATE_PATTERN_ERROR_MESSAGE)
    private String date;

    @Size(max = 255)
    private String retrospectionImage;

    @Builder
    public UpdateRetrospectionImageRequest(String date, String retrospectionImage) {
        this.date = date;
        this.retrospectionImage = retrospectionImage;
    }
}
