package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.AddWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateWeeklyTodoContentRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateWeeklyTodoStatusRequest;
import com.newsainturtle.shadowmate.planner.dto.response.AddWeeklyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.response.WeeklyPlannerTodoResponse;
import com.newsainturtle.shadowmate.planner.entity.Weekly;
import com.newsainturtle.shadowmate.planner.entity.WeeklyTodo;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.WeeklyRepository;
import com.newsainturtle.shadowmate.planner.repository.WeeklyTodoRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyPlannerServiceImpl extends DateCommonService implements WeeklyPlannerService {

    private final WeeklyRepository weeklyRepository;
    private final WeeklyTodoRepository weeklyTodoRepository;

    private Weekly getOrCreateWeeklyPlanner(final User user, final String startDateStr, final String endDateStr) {
        checkValidWeek(startDateStr, endDateStr);
        if (stringToLocalDate(startDateStr).getDayOfWeek().getValue() != 1 || ChronoUnit.DAYS.between(stringToLocalDate(startDateStr), stringToLocalDate(endDateStr)) != 6) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE);
        }
        Weekly weekly = weeklyRepository.findByUserAndStartDayAndEndDay(user, startDateStr, endDateStr);
        if (weekly == null) {
            weekly = weeklyRepository.save(Weekly.builder()
                    .startDay(startDateStr)
                    .endDay(endDateStr)
                    .user(user)
                    .build());
        }
        return weekly;
    }

    private Weekly getWeeklyPlanner(final User user, final String startDateStr, final String endDateStr) {
        checkValidWeek(startDateStr, endDateStr);
        final Weekly weekly = weeklyRepository.findByUserAndStartDayAndEndDay(user, startDateStr, endDateStr);
        if (weekly == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_WEEKLY_PLANNER);
        }
        return weekly;
    }

    private WeeklyTodo getWeeklyTodo(final User user, final String startDateStr, final String endDateStr, final Long weeklyTodoId) {
        final Weekly weekly = getWeeklyPlanner(user, startDateStr, endDateStr);
        final WeeklyTodo weeklyTodo = weeklyTodoRepository.findByIdAndWeekly(weeklyTodoId, weekly);
        if (weeklyTodo == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TODO);
        }
        return weeklyTodo;
    }

    @Override
    public void checkValidWeek(final String startDateStr, final String endDateStr) {
        if (stringToLocalDate(startDateStr).getDayOfWeek().getValue() != 1
                || ChronoUnit.DAYS.between(stringToLocalDate(startDateStr), stringToLocalDate(endDateStr)) != 6) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE);
        }
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
        final WeeklyTodo weeklyTodo = getWeeklyTodo(user, updateWeeklyTodoContentRequest.getStartDate(),
                updateWeeklyTodoContentRequest.getEndDate(), updateWeeklyTodoContentRequest.getWeeklyTodoId());
        weeklyTodo.updateWeeklyTodoContent(updateWeeklyTodoContentRequest.getWeeklyTodoContent());
    }

    @Override
    @Transactional
    public void updateWeeklyTodoStatus(final User user, final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest) {
        final WeeklyTodo weeklyTodo = getWeeklyTodo(user, updateWeeklyTodoStatusRequest.getStartDate(),
                updateWeeklyTodoStatusRequest.getEndDate(), updateWeeklyTodoStatusRequest.getWeeklyTodoId());
        weeklyTodo.updateWeeklyTodoStatus(updateWeeklyTodoStatusRequest.getWeeklyTodoStatus());
    }

    @Override
    @Transactional
    public void removeWeeklyTodo(final User user, final RemoveWeeklyTodoRequest removeWeeklyTodoRequest) {
        final Weekly weekly = getWeeklyPlanner(user, removeWeeklyTodoRequest.getStartDate(), removeWeeklyTodoRequest.getEndDate());
        weeklyTodoRepository.deleteByIdAndWeekly(removeWeeklyTodoRequest.getWeeklyTodoId(), weekly);
    }

    @Override
    public List<WeeklyPlannerTodoResponse> getWeeklyTodos(final User plannerWriter, final String startDate, final String endDate, final boolean permission) {
        final List<WeeklyPlannerTodoResponse> weeklyTodos = new ArrayList<>();
        final Weekly weekly = weeklyRepository.findByUserAndStartDayAndEndDay(plannerWriter, startDate, endDate);
        if (weekly != null && permission) {
            final List<WeeklyTodo> weeklyTodoList = weeklyTodoRepository.findAllByWeekly(weekly);
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

}
