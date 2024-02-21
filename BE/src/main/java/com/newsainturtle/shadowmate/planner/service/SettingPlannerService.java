package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.AddDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.response.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface SettingPlannerService {
    AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest);
    void updateDailyTodo(final User user, final UpdateDailyTodoRequest updateDailyTodoRequest);
}
