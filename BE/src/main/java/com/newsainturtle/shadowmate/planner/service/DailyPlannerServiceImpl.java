package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
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
import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyPlannerServiceImpl implements DailyPlannerService {

    private final DailyPlannerRepository dailyPlannerRepository;
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final DailyPlannerLikeRepository dailyPlannerLikeRepository;
    private final TimeTableRepository timeTableRepository;
    private final UserRepository userRepository;
    private final DdayRepository ddayRepository;
    private final FollowRepository followRepository;

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
        final Todo todo = getTodo(removeDailyTodoRequest.getTodoId(), dailyPlanner);
        todoRepository.deleteByIdAndDailyPlanner(todo.getId(), dailyPlanner);
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

    private DailyPlanner getAnotherUserDailyPlanner(final User user, final Long plannerWriterId, final String date) {
        if (user.getId().equals(plannerWriterId)) {
            throw new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
        }

        final User plannerWriter = userRepository.findByIdAndWithdrawalIsFalse(plannerWriterId);
        if (plannerWriter == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_USER);
        }

        final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(plannerWriter, Date.valueOf(date));
        if (dailyPlanner == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }
        return dailyPlanner;
    }

    @Override
    @Transactional
    public void addDailyLike(final User user, final Long plannerWriterId, final AddDailyLikeRequest addDailyPlannerLikeRequest) {
        final DailyPlanner dailyPlanner = getAnotherUserDailyPlanner(user, plannerWriterId, addDailyPlannerLikeRequest.getDate());
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
    public void removeDailyLike(final User user, final Long plannerWriterId, final RemoveDailyLikeRequest removeDailyLikeRequest) {
        final DailyPlanner dailyPlanner = getAnotherUserDailyPlanner(user, plannerWriterId, removeDailyLikeRequest.getDate());
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
    public void addTimeTable(final User user, final AddTimeTableRequest addTimeTableRequest) {
        LocalDateTime startTime = stringToLocalDateTime(addTimeTableRequest.getStartTime());
        LocalDateTime endTime = stringToLocalDateTime(addTimeTableRequest.getEndTime());
        checkValidDateTime(addTimeTableRequest.getDate(), startTime, endTime);

        final DailyPlanner dailyPlanner = getDailyPlanner(user, addTimeTableRequest.getDate());
        final Todo todo = getTodo(addTimeTableRequest.getTodoId(), dailyPlanner);

        if (todo.getTimeTable() != null) {
            throw new PlannerException(PlannerErrorResult.ALREADY_ADDED_TIME_TABLE);
        }

        todo.setTimeTable(TimeTable.builder()
                .endTime(endTime)
                .startTime(startTime)
                .build());
    }

    @Override
    @Transactional
    public void removeTimeTable(final User user, final RemoveTimeTableRequest removeTimeTableRequest) {
        final DailyPlanner dailyPlanner = getDailyPlanner(user, removeTimeTableRequest.getDate());
        final Todo todo = getTodo(removeTimeTableRequest.getTodoId(), dailyPlanner);
        if (todo.getTimeTable() == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TIME_TABLE);
        }
        final long timeTableId = todo.getTimeTable().getId();
        todo.setTimeTable(null);
        timeTableRepository.deleteById(timeTableId);
    }

    private String LocalDateTimeToString(final LocalDateTime time) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return time.format(formatter);
    }

    private String getDday(final User user) {
        final Date today = Date.valueOf(LocalDate.now());
        Dday dday = ddayRepository.findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(user, today);
        if (dday == null) dday = ddayRepository.findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(user, today);
        return dday == null ? null : dday.getDdayDate().toString();
    }

    private boolean havePermissionToSearch(final User user, final User plannerWriter) {
        if (user.equals(plannerWriter) ||
                plannerWriter.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC) ||
                (plannerWriter.getPlannerAccessScope().equals(PlannerAccessScope.FOLLOW) && followRepository.findByFollowerIdAndFollowingId(user, plannerWriter) != null)
        ) {
            return true;
        }
        return false;
    }

    @Override
    public SearchDailyPlannerResponse searchDailyPlanner(final User user, final Long plannerWriterId, final String date) {
        final String datePattern = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$";
        if (!Pattern.matches(datePattern, date)) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        final User plannerWriter = userRepository.findByIdAndWithdrawalIsFalse(plannerWriterId);
        if (plannerWriter == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_USER);
        }

        final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(plannerWriter, Date.valueOf(date));
        int totalMinutes = 0;

        if (dailyPlanner == null || !havePermissionToSearch(user, plannerWriter)) {
            return SearchDailyPlannerResponse.builder()
                    .date(date)
                    .plannerAccessScope(plannerWriter.getPlannerAccessScope().getScope())
                    .dday(getDday(user))
                    .build();
        } else {
            final boolean like = dailyPlannerLikeRepository.findByUserAndDailyPlanner(user, dailyPlanner) != null;
            final long likeCount = dailyPlannerLikeRepository.countByDailyPlanner(dailyPlanner);
            final List<Todo> todoList = todoRepository.findAllByDailyPlanner(dailyPlanner);

            final List<DailyPlannerTodo> dailyTodo = new ArrayList<>();
            for (Todo todo : todoList) {
                dailyTodo.add(DailyPlannerTodo.builder()
                        .todoId(todo.getId())
                        .category(todo.getCategory() != null ? DailyPlannerTodoCategory.builder()
                                .categoryId(todo.getCategory().getId())
                                .categoryTitle(todo.getCategory().getCategoryTitle())
                                .categoryColorCode(todo.getCategory().getCategoryColor().getCategoryColorCode())
                                .categoryEmoticon(todo.getCategory().getCategoryEmoticon())
                                .build() : null)
                        .todoContent(todo.getTodoContent())
                        .todoStatus(todo.getTodoStatus().getStatus())
                        .timeTable(todo.getTimeTable() != null ? DailyPlannerTodoTimeTable.builder()
                                .timeTableId(todo.getTimeTable().getId())
                                .startTime(LocalDateTimeToString(todo.getTimeTable().getStartTime()))
                                .endTime(LocalDateTimeToString(todo.getTimeTable().getEndTime()))
                                .build() : null)
                        .build());
                if (todo.getTimeTable() != null) {
                    totalMinutes += ChronoUnit.MINUTES.between(todo.getTimeTable().getStartTime(), todo.getTimeTable().getEndTime());
                }
            }

            return SearchDailyPlannerResponse.builder()
                    .date(date)
                    .plannerAccessScope(plannerWriter.getPlannerAccessScope().getScope())
                    .dday(getDday(user))
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .like(like)
                    .likeCount(likeCount)
                    .studyTimeHour(totalMinutes / 60)
                    .studyTimeMinute(totalMinutes % 60)
                    .dailyTodo(dailyTodo)
                    .build();

        }
    }
}
