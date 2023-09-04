package com.newsainturtle.shadowmate.planner_setting.dto;

import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetCategoryListResponse {
    private List<Category> categoryList;

    @Builder
    public GetCategoryListResponse(List<Category> categoryList) {
        this.categoryList = categoryList;
    }
}
