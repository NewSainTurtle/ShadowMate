package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.*;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.DailyPlannerLike;
import com.newsainturtle.shadowmate.planner.entity.TimeTable;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerLikeRepository;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TimeTableRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyPlannerServiceImpl implements DailyPlannerService {

    private final DailyPlannerRepository dailyPlannerRepository;
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final DailyPlannerLikeRepository dailyPlannerLikeRepository;
    private final TimeTableRepository timeTableRepository;

    private DailyPlanner getOrCreateDailyPlanner(final User user, final String date) {
        DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf(date));
        if (dailyPlanner == null) {
            dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(Date.valueOf(date))
                    .user(user)
                    .build());
        }
        return dailyPlanner;
    }

    private DailyPlanner getDailyPlanner(final User user, final String date) {
        final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf(date));
        if (dailyPlanner == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER);
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

    private Todo getTodo(final Long todoId, final DailyPlanner dailyPlanner) {
        final Todo todo = todoRepository.findByIdAndDailyPlanner(todoId, dailyPlanner);
        if (todo == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TODO);
        }
        return todo;
    }

    @Override
    @Transactional
    public AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, addDailyTodoRequest.getDate());
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
    public void updateDailyTodo(final User user, final UpdateDailyTodoRequest updateDailyTodoRequest) {
        final TodoStatus status = TodoStatus.parsing(updateDailyTodoRequest.getTodoStatus());
        if (status == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TODO_STATUS);
        }
        final DailyPlanner dailyPlanner = getDailyPlanner(user, updateDailyTodoRequest.getDate());
        final Category category = getCategory(user, updateDailyTodoRequest.getCategoryId());
        final Todo todo = getTodo(updateDailyTodoRequest.getTodoId(), dailyPlanner);
        final Todo changeTodo = Todo.builder()
                .id(todo.getId())
                .createTime(todo.getCreateTime())
                .todoContent(updateDailyTodoRequest.getTodoContent())
                .category(category)
                .todoStatus(status)
                .dailyPlanner(todo.getDailyPlanner())
                .build();
        todoRepository.save(changeTodo);
    }

    @Override
    @Transactional
    public void removeDailyTodo(final User user, final RemoveDailyTodoRequest removeDailyTodoRequest) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, removeDailyTodoRequest.getDate());
        todoRepository.deleteByIdAndDailyPlanner(removeDailyTodoRequest.getTodoId(), dailyPlanner);
    }

    @Override
    @Transactional
    public void updateTodayGoal(final User user, final UpdateTodayGoalRequest updateTodayGoalRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateTodayGoalRequest.getDate());
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

    @Override
    @Transactional
    public void updateTomorrowGoal(final User user, final UpdateTomorrowGoalRequest updateTomorrowGoalRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateTomorrowGoalRequest.getDate());
        final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                .id(dailyPlanner.getId())
                .createTime(dailyPlanner.getCreateTime())
                .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                .user(dailyPlanner.getUser())
                .retrospection(dailyPlanner.getRetrospection())
                .retrospectionImage(dailyPlanner.getRetrospectionImage())
                .todayGoal(dailyPlanner.getTodayGoal())
                .tomorrowGoal(updateTomorrowGoalRequest.getTomorrowGoal())
                .build();
        dailyPlannerRepository.save(changeDailyPlanner);
    }

    @Override
    @Transactional
    public void updateRetrospection(final User user, final UpdateRetrospectionRequest updateRetrospectionRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateRetrospectionRequest.getDate());
        final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                .id(dailyPlanner.getId())
                .createTime(dailyPlanner.getCreateTime())
                .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                .user(dailyPlanner.getUser())
                .retrospectionImage(dailyPlanner.getRetrospectionImage())
                .todayGoal(dailyPlanner.getTodayGoal())
                .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                .retrospection(updateRetrospectionRequest.getRetrospection())
                .build();
        dailyPlannerRepository.save(changeDailyPlanner);
    }

    @Override
    @Transactional
    public void updateRetrospectionImage(final User user, final UpdateRetrospectionImageRequest updateRetrospectionImageRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateRetrospectionImageRequest.getDate());
        final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                .id(dailyPlanner.getId())
                .createTime(dailyPlanner.getCreateTime())
                .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                .user(dailyPlanner.getUser())
                .todayGoal(dailyPlanner.getTodayGoal())
                .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                .retrospection(dailyPlanner.getRetrospection())
                .retrospectionImage(updateRetrospectionImageRequest.getRetrospectionImage())
                .build();
        dailyPlannerRepository.save(changeDailyPlanner);
    }

    private DailyPlanner getAnotherUserDailyPlanner(final User user, final Long anotherUserId, final String date) {
        if (user.getId().equals(anotherUserId)) {
            throw new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
        }
        DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserIdAndDailyPlannerDay(anotherUserId, Date.valueOf(date));
        if (dailyPlanner == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }
        return dailyPlanner;
    }

    @Override
    @Transactional
    public void addDailyLike(final User user, final AddDailyLikeRequest addDailyPlannerLikeRequest) {
        final DailyPlanner dailyPlanner = getAnotherUserDailyPlanner(user, addDailyPlannerLikeRequest.getAnotherUserId(),
                addDailyPlannerLikeRequest.getDate());
        DailyPlannerLike dailyPlannerLike = dailyPlannerLikeRepository.findByUserAndDailyPlanner(user, dailyPlanner);
        if (dailyPlannerLike != null) {
            throw new PlannerException(PlannerErrorResult.ALREADY_ADDED_LIKE);
        }
        dailyPlannerLike = DailyPlannerLike.builder()
                .dailyPlanner(dailyPlanner)
                .user(user)
                .build();
        dailyPlannerLikeRepository.save(dailyPlannerLike);
    }

    @Override
    @Transactional
    public void removeDailyLike(final User user, final RemoveDailyLikeRequest removeDailyLikeRequest) {
        final DailyPlanner dailyPlanner = getAnotherUserDailyPlanner(user, removeDailyLikeRequest.getAnotherUserId(),
                removeDailyLikeRequest.getDate());
        dailyPlannerLikeRepository.deleteByUserAndDailyPlanner(user, dailyPlanner);
    }

    private LocalDateTime stringToLocalDateTime(String timeStr) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(timeStr, formatter);
    }

    private void checkValidDateTime(String date, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime localDateTime = stringToLocalDateTime(date + " 00:00");
        if (endTime.isBefore(startTime)
                || localDateTime.withHour(4).isAfter(startTime)
                || localDateTime.plusDays(1).withHour(4).isBefore(endTime)) {
            throw new PlannerException(PlannerErrorResult.INVALID_TIME);
        }
    }

    @Override
    @Transactional
    public AddTimeTableResponse addTimeTable(final User user, final AddTimeTableRequest addTimeTableRequest) {
        LocalDateTime startTime = stringToLocalDateTime(addTimeTableRequest.getStartTime());
        LocalDateTime endTime = stringToLocalDateTime(addTimeTableRequest.getEndTime());
        checkValidDateTime(addTimeTableRequest.getDate(), startTime, endTime);

        final DailyPlanner dailyPlanner = getDailyPlanner(user, addTimeTableRequest.getDate());
        final Todo todo = getTodo(addTimeTableRequest.getTodoId(), dailyPlanner);

        TimeTable timeTable = timeTableRepository.findByTodo(todo);
        if (timeTable != null) {
            throw new PlannerException(PlannerErrorResult.ALREADY_ADDED_TIME_TABLE);
        }
        timeTable = TimeTable.builder()
                .todo(todo)
                .endTime(endTime)
                .startTime(startTime)
                .build();
        TimeTable saveTimeTable = timeTableRepository.save(timeTable);
        return AddTimeTableResponse.builder().timeTableId(saveTimeTable.getId()).build();
    }

    @Override
    @Transactional
    public void removeTimeTable(final User user, final RemoveTimeTableRequest removeTimeTableRequest) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, removeTimeTableRequest.getDate());
        final TimeTable timeTable = timeTableRepository.findById(removeTimeTableRequest.getTimeTableId()).orElse(null);
        if (timeTable == null || !dailyPlanner.equals(timeTable.getTodo().getDailyPlanner())) {
            throw new PlannerException(PlannerErrorResult.INVALID_TIME_TABLE);
        }
        timeTableRepository.deleteById(removeTimeTableRequest.getTimeTableId());
    }
}
