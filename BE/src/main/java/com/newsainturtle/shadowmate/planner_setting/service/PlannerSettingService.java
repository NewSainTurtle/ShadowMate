package com.newsainturtle.shadowmate.planner_setting.service;


import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryColorListResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryListResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.SetAccessScopeRequest;
import com.newsainturtle.shadowmate.user.entity.User;

public interface PlannerSettingService {
    void addCategory(final Long userId, final AddCategoryRequest addCategoryRequest);

    GetCategoryColorListResponse getCategoryColorList();

    GetCategoryListResponse getCategoryList(final Long userId);

    void setAccessScope(final User user, final SetAccessScopeRequest setAccessScopeRequest);

}
