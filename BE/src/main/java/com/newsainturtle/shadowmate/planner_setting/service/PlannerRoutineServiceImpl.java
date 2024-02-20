package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerService;
import com.newsainturtle.shadowmate.planner_setting.dto.request.AddRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.RemoveCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.UpdateRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.response.AddRoutineResponse;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class PlannerRoutineServiceImpl extends DateCommonService implements PlannerRoutineService {

    private final DailyPlannerService dailyPlannerService;
    private final PlannerSettingService plannerSettingService;
    private final RoutineService routineService;

    private void checkValidDay(final String startDateStr, final String endDateStr) {
        if (ChronoUnit.DAYS.between(stringToLocalDate(startDateStr), stringToLocalDate(endDateStr)) < 0) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_DATE);
        }
    }

    @Override
    public void removeCategory(final User user, final RemoveCategoryRequest removeCategoryRequest) {
        final Category category = plannerSettingService.getCategory(user, removeCategoryRequest.getCategoryId());
        if (category == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY);
        }
        plannerSettingService.removeCategory(user, category, dailyPlannerService.getRoutineCount(category) + routineService.getRoutineCount(category));
    }

    @Override
    public AddRoutineResponse addRoutine(final User user, final AddRoutineRequest addRoutineRequest) {
        checkValidDay(addRoutineRequest.getStartDay(), addRoutineRequest.getEndDay());
        final Category category = plannerSettingService.getCategory(user, addRoutineRequest.getCategoryId());
        return routineService.addRoutine(user, category, addRoutineRequest);
    }

    @Override
    public void updateRoutine(final User user, final UpdateRoutineRequest updateRoutineRequest) {
        checkValidDay(updateRoutineRequest.getStartDay(), updateRoutineRequest.getEndDay());
        final Category category = plannerSettingService.getCategory(user, updateRoutineRequest.getCategoryId());
        routineService.updateRoutine(user, category, updateRoutineRequest);
    }

    @Override
    public void removeDailyTodo(final User user, final RemoveDailyTodoRequest removeDailyTodoRequest) {
        final DailyPlanner dailyPlanner = dailyPlannerService.getOrExceptionDailyPlanner(user, removeDailyTodoRequest.getDate());
        final Todo todo = dailyPlannerService.getTodo(removeDailyTodoRequest.getTodoId(), dailyPlanner);
        routineService.removeRoutineTodo(todo);
        dailyPlannerService.removeDailyTodo(todo.getId());
    }

}