package com.newsainturtle.shadowmate.planner_setting.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @Size(max = 2, message = "이모티콘은 하나만 넣을 수 있습니다.")
    private String categoryEmoticon;

    @Builder
    public UpdateCategoryRequest(Long categoryId, String categoryTitle, Long categoryColorId, String categoryEmoticon) {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.categoryColorId = categoryColorId;
        this.categoryEmoticon = categoryEmoticon;
    }
}
