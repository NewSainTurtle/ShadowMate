package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.AddDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.response.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingService;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SettingPlannerServiceImpl implements SettingPlannerService {

    private final DailyPlannerService dailyPlannerService;
    private final PlannerSettingService plannerSettingService;

    @Override
    public AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest) {
        return dailyPlannerService.addDailyTodo(user, plannerSettingService.getCategory(user, addDailyTodoRequest.getCategoryId()), addDailyTodoRequest);
    }

    @Override
    public void updateDailyTodo(final User user, final UpdateDailyTodoRequest updateDailyTodoRequest) {
        dailyPlannerService.updateDailyTodo(user, plannerSettingService.getCategory(user, updateDailyTodoRequest.getCategoryId()), updateDailyTodoRequest);
    }
}
