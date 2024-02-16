package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.dto.response.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.response.AddTimeTableResponse;
import com.newsainturtle.shadowmate.planner.dto.response.TodoIndexResponse;
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
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineTodo;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineTodoRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyPlannerServiceImpl extends DateCommonService implements DailyPlannerService {

    private final DailyPlannerRepository dailyPlannerRepository;
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final DailyPlannerLikeRepository dailyPlannerLikeRepository;
    private final TimeTableRepository timeTableRepository;
    private final UserRepository userRepository;
    private final RoutineTodoRepository routineTodoRepository;

    @Override
    public DailyPlanner getOrCreateDailyPlanner(final User user, final String date) {
        final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, date);
        if (dailyPlanner == null) {
            return dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(date)
                    .user(user)
                    .build());
        }
        return dailyPlanner;
    }

    @Override
    @Transactional(readOnly = true)
    public DailyPlanner getDailyPlanner(final User user, final String date) {
        final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, date);
        if (dailyPlanner == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }
        return dailyPlanner;
    }

    private Category getCategory(final User user, final Long categoryId) {
        if (categoryId == 0)
            return null;
        final Category category = categoryRepository.findByUserAndId(user, categoryId);
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

    private DailyPlanner getAnotherUserDailyPlanner(final User user, final Long plannerWriterId, final String date) {
        if (user.getId().equals(plannerWriterId)) {
            throw new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
        }
        final User plannerWriter = certifyPlannerWriter(plannerWriterId);
        return getDailyPlanner(plannerWriter, date);
    }

    private void checkValidDateTime(final String date, final LocalDateTime startTime, final LocalDateTime endTime) {
        final LocalDateTime localDateTime = stringToLocalDateTime(date + " 00:00");
        if (endTime.isBefore(startTime)
                || localDateTime.withHour(4).isAfter(startTime)
                || localDateTime.plusDays(1).withHour(4).isBefore(endTime)) {
            throw new PlannerException(PlannerErrorResult.INVALID_TIME);
        }
    }

    private User certifyPlannerWriter(final long plannerWriterId) {
        final User plannerWriter = userRepository.findByIdAndWithdrawalIsFalse(plannerWriterId);
        if (plannerWriter == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_USER);
        }
        return plannerWriter;
    }

    @Override
    public AddDailyTodoResponse addDailyTodo(final User user, final AddDailyTodoRequest addDailyTodoRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, addDailyTodoRequest.getDate());
        final Category category = getCategory(user, addDailyTodoRequest.getCategoryId());
        final TodoIndexResponse lastTodoIndex = todoRepository.findTopByDailyPlannerOrderByTodoIndexDesc(dailyPlanner);
        final long todoId = todoRepository.save(Todo.builder()
                .category(category)
                .todoContent(addDailyTodoRequest.getTodoContent())
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .todoIndex(lastTodoIndex == null ? 100000 : lastTodoIndex.getTodoIndex() + 100000)
                .timeTables(new ArrayList<>())
                .build()).getId();
        return AddDailyTodoResponse.builder().todoId(todoId).build();
    }

    @Override
    public void updateDailyTodo(final User user, final UpdateDailyTodoRequest updateDailyTodoRequest) {
        final TodoStatus status = TodoStatus.parsing(updateDailyTodoRequest.getTodoStatus());
        if (status == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TODO_STATUS);
        }
        final DailyPlanner dailyPlanner = getDailyPlanner(user, updateDailyTodoRequest.getDate());
        final Category category = getCategory(user, updateDailyTodoRequest.getCategoryId());
        final Todo todo = getTodo(updateDailyTodoRequest.getTodoId(), dailyPlanner);
        if ((status.equals(TodoStatus.EMPTY) || status.equals(TodoStatus.INCOMPLETE)) &&
                (todo.getTodoStatus().equals(TodoStatus.INPROGRESS)
                        || todo.getTodoStatus().equals(TodoStatus.COMPLETE))) {
            timeTableRepository.deleteAllByTodoId(todo.getId());
            todo.clearTimeTables();
        }
        todoRepository.updateAllByTodoId(updateDailyTodoRequest.getTodoContent(), category, status, LocalDateTime.now(),
                todo.getId());
    }

    @Override
    public void removeDailyTodo(final User user, final RemoveDailyTodoRequest removeDailyTodoRequest) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, removeDailyTodoRequest.getDate());
        final Todo todo = getTodo(removeDailyTodoRequest.getTodoId(), dailyPlanner);
        final RoutineTodo routineTodo = routineTodoRepository.findByTodo(todo);
        if (routineTodo != null) {
            routineTodo.setRoutine(null);
            routineTodoRepository.deleteById(routineTodo.getId());
        }
        todoRepository.deleteById(todo.getId());
    }

    @Override
    public void updateTodayGoal(final User user, final UpdateTodayGoalRequest updateTodayGoalRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateTodayGoalRequest.getDate());
        dailyPlanner.updateTodayGoal(updateTodayGoalRequest.getTodayGoal());
    }

    @Override
    public void updateTomorrowGoal(final User user, final UpdateTomorrowGoalRequest updateTomorrowGoalRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateTomorrowGoalRequest.getDate());
        dailyPlanner.updateTomorrowGoal(updateTomorrowGoalRequest.getTomorrowGoal());
    }

    @Override
    public void updateRetrospection(final User user, final UpdateRetrospectionRequest updateRetrospectionRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateRetrospectionRequest.getDate());
        dailyPlanner.updateRetrospection(updateRetrospectionRequest.getRetrospection());
    }

    @Override
    public void updateRetrospectionImage(final User user,
                                         final UpdateRetrospectionImageRequest updateRetrospectionImageRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateRetrospectionImageRequest.getDate());
        dailyPlanner.updateRetrospectionImage(updateRetrospectionImageRequest.getRetrospectionImage());
    }

    @Override
    public void addDailyLike(final User user, final Long plannerWriterId,
                             final AddDailyLikeRequest addDailyPlannerLikeRequest) {
        final DailyPlanner dailyPlanner = getAnotherUserDailyPlanner(user, plannerWriterId,
                addDailyPlannerLikeRequest.getDate());
        if (dailyPlannerLikeRepository.existsByUserAndDailyPlanner(user, dailyPlanner)) {
            throw new PlannerException(PlannerErrorResult.ALREADY_ADDED_LIKE);
        }
        dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                .dailyPlanner(dailyPlanner)
                .user(user)
                .build());
    }

    @Override
    public void removeDailyLike(final User user, final Long plannerWriterId,
                                final RemoveDailyLikeRequest removeDailyLikeRequest) {
        final DailyPlanner dailyPlanner = getAnotherUserDailyPlanner(user, plannerWriterId,
                removeDailyLikeRequest.getDate());
        dailyPlannerLikeRepository.deleteByUserAndDailyPlanner(user, dailyPlanner);
    }

    @Override
    public AddTimeTableResponse addTimeTable(final User user, final AddTimeTableRequest addTimeTableRequest) {
        final LocalDateTime startTime = stringToLocalDateTime(addTimeTableRequest.getStartTime());
        final LocalDateTime endTime = stringToLocalDateTime(addTimeTableRequest.getEndTime());
        checkValidDateTime(addTimeTableRequest.getDate(), startTime, endTime);

        final DailyPlanner dailyPlanner = getDailyPlanner(user, addTimeTableRequest.getDate());
        final Todo todo = getTodo(addTimeTableRequest.getTodoId(), dailyPlanner);
        if (todo.getTodoStatus().equals(TodoStatus.EMPTY) || todo.getTodoStatus().equals(TodoStatus.INCOMPLETE)) {
            throw new PlannerException(PlannerErrorResult.FAILED_ADDED_TIMETABLE);
        }

        final TimeTable timeTable = timeTableRepository.save(TimeTable.builder()
                .endTime(endTime)
                .startTime(startTime)
                .build());
        timeTable.setTodo(todo);
        return AddTimeTableResponse.builder().timeTableId(timeTable.getId()).build();
    }

    @Override
    public void removeTimeTable(final User user, final RemoveTimeTableRequest removeTimeTableRequest) {
        getDailyPlanner(user, removeTimeTableRequest.getDate());
        final TimeTable findTimeTable = timeTableRepository.findByIdAndTodoId(removeTimeTableRequest.getTimeTableId(),
                removeTimeTableRequest.getTodoId());
        if (findTimeTable == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TIMETABLE);
        }
        findTimeTable.setTodo(null);
        timeTableRepository.deleteById(removeTimeTableRequest.getTimeTableId());
    }

    @Override
    public void changeDailyTodoSequence(final User user,
                                        final ChangeDailyTodoSequenceRequest changeDailyTodoSequenceRequest) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, changeDailyTodoSequenceRequest.getDate());
        final Todo todo = getTodo(changeDailyTodoSequenceRequest.getTodoId(), dailyPlanner);

        if (changeDailyTodoSequenceRequest.getUpperTodoId() == null) {
            final TodoIndexResponse lowerTodoIndex = todoRepository
                    .findTopByDailyPlannerAndTodoIndexGreaterThanOrderByTodoIndex(dailyPlanner, 0);
            if (lowerTodoIndex == null) {
                throw new PlannerException(PlannerErrorResult.INVALID_TODO);
            } else {
                todo.updateTodoIndex(lowerTodoIndex.getTodoIndex() / 2);
            }
        } else {
            final double upperTodoIndex = getTodo(changeDailyTodoSequenceRequest.getUpperTodoId(), dailyPlanner)
                    .getTodoIndex();
            final TodoIndexResponse lowerTodoIndex = todoRepository
                    .findTopByDailyPlannerAndTodoIndexGreaterThanOrderByTodoIndex(dailyPlanner, upperTodoIndex);
            if (lowerTodoIndex == null) {
                todo.updateTodoIndex(upperTodoIndex + 100000);
            } else {
                todo.updateTodoIndex((upperTodoIndex + lowerTodoIndex.getTodoIndex()) / 2);
            }
        }

    }

    @Override
    @Transactional(readOnly = true)
    public long getRoutineCount(final Category category) {
        return todoRepository.countByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyPlanner> getDailyPlannerList(final User user) {
        return dailyPlannerRepository.findAllByUser(user);
    }
}
