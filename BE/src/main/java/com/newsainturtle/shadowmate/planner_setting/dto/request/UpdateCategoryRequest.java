package com.newsainturtle.shadowmate.planner_setting.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.newsainturtle.shadowmate.planner_setting.constant.PlannerSettingConstant.EMOTICON_ERROR_MESSAGE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCategoryRequest {

    @NotNull
    private Long categoryId;

    @NotNull
    @Size(min = 1, max = 10)
    private String categoryTitle;

    @NotNull
    private Long categoryColorId;

    @Size(max = 2, message = EMOTICON_ERROR_MESSAGE)
    private String categoryEmoticon;

    @Builder
    public UpdateCategoryRequest(Long categoryId, String categoryTitle, Long categoryColorId, String categoryEmoticon) {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.categoryColorId = categoryColorId;
        this.categoryEmoticon = categoryEmoticon;
    }
}
