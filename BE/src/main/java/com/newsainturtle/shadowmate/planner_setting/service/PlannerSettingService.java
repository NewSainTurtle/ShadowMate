package com.newsainturtle.shadowmate.planner_setting.service;


import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryColorListResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.SetAccessScopeRequest;
import com.newsainturtle.shadowmate.user.entity.User;

public interface PlannerSettingService {
    void addCategory(Long userId, AddCategoryRequest addCategoryRequest);
    GetCategoryColorListResponse getCategoryList();

    void setAccessScope(final Long userId, final User user, final SetAccessScopeRequest setAccessScopeRequest);
}
