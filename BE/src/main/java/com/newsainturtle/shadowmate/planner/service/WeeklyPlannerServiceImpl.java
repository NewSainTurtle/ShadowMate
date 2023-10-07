package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.AddWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.AddWeeklyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.UpdateWeeklyTodoContentRequest;
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

import java.sql.Date;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyPlannerServiceImpl implements WeeklyPlannerService {

    private final WeeklyRepository weeklyRepository;
    private final WeeklyTodoRepository weeklyTodoRepository;

    private Weekly getOrCreateWeeklyPlanner(final User user, final String startDateStr, final String endDateStr) {
        Date startDate = Date.valueOf(startDateStr);
        Date endDate = Date.valueOf(endDateStr);
        if (startDate.getDay() != 1 || Period.between(startDate.toLocalDate(), endDate.toLocalDate()).getDays() != 6) {
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
        final Weekly weekly = getOrCreateWeeklyPlanner(user, updateWeeklyTodoContentRequest.getStartDate(), updateWeeklyTodoContentRequest.getEndDate());
        final WeeklyTodo weeklyTodo = weeklyTodoRepository.findByIdAndWeekly(updateWeeklyTodoContentRequest.getWeeklyTodoId(), weekly);
        if (weeklyTodo == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_TODO);
        }
        final WeeklyTodo changeWeeklyTodo = WeeklyTodo.builder()
                .id(weeklyTodo.getId())
                .createTime(weeklyTodo.getCreateTime())
                .weekly(weeklyTodo.getWeekly())
                .weeklyTodoContent(updateWeeklyTodoContentRequest.getWeeklyTodoContent())
                .weeklyTodoStatus(weeklyTodo.getWeeklyTodoStatus())
                .build();
        weeklyTodoRepository.save(changeWeeklyTodo);
    }
}
