package com.newsainturtle.shadowmate.planner_setting.service;


import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryColorListResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryListResponse;

public interface PlannerSettingService {
    void addCategory(final Long userId, final AddCategoryRequest addCategoryRequest);

    GetCategoryColorListResponse getCategoryColorList();

    GetCategoryListResponse getCategoryList(final Long userId);
}
