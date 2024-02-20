package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner_setting.dto.request.AddRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.RemoveRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.UpdateRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.response.AddRoutineResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.response.GetRoutineListResponse;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.user.entity.User;

public interface RoutineService {
    AddRoutineResponse addRoutine(final User user, final Category category, final AddRoutineRequest addRoutineRequest);
    GetRoutineListResponse getRoutineList(final User user);
    void removeRoutine(final User user, final RemoveRoutineRequest removeRoutineRequest);
    void updateRoutine(final User user, final Category category, final UpdateRoutineRequest updateRoutineRequest);
    long getRoutineCount(final Category category);
    void removeRoutineTodo(final Todo todo);
}
