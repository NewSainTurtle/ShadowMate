package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.AddWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateWeeklyTodoContentRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateWeeklyTodoStatusRequest;
import com.newsainturtle.shadowmate.planner.dto.response.AddWeeklyTodoResponse;
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

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Transactional
public class WeeklyPlannerServiceImpl extends DateCommonService implements WeeklyPlannerService {

    private final WeeklyRepository weeklyRepository;
    private final WeeklyTodoRepository weeklyTodoRepository;

    private Weekly getOrCreateWeeklyPlanner(final User user, final String startDateStr, final String endDateStr) {
        checkValidWeek(startDateStr, endDateStr);
        final LocalDate startDate = stringToLocalDate(startDateStr);
        final LocalDate endDate = stringToLocalDate(endDateStr);
        if (stringToLocalDate(startDateStr).getDayOfWeek().getValue() != 1 || Period.between(startDate, endDate).getDays() != 6) {
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

    private Weekly getWeeklyPlanner(final User user, final String startDateStr, final String endDateStr) {
        checkValidWeek(startDateStr, endDateStr);
        final Weekly weekly = weeklyRepository.findByUserAndStartDayAndEndDay(user, stringToLocalDate(startDateStr), stringToLocalDate(endDateStr));
        if (weekly == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_WEEKLY_PLANNER);
        }
        return weekly;
    }

    private void checkValidWeek(final String startDateStr, final String endDateStr) {
        if (stringToLocalDate(startDateStr).getDayOfWeek().getValue() != 1
                || Period.between(stringToLocalDate(startDateStr), stringToLocalDate(endDateStr)).getDays() != 6) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE);
        }
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
    public void updateWeeklyTodoContent(final User user, final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest) {
        final WeeklyTodo weeklyTodo = getWeeklyTodo(user, updateWeeklyTodoContentRequest.getStartDate(),
                updateWeeklyTodoContentRequest.getEndDate(), updateWeeklyTodoContentRequest.getWeeklyTodoId());
        weeklyTodo.updateWeeklyTodoContent(updateWeeklyTodoContentRequest.getWeeklyTodoContent());
    }

    @Override
    public void updateWeeklyTodoStatus(final User user, final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest) {
        final WeeklyTodo weeklyTodo = getWeeklyTodo(user, updateWeeklyTodoStatusRequest.getStartDate(),
                updateWeeklyTodoStatusRequest.getEndDate(), updateWeeklyTodoStatusRequest.getWeeklyTodoId());
        weeklyTodo.updateWeeklyTodoStatus(updateWeeklyTodoStatusRequest.getWeeklyTodoStatus());
    }

    @Override
    public void removeWeeklyTodo(final User user, final RemoveWeeklyTodoRequest removeWeeklyTodoRequest) {
        final Weekly weekly = getWeeklyPlanner(user, removeWeeklyTodoRequest.getStartDate(), removeWeeklyTodoRequest.getEndDate());
        weeklyTodoRepository.deleteByIdAndWeekly(removeWeeklyTodoRequest.getWeeklyTodoId(), weekly);
    }

}
