package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.planner_setting.dto.request.*;
import com.newsainturtle.shadowmate.planner_setting.dto.response.*;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.user.entity.User;

public interface PlannerSettingService {
    Category getCategory(final User user, final Long categoryId);
    AddCategoryResponse addCategory(final User user, final AddCategoryRequest addCategoryRequest);
    void updateCategory(final User user, final UpdateCategoryRequest updateCategoryRequest);
    void removeCategory(final User user, final Category category, final long count);
    GetCategoryColorListResponse getCategoryColorList();
    GetCategoryListResponse getCategoryList(final User user);
    AddDdayResponse addDday(final User user, final AddDdayRequest addDdayRequest);
    GetDdayListResponse getDdayList(final User user);
    void removeDday(final User user, final RemoveDdayRequest removeDdayRequest);
    void updateDday(final User user, final UpdateDdayRequest updateDdayRequest);
}
