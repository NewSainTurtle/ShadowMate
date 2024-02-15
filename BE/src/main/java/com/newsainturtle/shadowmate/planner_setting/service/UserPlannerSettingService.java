package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.planner_setting.dto.request.SetAccessScopeRequest;
import com.newsainturtle.shadowmate.user.entity.User;

public interface UserPlannerSettingService {
    void setAccessScope(final User user, final SetAccessScopeRequest setAccessScopeRequest);
    void deleteUser(final User user);
}
