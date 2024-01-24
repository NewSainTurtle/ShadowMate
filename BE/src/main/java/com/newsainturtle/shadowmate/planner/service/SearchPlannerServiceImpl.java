package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.planner.dto.response.*;
import com.newsainturtle.shadowmate.planner.entity.*;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.*;
import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineTodo;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineTodoRepository;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.newsainturtle.shadowmate.common.constant.CommonConstant.DATE_PATTERN;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchPlannerServiceImpl extends DateCommonService implements SearchPlannerService {

    private final UserRepository userRepository;
    private final DailyPlannerRepository dailyPlannerRepository;
    private final DailyPlannerLikeRepository dailyPlannerLikeRepository;
    private final TodoRepository todoRepository;
    private final TimeTableRepository timeTableRepository;
    private final DdayRepository ddayRepository;
    private final FollowRepository followRepository;
    private final WeeklyRepository weeklyRepository;
    private final WeeklyTodoRepository weeklyTodoRepository;
    private final SocialRepository socialRepository;
    private final RoutineRepository routineRepository;
    private final RoutineTodoRepository routineTodoRepository;
    private int totalMinutes;

    private Dday getDday(final User user) {
        final String today = String.valueOf(LocalDate.now());
        Dday dday = ddayRepository.findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(user, today);
        if (dday == null) dday = ddayRepository.findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(user, today);
        return dday;
    }

    private boolean havePermissionToSearch(final User user, final User plannerWriter) {
        if (user.getId().equals(plannerWriter.getId()) ||
                plannerWriter.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC) ||
                (plannerWriter.getPlannerAccessScope().equals(PlannerAccessScope.FOLLOW) && followRepository.findByFollowerIdAndFollowingId(user, plannerWriter) != null)
        ) {
            return true;
        } else {
            return false;
        }
    }

    private void checkValidDate(final String date) {
        if (!Pattern.matches(DATE_PATTERN, date)) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT);
        }
    }

    private void checkValidWeek(final String startDateStr, final String endDateStr) {
        if (stringToLocalDate(startDateStr).getDayOfWeek().getValue() != 1
                || Period.between(stringToLocalDate(startDateStr), stringToLocalDate(endDateStr)).getDays() != 6) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE);
        }
    }

    private User certifyPlannerWriter(final long plannerWriterId) {
        final User plannerWriter = userRepository.findByIdAndWithdrawalIsFalse(plannerWriterId);
        if (plannerWriter == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_USER);
        }
        return plannerWriter;
    }

    private List<WeeklyPlannerTodoResponse> getWeeklyTodos(final User plannerWriter, final String startDate, final String endDate, final boolean permission) {
        final List<WeeklyPlannerTodoResponse> weeklyTodos = new ArrayList<>();
        checkValidWeek(startDate, endDate);
        final Weekly weekly = weeklyRepository.findByUserAndStartDayAndEndDay(plannerWriter, startDate, endDate);
        if (weekly != null) {
            final List<WeeklyTodo> weeklyTodoList = weeklyTodoRepository.findAllByWeekly(weekly);
            if (permission) {
                for (WeeklyTodo weeklyTodo : weeklyTodoList) {
                    weeklyTodos.add(WeeklyPlannerTodoResponse.builder()
                            .weeklyTodoId(weeklyTodo.getId())
                            .weeklyTodoContent(weeklyTodo.getWeeklyTodoContent())
                            .weeklyTodoStatus(weeklyTodo.getWeeklyTodoStatus())
                            .build());
                }
            }
        }
        return weeklyTodos;
    }

    private CalendarDayTotalResponse getDayList(final User user, final User plannerWriter, final LocalDate date) {
        final List<CalendarDayResponse> dayList = new ArrayList<>();
        final List<Long> dailyPlannerIdList = new ArrayList<>();
        int todoTotal = 0;
        int todoIncomplete = 0;

        if (havePermissionToSearch(user, plannerWriter)) {
            final int lastDay = YearMonth.from(date).lengthOfMonth();
            for (int i = 0; i < lastDay; i++) {
                final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(plannerWriter, String.valueOf(date.plusDays(i)));
                int todoCount = 0;
                int dayStatus = 0;
                final int routineCount = routineTodoRepository.countByUserAndDailyPlannerDayAndTodoIsNull(plannerWriter.getId(), String.valueOf(date.plusDays(i)));
                if (dailyPlanner != null) {
                    dailyPlannerIdList.add(dailyPlanner.getId());
                    final int totalCount = todoRepository.countByDailyPlanner(dailyPlanner) + routineCount;
                    if (totalCount > 0) {
                        todoCount = todoRepository.countByDailyPlannerAndTodoStatusNot(dailyPlanner, TodoStatus.COMPLETE) + routineCount;
                        todoTotal += totalCount;
                        todoIncomplete += todoCount;
                        final double percent = ((totalCount - todoCount) / (double) totalCount) * 100;
                        if (percent == 100) {
                            dayStatus = 3;
                        } else if (percent >= 60) {
                            dayStatus = 2;
                        } else if (percent >= 0) {
                            dayStatus = 1;
                        }
                    }
                } else if (routineCount > 0) {
                    todoCount = routineCount;
                    dayStatus = 1;
                }

                dayList.add(
                        CalendarDayResponse.builder()
                                .date(localDateToString(date.plusDays(i)))
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
                .plannerLikeCount(dailyPlannerLikeRepository.countByDailyPlannerIdIn(dailyPlannerIdList))
                .build();
    }

    private List<WeeklyPlannerDailyResponse> getDayList(final User plannerWriter, final User user, final LocalDate date, final boolean permission) {
        final List<WeeklyPlannerDailyResponse> dayList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(plannerWriter, String.valueOf(date.plusDays(i)));
            final List<WeeklyPlannerDailyTodoResponse> dailyTodos = new ArrayList<>();

            if (plannerWriter.getId() == user.getId()) {
                dailyPlanner = makeRoutineTodo(user, String.valueOf(date.plusDays(i)), dailyPlanner);
            }

            if (dailyPlanner == null || !permission) {
                dayList.add(WeeklyPlannerDailyResponse.builder()
                        .date(localDateToString(date.plusDays(i)))
                        .retrospection(null)
                        .dailyTodos(dailyTodos)
                        .build());
            } else {
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
                dayList.add(WeeklyPlannerDailyResponse.builder()
                        .date(localDateToString(date.plusDays(i)))
                        .retrospection(dailyPlanner.getRetrospection())
                        .dailyTodos(dailyTodos)
                        .build());
            }
        }
        return dayList;
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

    private DailyPlanner makeRoutineTodo(final User user, final String date, DailyPlanner dailyPlanner) {
        final RoutineTodo[] routineTodoList = routineTodoRepository.findAllByUserAndDailyPlannerDayAndTodoIsNull(user.getId(), date);
        if (routineTodoList.length > 0) {
            if (dailyPlanner == null) {
                dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                        .dailyPlannerDay(date)
                        .user(user)
                        .build());
            }
            final TodoIndexResponse todoIndexResponse = todoRepository.findTopByDailyPlannerOrderByTodoIndexDesc(dailyPlanner);
            double lastTodoIndex = todoIndexResponse == null ? 100000 : todoIndexResponse.getTodoIndex() + 100000;
            for (RoutineTodo routineTodo : routineTodoList) {
                final Todo todo = todoRepository.save(Todo.builder()
                        .category(routineTodo.getRoutine().getCategory())
                        .todoContent(routineTodo.getRoutine().getRoutineContent())
                        .todoStatus(TodoStatus.EMPTY)
                        .dailyPlanner(dailyPlanner)
                        .todoIndex(lastTodoIndex)
                        .timeTables(new ArrayList<>())
                        .build());
                routineTodo.updateTodo(todo);
                lastTodoIndex += 100000;
            }
        }
        return dailyPlanner;
    }

    @Override
    @Transactional
    public SearchDailyPlannerResponse searchDailyPlanner(final User user, final Long plannerWriterId, final String date) {
        checkValidDate(date);
        final User plannerWriter = certifyPlannerWriter(plannerWriterId);
        DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(plannerWriter, date);
        final Dday dday = getDday(user);

        if (plannerWriter.getId() == user.getId()) {
            dailyPlanner = makeRoutineTodo(user, date, dailyPlanner);
        }

        if (dailyPlanner == null || !havePermissionToSearch(user, plannerWriter)) {
            return SearchDailyPlannerResponse.builder()
                    .date(date)
                    .plannerAccessScope(plannerWriter.getPlannerAccessScope().getScope())
                    .dday(dday == null ? null : dday.getDdayDate())
                    .ddayTitle(dday == null ? null : dday.getDdayTitle())
                    .build();
        } else {
            final boolean like = dailyPlannerLikeRepository.findByUserAndDailyPlanner(user, dailyPlanner) != null;
            final Social shareSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner);
            final long likeCount = dailyPlannerLikeRepository.countByDailyPlanner(dailyPlanner);
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
                    .dday(dday == null ? null : dday.getDdayDate().toString())
                    .ddayTitle(dday == null ? null : dday.getDdayTitle())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .shareSocial(shareSocial == null ? null : shareSocial.getId())
                    .like(like)
                    .likeCount(likeCount)
                    .studyTimeHour(totalMinutes / 60)
                    .studyTimeMinute(totalMinutes % 60)
                    .dailyTodos(dailyTodos)
                    .build();

        }
    }

    @Override
    public SearchCalendarResponse searchCalendar(final User user, final Long plannerWriterId, final String dateStr) {
        final String datePattern = "^([12]\\d{3}-(0[1-9]|1[0-2])-01)$";
        if (!Pattern.matches(datePattern, dateStr)) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT);
        }
        final User plannerWriter = certifyPlannerWriter(plannerWriterId);
        final CalendarDayTotalResponse calendarDayTotalResponse = getDayList(user, plannerWriter, stringToLocalDate(dateStr));

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
        checkValidDate(startDate);
        checkValidDate(endDate);

        final User plannerWriter = certifyPlannerWriter(plannerWriterId);
        final boolean permission = havePermissionToSearch(user, plannerWriter);
        final List<WeeklyPlannerTodoResponse> weeklyTodos = getWeeklyTodos(plannerWriter, startDate, endDate, permission);
        final List<WeeklyPlannerDailyResponse> dayList = getDayList(plannerWriter, user, stringToLocalDate(startDate), permission);
        final Dday dday = getDday(user);

        return SearchWeeklyPlannerResponse.builder()
                .plannerAccessScope(plannerWriter.getPlannerAccessScope().getScope())
                .dday(dday == null ? null : dday.getDdayDate())
                .weeklyTodos(weeklyTodos)
                .dayList(dayList)
                .build();
    }
}
