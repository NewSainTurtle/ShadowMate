package com.newsainturtle.shadowmate.planner_setting.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetCategoryListResponse {
    private List<GetCategoryResponse> categoryList;

    @Builder
    public GetCategoryListResponse(List<GetCategoryResponse> categoryList) {
        this.categoryList = categoryList;
    }
}
