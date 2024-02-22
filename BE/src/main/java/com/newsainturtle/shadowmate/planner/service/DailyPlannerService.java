package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.dto.response.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.response.AddTimeTableResponse;
import com.newsainturtle.shadowmate.planner.dto.response.SearchDailyPlannerResponse;
import com.newsainturtle.shadowmate.planner.dto.response.WeeklyPlannerDailyResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.user.entity.User;

import java.util.List;

public interface DailyPlannerService {
    void checkValidDate(final String date);
    Todo getTodo(final Long todoId, final DailyPlanner dailyPlanner);
    AddDailyTodoResponse addDailyTodo(final User user, final Category category, final AddDailyTodoRequest addDailyTodoRequest);
    void updateDailyTodo(final User user, final Category category, final UpdateDailyTodoRequest updateDailyTodoRequest);
    void removeDailyTodo(final long todoId);
    void updateTodayGoal(final User user, final UpdateTodayGoalRequest updateTodayGoalRequest);
    void updateTomorrowGoal(final User user, final UpdateTomorrowGoalRequest updateTomorrowGoalRequest);
    void updateRetrospection(final User user, final UpdateRetrospectionRequest updateRetrospectionRequest);
    void updateRetrospectionImage(final User user, final UpdateRetrospectionImageRequest updateRetrospectionImageRequest);
    void addDailyLike(final User user, final User plannerWriter, final AddDailyLikeRequest addDailyPlannerLikeRequest);
    void removeDailyLike(final User user, final User plannerWriter, final RemoveDailyLikeRequest removeDailyLikeRequest);
    long getMonthlyLikeCount(final List<Long> dailyPlannerIdList);
    AddTimeTableResponse addTimeTable(final User user, final AddTimeTableRequest addTimeTableRequest);
    void removeTimeTable(final User user, final RemoveTimeTableRequest removeTimeTableRequest);
    DailyPlanner getOrCreateDailyPlanner(final User user, final String date);
    DailyPlanner getOrExceptionDailyPlanner(final User user, final String date);
    DailyPlanner getDailyPlanner(final User user, final String date);
    void changeDailyTodoSequence(final User user, final ChangeDailyTodoSequenceRequest changeDailyTodoSequenceRequest);
    long getRoutineCount(final Category category);
    List<DailyPlanner> getDailyPlannerList(final User user);
    WeeklyPlannerDailyResponse getWeeklyPlannerDailyResponse(final User plannerWriter, final String date);
    SearchDailyPlannerResponse searchDailyPlanner(final User user, final User plannerWriter, final String date, final DailyPlanner dailyPlanner);
    int countTodo(final DailyPlanner dailyPlanner);
    int countTodoComplete(final DailyPlanner dailyPlanner);
}
