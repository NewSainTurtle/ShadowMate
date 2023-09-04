package com.newsainturtle.shadowmate.planner_setting.service;


import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryColorListResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.SetAccessScopeRequest;

public interface PlannerSettingService {
    void addCategory(Long userId, AddCategoryRequest addCategoryRequest);
    GetCategoryColorListResponse getCategoryList();

    void setAccessScope(final Long userId, final SetAccessScopeRequest setAccessScopeRequest);
}
