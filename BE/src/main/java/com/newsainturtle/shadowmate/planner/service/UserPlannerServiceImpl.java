package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.follow.service.FollowService;
import com.newsainturtle.shadowmate.planner.dto.request.AddDailyLikeRequest;
import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveDailyLikeRequest;
import com.newsainturtle.shadowmate.planner.dto.response.*;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingService;
import com.newsainturtle.shadowmate.planner_setting.service.RoutineService;
import com.newsainturtle.shadowmate.social.service.SocialService;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPlannerServiceImpl extends DateCommonService implements UserPlannerService {

    private final DailyPlannerService dailyPlannerService;
    private final MonthlyPlannerService monthlyPlannerService;
    private final WeeklyPlannerService weeklyPlannerService;
    private final UserService userService;
    private final RoutineService routineService;
    private final PlannerSettingService plannerSettingService;
    private final FollowService followService;
    private final SocialService socialService;

    private List<WeeklyPlannerDailyResponse> getDayList(final User plannerWriter, final String date, final boolean permission) {
        final List<WeeklyPlannerDailyResponse> dayList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final String targetDate = localDateToString(stringToLocalDate(date).plusDays(i));
            if (!permission) {
                dayList.add(WeeklyPlannerDailyResponse.builder()
                        .date(targetDate)
                        .retrospection(null)
                        .dailyTodos(new ArrayList<>())
                        .build());
            } else {
                routineService.makeRoutineTodo(plannerWriter, targetDate);
                dayList.add(dailyPlannerService.getWeeklyPlannerDailyResponse(plannerWriter, targetDate));
            }
        }
        return dayList;
    }

    private CalendarDayTotalResponse getDayList(final User user, final User plannerWriter, final LocalDate date) {
        final List<CalendarDayResponse> dayList = new ArrayList<>();
        final List<Long> dailyPlannerIdList = new ArrayList<>();
        int todoTotal = 0, todoIncomplete = 0;
        if (followService.havePermissionToSearch(user, plannerWriter)) {
            final int lastDay = YearMonth.from(date).lengthOfMonth();
            for (int i = 0; i < lastDay; i++) {
                final String targetDate = localDateToString(date.plusDays(i));
                final DailyPlanner dailyPlanner = dailyPlannerService.getDailyPlanner(plannerWriter, targetDate);
                int todoCount = routineService.countRoutineTodo(plannerWriter, targetDate), dayStatus = 0;
                if (dailyPlanner != null) {
                    dailyPlannerIdList.add(dailyPlanner.getId());
                    final int totalCount = dailyPlannerService.countTodo(dailyPlanner) + todoCount;
                    if (totalCount > 0) {
                        todoCount += dailyPlannerService.countTodoComplete(dailyPlanner);
                        todoTotal += totalCount;
                        todoIncomplete += todoCount;
                        dayStatus = getDayStatus(((totalCount - todoCount) / (double) totalCount) * 100);
                    }
                } else if (todoCount > 0) {
                    dayStatus = 1;
                }

                dayList.add(
                        CalendarDayResponse.builder()
                                .date(targetDate)
                                .todoCount(todoCount)
                                .dayStatus(dayStatus)
                                .build()
                );
            }
        }

        return CalendarDayTotalResponse.builder()
                .calendarDayResponseList(dayList)
                .todoTotal(todoTotal)
                .todoIncomplete(todoIncomplete)
                .plannerLikeCount(dailyPlannerService.getMonthlyLikeCount(dailyPlannerIdList))
                .build();
    }

    private int getDayStatus(final double percent) {
        if (percent == 100) {
            return 3;
        } else if (percent >= 60) {
            return 2;
        } else if (percent >= 0) {
            return 1;
        }
        return 0;
    }

    @Override
    @Transactional
    public void addDailyLike(final User user, final Long plannerWriterId, final AddDailyLikeRequest addDailyPlannerLikeRequest) {
        dailyPlannerService.addDailyLike(user, userService.getUserById(plannerWriterId), addDailyPlannerLikeRequest);
    }

    @Override
    @Transactional
    public void removeDailyLike(final User user, final Long plannerWriterId, final RemoveDailyLikeRequest removeDailyLikeRequest) {
        dailyPlannerService.removeDailyLike(user, userService.getUserById(plannerWriterId), removeDailyLikeRequest);
    }

    @Override
    @Transactional
    public VisitorBookResponse addVisitorBook(final User visitor, final long ownerId, final AddVisitorBookRequest addVisitorBookRequest) {
        return monthlyPlannerService.addVisitorBook(visitor, userService.getUserById(ownerId), addVisitorBookRequest);
    }

    @Override
    public SearchVisitorBookResponse searchVisitorBook(final User visitor, final long ownerId, final long lastVisitorBookId) {
        return monthlyPlannerService.searchVisitorBook(visitor, userService.getUserById(ownerId), lastVisitorBookId);
    }

    @Override
    @Transactional
    public SearchDailyPlannerResponse searchDailyPlanner(final User user, final Long plannerWriterId, final String date) {
        dailyPlannerService.checkValidDate(date);
        final User plannerWriter = userService.getUserById(plannerWriterId);
        final Dday dday = plannerSettingService.getDday(user);

        if (followService.havePermissionToSearch(user, plannerWriter)) {
            routineService.makeRoutineTodo(plannerWriter, date);
            final DailyPlanner dailyPlanner = dailyPlannerService.getDailyPlanner(plannerWriter, date);
            if (dailyPlanner != null) {
                SearchDailyPlannerResponse searchDailyPlannerResponse = dailyPlannerService.searchDailyPlanner(user, plannerWriter, date, dailyPlanner);
                searchDailyPlannerResponse.setShareSocial(socialService.getSocialId(dailyPlanner));
                searchDailyPlannerResponse.setDday(dday);
                return searchDailyPlannerResponse;
            }
        }

        return SearchDailyPlannerResponse.builder()
                .date(date)
                .plannerAccessScope(plannerWriter.getPlannerAccessScope().getScope())
                .dday(dday == null ? null : dday.getDdayDate())
                .ddayTitle(dday == null ? null : dday.getDdayTitle())
                .dailyTodos(new ArrayList<>())
                .build();
    }

    @Override
    public SearchCalendarResponse searchCalendar(final User user, final Long plannerWriterId, final String date) {
        monthlyPlannerService.checkValidDate(date);
        final User plannerWriter = userService.getUserById(plannerWriterId);
        final CalendarDayTotalResponse calendarDayTotalResponse = getDayList(user, plannerWriter, stringToLocalDate(date));

        return SearchCalendarResponse.builder()
                .plannerAccessScope(plannerWriter.getPlannerAccessScope().getScope())
                .dayList(calendarDayTotalResponse.getCalendarDayResponseList())
                .todoTotal(calendarDayTotalResponse.getTodoTotal())
                .todoComplete(calendarDayTotalResponse.getTodoTotal() - calendarDayTotalResponse.getTodoIncomplete())
                .todoIncomplete(calendarDayTotalResponse.getTodoIncomplete())
                .plannerLikeCount(calendarDayTotalResponse.getPlannerLikeCount())
                .build();
    }

    @Override
    @Transactional
    public SearchWeeklyPlannerResponse searchWeeklyPlanner(final User user, final Long plannerWriterId, final String startDate, final String endDate) {
        dailyPlannerService.checkValidDate(startDate);
        dailyPlannerService.checkValidDate(endDate);
        weeklyPlannerService.checkValidWeek(startDate, endDate);
        final User plannerWriter = userService.getUserById(plannerWriterId);
        final boolean permission = followService.havePermissionToSearch(user, plannerWriter);
        final Dday dday = plannerSettingService.getDday(user);

        return SearchWeeklyPlannerResponse.builder()
                .plannerAccessScope(plannerWriter.getPlannerAccessScope().getScope())
                .dday(dday == null ? null : dday.getDdayDate())
                .weeklyTodos(weeklyPlannerService.getWeeklyTodos(plannerWriter, startDate, endDate, permission))
                .dayList(getDayList(plannerWriter, startDate, permission))
                .build();
    }
}
