package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.planner_setting.dto.request.*;
import com.newsainturtle.shadowmate.planner_setting.dto.response.*;
import com.newsainturtle.shadowmate.user.entity.User;

public interface PlannerSettingService {
    AddCategoryResponse addCategory(final User user, final AddCategoryRequest addCategoryRequest);
    void updateCategory(final User user, final UpdateCategoryRequest updateCategoryRequest);
    void removeCategory(final User user, final RemoveCategoryRequest removeCategoryRequest);
    GetCategoryColorListResponse getCategoryColorList();
    GetCategoryListResponse getCategoryList(final User user);
    void setAccessScope(final User user, final SetAccessScopeRequest setAccessScopeRequest);
    AddDdayResponse addDday(final User user, final AddDdayRequest addDdayRequest);
    GetDdayListResponse getDdayList(final User user);
    void removeDday(final User user, final RemoveDdayRequest removeDdayRequest);
    void updateDday(final User user, final UpdateDdayRequest updateDdayRequest);
    AddRoutineResponse addRoutine(final User user, final AddRoutineRequest addRoutineRequest);
}
