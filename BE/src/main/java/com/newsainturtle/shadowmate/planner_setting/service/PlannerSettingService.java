package com.newsainturtle.shadowmate.planner_setting.service;


import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryColorListResponse;

public interface PlannerSettingService {
    void addCategory(Long userId, AddCategoryRequest addCategoryRequest);
    GetCategoryColorListResponse getCategoryColorList();
}
