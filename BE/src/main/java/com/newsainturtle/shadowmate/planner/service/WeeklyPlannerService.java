package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.AddWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateWeeklyTodoContentRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateWeeklyTodoStatusRequest;
import com.newsainturtle.shadowmate.planner.dto.response.AddWeeklyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.response.WeeklyPlannerTodoResponse;
import com.newsainturtle.shadowmate.user.entity.User;

import java.util.List;

public interface WeeklyPlannerService {
    void checkValidWeek(final String startDateStr, final String endDateStr);
    AddWeeklyTodoResponse addWeeklyTodo(final User user, final AddWeeklyTodoRequest addWeeklyTodoRequest);
    void updateWeeklyTodoContent(final User user, final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest);
    void updateWeeklyTodoStatus(final User user, final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest);
    void removeWeeklyTodo(final User user, final RemoveWeeklyTodoRequest removeWeeklyTodoRequest);
    List<WeeklyPlannerTodoResponse> getWeeklyTodos(final User plannerWriter, final String startDate, final String endDate, final boolean permission);
}
