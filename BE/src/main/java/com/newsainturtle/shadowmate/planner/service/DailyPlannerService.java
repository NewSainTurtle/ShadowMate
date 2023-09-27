package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface DailyPlannerService {
    AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest);
}
