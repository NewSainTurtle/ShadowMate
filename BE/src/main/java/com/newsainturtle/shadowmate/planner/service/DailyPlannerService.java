package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.*;
import com.newsainturtle.shadowmate.user.entity.User;

public interface DailyPlannerService {
    AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest);
    void updateDailyTodo(final User user, final UpdateDailyTodoRequest updateDailyTodoRequest);
    void removeDailyTodo(final User user, final RemoveDailyTodoRequest removeDailyTodoRequest);
    void updateTodayGoal(final User user, final UpdateTodayGoalRequest updateTodayGoalRequest);
    void updateTomorrowGoal(final User user, final UpdateTomorrowGoalRequest updateTomorrowGoalRequest);
    void updateRetrospection(final User user, final UpdateRetrospectionRequest updateRetrospectionRequest);
    void updateRetrospectionImage(final User user, final UpdateRetrospectionImageRequest updateRetrospectionImageRequest);
    void addDailyLike(final User user, final AddDailyLikeRequest addDailyPlannerLikeRequest);
    void removeDailyLike(final User user, final RemoveDailyLikeRequest removeDailyLikeRequest);
    AddTimeTableResponse addTimeTable(final User user, final AddTimeTableRequest addTimeTableRequest);
}
