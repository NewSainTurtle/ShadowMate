package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.dto.response.*;
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
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.newsainturtle.shadowmate.common.constant.CommonConstant.DATE_PATTERN;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyPlannerServiceImpl extends DateCommonService implements DailyPlannerService {

    private final DailyPlannerRepository dailyPlannerRepository;
    private final TodoRepository todoRepository;
    private final DailyPlannerLikeRepository dailyPlannerLikeRepository;
    private final TimeTableRepository timeTableRepository;
    private int totalMinutes;

    private DailyPlanner getAnotherUserDailyPlanner(final User user, final User plannerWriter, final String date) {
        if (user.getId().equals(plannerWriter.getId())) {
            throw new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
        }
        return getOrExceptionDailyPlanner(plannerWriter, date);
    }

    private void checkValidDateTime(final String date, final LocalDateTime startTime, final LocalDateTime endTime) {
        final LocalDateTime localDateTime = stringToLocalDateTime(date + " 00:00");
        if (endTime.isBefore(startTime)
                || localDateTime.withHour(4).isAfter(startTime)
                || localDateTime.plusDays(1).withHour(4).isBefore(endTime)) {
            throw new PlannerException(PlannerErrorResult.INVALID_TIME);
        }
    }

    private List<DailyPlannerTodoTimeTableResponse> getTimeTable(final Todo todo) {
        final List<DailyPlannerTodoTimeTableResponse> timeTables = new ArrayList<>();
        for (TimeTable timeTable : todo.getTimeTables()) {
            totalMinutes += ChronoUnit.MINUTES.between(timeTable.getStartTime(), timeTable.getEndTime());
            timeTables.add(DailyPlannerTodoTimeTableResponse.builder()
                    .timeTableId(timeTable.getId())
                    .startTime(localDateTimeToString(timeTable.getStartTime()))
                    .endTime(localDateTimeToString(timeTable.getEndTime()))
                    .build());
        }

        return timeTables;
    }

    private List<WeeklyPlannerDailyTodoResponse> getDayTodo(final DailyPlanner dailyPlanner) {
        final List<WeeklyPlannerDailyTodoResponse> dailyTodos = new ArrayList<>();
        final List<Todo> todoList = todoRepository.findAllByDailyPlannerOrderByTodoIndex(dailyPlanner);
        for (Todo todo : todoList) {
            dailyTodos.add(WeeklyPlannerDailyTodoResponse.builder()
                    .todoId(todo.getId())
                    .category(todo.getCategory() != null ? DailyPlannerTodoCategoryResponse.builder()
                            .categoryId(todo.getCategory().getId())
                            .categoryTitle(todo.getCategory().getCategoryTitle())
                            .categoryColorCode(todo.getCategory().getCategoryColor().getCategoryColorCode())
                            .categoryEmoticon(todo.getCategory().getCategoryEmoticon())
                            .build() : null)
                    .todoContent(todo.getTodoContent())
                    .todoStatus(todo.getTodoStatus().getStatus())
                    .build());
        }
        return dailyTodos;
    }

    @Override
    public void checkValidDate(final String date) {
        if (!Pattern.matches(DATE_PATTERN, date)) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT);
        }
    }

    @Override
    @Transactional
    public DailyPlanner getOrCreateDailyPlanner(final User user, final String date) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, date);
        if (dailyPlanner == null) {
            return dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(date)
                    .user(user)
                    .build());
        }
        return dailyPlanner;
    }

    @Override
    public DailyPlanner getOrExceptionDailyPlanner(final User user, final String date) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, date);
        if (dailyPlanner == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }
        return dailyPlanner;
    }

    @Override
    public DailyPlanner getDailyPlanner(final User user, final String date) {
        return dailyPlannerRepository.findByUserAndDailyPlannerDay(user, date);
    }

    @Override
    public Todo getTodo(final Long todoId, final DailyPlanner dailyPlanner) {
        final Todo todo = todoRepository.findByIdAndDailyPlanner(todoId, dailyPlanner);
        if (todo == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TODO);
        }
        return todo;
    }

    @Override
    @Transactional
    public AddDailyTodoResponse addDailyTodo(final User user, final Category category, final AddDailyTodoRequest addDailyTodoRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, addDailyTodoRequest.getDate());
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
    @Transactional
    public void updateDailyTodo(final User user, final Category category, final UpdateDailyTodoRequest updateDailyTodoRequest) {
        final TodoStatus status = TodoStatus.parsing(updateDailyTodoRequest.getTodoStatus());
        if (status == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TODO_STATUS);
        }
        final DailyPlanner dailyPlanner = getOrExceptionDailyPlanner(user, updateDailyTodoRequest.getDate());
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
    @Transactional
    public void removeDailyTodo(final long todoId) {
        todoRepository.deleteById(todoId);
    }

    @Override
    @Transactional
    public void updateTodayGoal(final User user, final UpdateTodayGoalRequest updateTodayGoalRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateTodayGoalRequest.getDate());
        dailyPlanner.updateTodayGoal(updateTodayGoalRequest.getTodayGoal());
    }

    @Override
    @Transactional
    public void updateTomorrowGoal(final User user, final UpdateTomorrowGoalRequest updateTomorrowGoalRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateTomorrowGoalRequest.getDate());
        dailyPlanner.updateTomorrowGoal(updateTomorrowGoalRequest.getTomorrowGoal());
    }

    @Override
    @Transactional
    public void updateRetrospection(final User user, final UpdateRetrospectionRequest updateRetrospectionRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateRetrospectionRequest.getDate());
        dailyPlanner.updateRetrospection(updateRetrospectionRequest.getRetrospection());
    }

    @Override
    @Transactional
    public void updateRetrospectionImage(final User user,
                                         final UpdateRetrospectionImageRequest updateRetrospectionImageRequest) {
        final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, updateRetrospectionImageRequest.getDate());
        dailyPlanner.updateRetrospectionImage(updateRetrospectionImageRequest.getRetrospectionImage());
    }

    @Override
    @Transactional
    public void addDailyLike(final User user, final User plannerWriter, final AddDailyLikeRequest addDailyPlannerLikeRequest) {
        final DailyPlanner dailyPlanner = getAnotherUserDailyPlanner(user, plannerWriter, addDailyPlannerLikeRequest.getDate());
        if (dailyPlannerLikeRepository.existsByUserAndDailyPlanner(user, dailyPlanner)) {
            throw new PlannerException(PlannerErrorResult.ALREADY_ADDED_LIKE);
        }
        dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                .dailyPlanner(dailyPlanner)
                .user(user)
                .build());
    }

    @Override
    @Transactional
    public void removeDailyLike(final User user, final User plannerWriter, final RemoveDailyLikeRequest removeDailyLikeRequest) {
        final DailyPlanner dailyPlanner = getAnotherUserDailyPlanner(user, plannerWriter, removeDailyLikeRequest.getDate());
        dailyPlannerLikeRepository.deleteByUserAndDailyPlanner(user, dailyPlanner);
    }

    @Override
    @Transactional
    public AddTimeTableResponse addTimeTable(final User user, final AddTimeTableRequest addTimeTableRequest) {
        final LocalDateTime startTime = stringToLocalDateTime(addTimeTableRequest.getStartTime());
        final LocalDateTime endTime = stringToLocalDateTime(addTimeTableRequest.getEndTime());
        checkValidDateTime(addTimeTableRequest.getDate(), startTime, endTime);

        final DailyPlanner dailyPlanner = getOrExceptionDailyPlanner(user, addTimeTableRequest.getDate());
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
    @Transactional
    public void removeTimeTable(final User user, final RemoveTimeTableRequest removeTimeTableRequest) {
        getOrExceptionDailyPlanner(user, removeTimeTableRequest.getDate());
        final TimeTable findTimeTable = timeTableRepository.findByIdAndTodoId(removeTimeTableRequest.getTimeTableId(),
                removeTimeTableRequest.getTodoId());
        if (findTimeTable == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TIMETABLE);
        }
        findTimeTable.setTodo(null);
        timeTableRepository.deleteById(removeTimeTableRequest.getTimeTableId());
    }

    @Override
    @Transactional
    public void changeDailyTodoSequence(final User user,
                                        final ChangeDailyTodoSequenceRequest changeDailyTodoSequenceRequest) {
        final DailyPlanner dailyPlanner = getOrExceptionDailyPlanner(user, changeDailyTodoSequenceRequest.getDate());
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
    public long getRoutineCount(final Category category) {
        return todoRepository.countByCategory(category);
    }

    @Override
    public List<DailyPlanner> getDailyPlannerList(final User user) {
        return dailyPlannerRepository.findAllByUser(user);
    }

    @Override
    public long getMonthlyLikeCount(final List<Long> dailyPlannerIdList) {
        return dailyPlannerLikeRepository.countByDailyPlannerIdIn(dailyPlannerIdList);
    }

    @Override
    public WeeklyPlannerDailyResponse getWeeklyPlannerDailyResponse(final User plannerWriter, final String date) {
        final DailyPlanner dailyPlanner = getDailyPlanner(plannerWriter, date);
        if (dailyPlanner == null) {
            return WeeklyPlannerDailyResponse.builder()
                    .date(date)
                    .retrospection(null)
                    .dailyTodos(new ArrayList<>())
                    .build();
        }
        return WeeklyPlannerDailyResponse.builder()
                .date(date)
                .retrospection(dailyPlanner.getRetrospection())
                .dailyTodos(getDayTodo(dailyPlanner))
                .build();
    }

    @Override
    public SearchDailyPlannerResponse searchDailyPlanner(final User user, final User plannerWriter, final String date, final DailyPlanner dailyPlanner) {
        final List<Todo> todoList = todoRepository.findAllByDailyPlannerOrderByTodoIndex(dailyPlanner);
        final List<DailyPlannerTodoResponse> dailyTodos = new ArrayList<>();
        totalMinutes = 0;
        for (Todo todo : todoList) {
            dailyTodos.add(DailyPlannerTodoResponse.builder()
                    .todoId(todo.getId())
                    .category(todo.getCategory() != null ? DailyPlannerTodoCategoryResponse.builder()
                            .categoryId(todo.getCategory().getId())
                            .categoryTitle(todo.getCategory().getCategoryTitle())
                            .categoryColorCode(todo.getCategory().getCategoryColor().getCategoryColorCode())
                            .categoryEmoticon(todo.getCategory().getCategoryEmoticon())
                            .build() : null)
                    .todoContent(todo.getTodoContent())
                    .todoStatus(todo.getTodoStatus().getStatus())
                    .timeTables(getTimeTable(todo))
                    .build());
        }

        return SearchDailyPlannerResponse.builder()
                .date(date)
                .plannerAccessScope(plannerWriter.getPlannerAccessScope().getScope())
                .todayGoal(dailyPlanner.getTodayGoal())
                .retrospection(dailyPlanner.getRetrospection())
                .retrospectionImage(dailyPlanner.getRetrospectionImage())
                .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                .like(dailyPlannerLikeRepository.existsByUserAndDailyPlanner(user, dailyPlanner))
                .likeCount(dailyPlannerLikeRepository.countByDailyPlanner(dailyPlanner))
                .studyTimeHour(totalMinutes / 60)
                .studyTimeMinute(totalMinutes % 60)
                .dailyTodos(dailyTodos)
                .build();
    }

    @Override
    public int countTodo(final DailyPlanner dailyPlanner) {
        return todoRepository.countByDailyPlanner(dailyPlanner);
    }

    @Override
    public int countTodoComplete(final DailyPlanner dailyPlanner) {
        return todoRepository.countByDailyPlannerAndTodoStatusNot(dailyPlanner, TodoStatus.COMPLETE);
    }
}
