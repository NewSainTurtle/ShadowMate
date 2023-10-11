package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.planner.dto.*;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.entity.Weekly;
import com.newsainturtle.shadowmate.planner.entity.WeeklyTodo;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner.repository.WeeklyRepository;
import com.newsainturtle.shadowmate.planner.repository.WeeklyTodoRepository;
import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyPlannerServiceImpl implements WeeklyPlannerService {

    private final WeeklyRepository weeklyRepository;
    private final WeeklyTodoRepository weeklyTodoRepository;
    private final UserRepository userRepository;
    private final DdayRepository ddayRepository;
    private final FollowRepository followRepository;
    private final DailyPlannerRepository dailyPlannerRepository;
    private final TodoRepository todoRepository;

    private Weekly getOrCreateWeeklyPlanner(final User user, final String startDateStr, final String endDateStr) {
        Date startDate = Date.valueOf(startDateStr);
        Date endDate = Date.valueOf(endDateStr);
        if (stringToLocalDate(startDateStr).getDayOfWeek().getValue() != 1 || Period.between(startDate.toLocalDate(), endDate.toLocalDate()).getDays() != 6) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE);
        }
        Weekly weekly = weeklyRepository.findByUserAndStartDayAndEndDay(user, startDate, endDate);
        if (weekly == null) {
            weekly = weeklyRepository.save(Weekly.builder()
                    .startDay(startDate)
                    .endDay(endDate)
                    .user(user)
                    .build());
        }
        return weekly;
    }

    private WeeklyTodo getWeeklyTodo(final User user, final String startDateStr, final String endDateStr, final Long weeklyTodoId) {
        final Weekly weekly = getOrCreateWeeklyPlanner(user, startDateStr, endDateStr);
        final WeeklyTodo weeklyTodo = weeklyTodoRepository.findByIdAndWeekly(weeklyTodoId, weekly);
        if (weeklyTodo == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TODO);
        }
        return weeklyTodo;
    }

    @Override
    @Transactional
    public AddWeeklyTodoResponse addWeeklyTodo(final User user, final AddWeeklyTodoRequest addWeeklyTodoRequest) {
        final Weekly weekly = getOrCreateWeeklyPlanner(user, addWeeklyTodoRequest.getStartDate(), addWeeklyTodoRequest.getEndDate());
        final WeeklyTodo weeklyTodo = weeklyTodoRepository.save(WeeklyTodo.builder()
                .weekly(weekly)
                .weeklyTodoContent(addWeeklyTodoRequest.getWeeklyTodoContent())
                .weeklyTodoStatus(false)
                .build());
        return AddWeeklyTodoResponse.builder().weeklyTodoId(weeklyTodo.getId()).build();
    }

    @Override
    @Transactional
    public void updateWeeklyTodoContent(final User user, final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest) {
        final WeeklyTodo weeklyTodo = getWeeklyTodo(user, updateWeeklyTodoContentRequest.getStartDate(), updateWeeklyTodoContentRequest.getEndDate(), updateWeeklyTodoContentRequest.getWeeklyTodoId());
        final WeeklyTodo changeWeeklyTodo = WeeklyTodo.builder()
                .id(weeklyTodo.getId())
                .createTime(weeklyTodo.getCreateTime())
                .weekly(weeklyTodo.getWeekly())
                .weeklyTodoContent(updateWeeklyTodoContentRequest.getWeeklyTodoContent())
                .weeklyTodoStatus(weeklyTodo.getWeeklyTodoStatus())
                .build();
        weeklyTodoRepository.save(changeWeeklyTodo);
    }

    @Override
    @Transactional
    public void updateWeeklyTodoStatus(final User user, final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest) {
        final WeeklyTodo weeklyTodo = getWeeklyTodo(user, updateWeeklyTodoStatusRequest.getStartDate(), updateWeeklyTodoStatusRequest.getEndDate(), updateWeeklyTodoStatusRequest.getWeeklyTodoId());
        final WeeklyTodo changeWeeklyTodo = WeeklyTodo.builder()
                .id(weeklyTodo.getId())
                .createTime(weeklyTodo.getCreateTime())
                .weekly(weeklyTodo.getWeekly())
                .weeklyTodoContent(weeklyTodo.getWeeklyTodoContent())
                .weeklyTodoStatus(updateWeeklyTodoStatusRequest.getWeeklyTodoStatus())
                .build();
        weeklyTodoRepository.save(changeWeeklyTodo);
    }

    @Override
    @Transactional
    public void removeWeeklyTodo(final User user, final RemoveWeeklyTodoRequest removeWeeklyTodoRequest) {
        final Weekly weekly = getOrCreateWeeklyPlanner(user, removeWeeklyTodoRequest.getStartDate(), removeWeeklyTodoRequest.getEndDate());
        weeklyTodoRepository.deleteByIdAndWeekly(removeWeeklyTodoRequest.getWeeklyTodoId(), weekly);
    }

    private String LocalDateToString(final LocalDate date) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    private LocalDate stringToLocalDate(final String dateStr) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateStr, formatter);
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

    private List<WeeklyPlannerTodoResponse> getWeeklyTodos(final User plannerWriter, final String startDate, final String endDate, final boolean permission) {
        final Weekly weekly = getOrCreateWeeklyPlanner(plannerWriter, startDate, endDate);
        final List<WeeklyTodo> weeklyTodoList = weeklyTodoRepository.findAllByWeekly(weekly);
        final List<WeeklyPlannerTodoResponse> weeklyTodos = new ArrayList<>();
        if (permission) {
            for (WeeklyTodo weeklyTodo : weeklyTodoList) {
                weeklyTodos.add(WeeklyPlannerTodoResponse.builder()
                        .weeklyTodoId(weeklyTodo.getId())
                        .weeklyTodoContent(weeklyTodo.getWeeklyTodoContent())
                        .weeklyTodoStatus(weeklyTodo.getWeeklyTodoStatus())
                        .build());
            }
        }
        return weeklyTodos;
    }

    private List<WeeklyPlannerDailyResponse> getDayList(final User plannerWriter, final LocalDate date, final boolean permission) {
        final List<WeeklyPlannerDailyResponse> dayList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(plannerWriter, Date.valueOf(date.plusDays(i)));
            final List<WeeklyPlannerDailyTodoResponse> dailyTodos = new ArrayList<>();
            if (dailyPlanner == null || !permission) {
                dayList.add(WeeklyPlannerDailyResponse.builder()
                        .date(LocalDateToString(date.plusDays(i)))
                        .retrospection(null)
                        .dailyTodos(dailyTodos)
                        .build());
            } else {
                final List<Todo> todoList = todoRepository.findAllByDailyPlanner(dailyPlanner);
                for (Todo todo : todoList) {
                    dailyTodos.add(WeeklyPlannerDailyTodoResponse.builder()
                            .todoId(todo.getId())
                            .category(todo.getCategory() != null ? DailyPlannerTodoCategory.builder()
                                    .categoryId(todo.getCategory().getId())
                                    .categoryTitle(todo.getCategory().getCategoryTitle())
                                    .categoryColorCode(todo.getCategory().getCategoryColor().getCategoryColorCode())
                                    .categoryEmoticon(todo.getCategory().getCategoryEmoticon())
                                    .build() : null)
                            .todoContent(todo.getTodoContent())
                            .todoStatus(todo.getTodoStatus().getStatus())
                            .build());
                }
                dayList.add(WeeklyPlannerDailyResponse.builder()
                        .date(LocalDateToString(date.plusDays(i)))
                        .retrospection(dailyPlanner.getRetrospection())
                        .dailyTodos(dailyTodos)
                        .build());
            }
        }
        return dayList;
    }

    @Override
    public SearchWeeklyPlannerResponse searchWeeklyPlanner(final User user, final Long plannerWriterId, final String startDate, final String endDate) {
        final String datePattern = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$";
        if (!Pattern.matches(datePattern, startDate) || !Pattern.matches(datePattern, endDate)) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        final User plannerWriter = userRepository.findByIdAndWithdrawalIsFalse(plannerWriterId);
        if (plannerWriter == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_USER);
        }

        final boolean permission = havePermissionToSearch(user, plannerWriter);
        final List<WeeklyPlannerTodoResponse> weeklyTodos = getWeeklyTodos(plannerWriter, startDate, endDate, permission);
        final List<WeeklyPlannerDailyResponse> dayList = getDayList(plannerWriter, stringToLocalDate(startDate), permission);

        return SearchWeeklyPlannerResponse.builder()
                .plannerAccessScope(plannerWriter.getPlannerAccessScope().getScope())
                .dday(getDday(user))
                .weeklyTodos(weeklyTodos)
                .dayList(dayList)
                .build();
    }

}
