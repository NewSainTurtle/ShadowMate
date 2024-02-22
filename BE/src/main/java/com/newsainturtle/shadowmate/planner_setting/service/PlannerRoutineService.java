package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.planner.dto.request.RemoveDailyTodoRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.AddRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.RemoveCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.UpdateRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.response.AddRoutineResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface PlannerRoutineService {
    void removeCategory(final User user, final RemoveCategoryRequest removeCategoryRequest);
    AddRoutineResponse addRoutine(final User user, final AddRoutineRequest addRoutineRequest);
    void updateRoutine(final User user, final UpdateRoutineRequest updateRoutineRequest);
    void removeDailyTodo(final User user, final RemoveDailyTodoRequest removeDailyTodoRequest);
}
