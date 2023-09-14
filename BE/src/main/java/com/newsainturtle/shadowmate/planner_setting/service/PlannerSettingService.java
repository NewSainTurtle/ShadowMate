package com.newsainturtle.shadowmate.planner_setting.service;


import com.newsainturtle.shadowmate.planner_setting.dto.*;
import com.newsainturtle.shadowmate.user.entity.User;

public interface PlannerSettingService {
    void addCategory(final User user, final AddCategoryRequest addCategoryRequest);

    void updateCategory(final User user, final UpdateCategoryRequest updateCategoryRequest);

    GetCategoryColorListResponse getCategoryColorList();

    GetCategoryListResponse getCategoryList(final User user);

    void setAccessScope(final User user, final SetAccessScopeRequest setAccessScopeRequest);

    void addDday(final User user, final AddDdayRequest addDdayRequest);

    GetDdayListResponse getDdayList(final User user);

    void removeDday(final User user, final RemoveDdayRequest removeDdayRequest);

}
