package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.UpdateTodayGoalRequest;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyPlannerServiceImpl implements DailyPlannerService {

    private final DailyPlannerRepository dailyPlannerRepository;
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    private DailyPlanner getDailyPlanner(final User user, final String date) {
        DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf(date));
        if (dailyPlanner == null) {
            dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(Date.valueOf(date))
                    .user(user)
                    .build());
        }
        return dailyPlanner;
    }

    private Category getCategory(final User user, final Long categoryId) {
        if (categoryId == 0) return null;
        Category category = categoryRepository.findByUserAndId(user, categoryId);
        if (category == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_CATEGORY);
        }
        return category;
    }

    @Override
    @Transactional
    public AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, addDailyTodoRequest.getDate());
        final Category category = getCategory(user, addDailyTodoRequest.getCategoryId());
        final Todo todo = Todo.builder()
                .category(category)
                .todoContent(addDailyTodoRequest.getTodoContent())
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build();
        final Todo saveTodo = todoRepository.save(todo);
        return AddDailyTodoResponse.builder().todoId(saveTodo.getId()).build();
    }

    @Override
    @Transactional
    public void updateTodayGoal(final User user, final UpdateTodayGoalRequest updateTodayGoalRequest) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, updateTodayGoalRequest.getDate());
        final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                .id(dailyPlanner.getId())
                .createTime(dailyPlanner.getCreateTime())
                .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                .user(dailyPlanner.getUser())
                .retrospection(dailyPlanner.getRetrospection())
                .retrospectionImage(dailyPlanner.getRetrospectionImage())
                .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                .todayGoal(updateTodayGoalRequest.getTodayGoal())
                .build();
        dailyPlannerRepository.save(changeDailyPlanner);
    }
}
