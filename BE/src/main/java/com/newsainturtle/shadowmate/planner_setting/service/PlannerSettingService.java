package com.newsainturtle.shadowmate.planner_setting.service;


import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;

import java.util.List;

public interface PlannerSettingService {
    void addCategory(Long userId, AddCategoryRequest addCategoryRequest);
    List<CategoryColor> getCategoryList();
}
