package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.response.TodoIndexResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.dto.request.AddRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.RemoveRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.request.UpdateRoutineRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.response.AddRoutineResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.response.GetCategoryResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.response.GetRoutineListResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.response.GetRoutineResponse;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.Routine;
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineDay;
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineTodo;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineDayRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.RoutineTodoRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineServiceImpl extends DateCommonService implements RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineDayRepository routineDayRepository;
    private final RoutineTodoRepository routineTodoRepository;
    private final DailyPlannerRepository dailyPlannerRepository;
    private final TodoRepository todoRepository;

    private void addRoutineTodo(final Routine routine, final LocalDate startDate, final LocalDate endDate, final List<String> days) {
        final boolean[] checkDays = new boolean[8];
        for (String requestDay : days) {
            int day = getDayIndex(requestDay);
            if (checkDays[day]) {
                throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
            }
            checkDays[day] = true;
            int plusCount;
            if (day < startDate.getDayOfWeek().getValue()) {
                plusCount = day + 7 - startDate.getDayOfWeek().getValue();
            } else {
                plusCount = day - startDate.getDayOfWeek().getValue();
            }

            LocalDate target = startDate.plusDays(plusCount);
            while (ChronoUnit.DAYS.between(target, endDate) >= 0) {
                final RoutineTodo routineTodo = routineTodoRepository.save(RoutineTodo.builder()
                        .day(requestDay)
                        .dailyPlannerDay(localDateToString(target))
                        .build());
                routineTodo.setRoutine(routine);
                target = target.plusDays(7);
            }
        }
    }

    private void addRoutineDay(final Routine routine, final List<String> days) {
        for (String requestDay : days) {
            final RoutineDay routineDay = routineDayRepository.save(RoutineDay.builder()
                    .day(requestDay)
                    .build());
            routineDay.setRoutine(routine);
        }
    }

    private int getDayIndex(String day) {
        if (day.equals("월")) {
            return 1;
        } else if (day.equals("화")) {
            return 2;
        } else if (day.equals("수")) {
            return 3;
        } else if (day.equals("목")) {
            return 4;
        } else if (day.equals("금")) {
            return 5;
        } else if (day.equals("토")) {
            return 6;
        } else if (day.equals("일")) {
            return 7;
        }
        throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
    }

    private DailyPlanner getOrCreateDailyPlanner(final User user, final String date) {
        final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, date);
        if (dailyPlanner == null) {
            return dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(date)
                    .user(user)
                    .build());
        }
        return dailyPlanner;
    }

    private void addTodo(final User user, final RoutineTodo[] routineTodoList) {
        for (RoutineTodo routineTodo : routineTodoList) {
            final DailyPlanner dailyPlanner = getOrCreateDailyPlanner(user, routineTodo.getDailyPlannerDay());
            final TodoIndexResponse lastTodoIndex = todoRepository.findTopByDailyPlannerOrderByTodoIndexDesc(dailyPlanner);
            todoRepository.save(Todo.builder()
                    .category(routineTodo.getRoutine().getCategory())
                    .todoContent(routineTodo.getRoutine().getRoutineContent())
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .todoIndex(lastTodoIndex == null ? 100000 : lastTodoIndex.getTodoIndex() + 100000)
                    .timeTables(new ArrayList<>())
                    .build());
        }
    }

    private void removeTodo(final Routine routine, final boolean onlyFutureAndToday, final String today) {
        RoutineTodo[] routineTodoList;
        if (onlyFutureAndToday) {
            routineTodoList = routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqual(routine, today);
        } else {
            routineTodoList = routineTodoRepository.findAllByRoutineAndTodoIsNotNull(routine);
        }

        for (RoutineTodo routineTodo : routineTodoList) {
            final long todoId = routineTodo.getTodo().getId();
            routineTodo.updateTodo(null);
            todoRepository.deleteById(todoId);
        }
    }

    private void removeRoutineTodoAndTodoIsNull(final RoutineTodo[] routineTodoList) {
        for (RoutineTodo routineTodo : routineTodoList) {
            routineTodo.setRoutine(null);
            routineTodoRepository.deleteById(routineTodo.getId());
        }
    }

    private void removeRoutineTodoAndTodoIsNotNull(final Routine routine, final RoutineTodo[] routineTodoList) {
        for (RoutineTodo routineTodo : routineTodoList) {
            final long todoId = routineTodo.getTodo().getId();
            if (routine.getRoutineContent().equals(routineTodo.getTodo().getTodoContent()) &&
                    ((routine.getCategory() == null && routineTodo.getTodo().getCategory() == null) ||
                            (routine.getCategory() != null && (routineTodo.getTodo().getCategory() != null && routine.getCategory().getId().equals(routineTodo.getTodo().getCategory().getId()))))) {
                todoRepository.deleteById(todoId);
            }
            routineTodo.updateTodo(null);
            routineTodo.setRoutine(null);
            routineTodoRepository.deleteById(routineTodo.getId());
        }
    }

    private void removeRoutineTodoDayLessThanToday(final Routine routine, final String day, final String today) {
        final RoutineTodo[] routineTodoList = routineTodoRepository.findAllByRoutineAndDailyPlannerDayLessThanAndDay(routine, today, day);
        for (RoutineTodo routineTodo : routineTodoList) {
            routineTodo.setRoutine(null);
            routineTodoRepository.deleteById(routineTodo.getId());
        }
    }

    private void removeRoutineTodoDayGreaterThanEqualToday(final Routine routine, final String day, final String today) {
        removeRoutineTodoAndTodoIsNull(routineTodoRepository.findAllByRoutineAndTodoIsNullAndDailyPlannerDayGreaterThanEqualAndDay(routine, today, day));
        removeRoutineTodoAndTodoIsNotNull(routine, routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqualAndDay(routine, today, day));
    }

    private void removeRoutineTodoDay(final Routine routine, final String day) {
        removeRoutineTodoAndTodoIsNull(routineTodoRepository.findAllByRoutineAndTodoIsNullAndDay(routine, day));
        removeRoutineTodoAndTodoIsNotNull(routine, routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDay(routine, day));
    }

    private void removeRoutineTodoDays(final Routine routine, final String startDay, final String endDay, final List<String> days) {
        removeRoutineTodoAndTodoIsNull(routineTodoRepository.findAllByRoutineAndTodoIsNullAndDailyPlannerDayBetweenAndDayIn(routine, startDay, endDay, days));
        removeRoutineTodoAndTodoIsNotNull(routine, routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayBetweenAndDayIn(routine, startDay, endDay, days));
    }

    private void updateTodo(final Routine routine, final RoutineTodo[] routineTodoList, final Category requestCategory, final String requestRoutineContent) {
        for (RoutineTodo routineTodo : routineTodoList) {
            if (routine.getRoutineContent().equals(routineTodo.getTodo().getTodoContent()) &&
                    ((routine.getCategory() == null && routineTodo.getTodo().getCategory() == null) || (routine.getCategory() != null && routine.getCategory().getId().equals(routineTodo.getTodo().getCategory().getId())))) {
                routineTodo.getTodo().updateCategoryAndTodoContent(requestCategory, requestRoutineContent);
            }
        }
    }

    private boolean[] checkDays(final List<String> days) {
        final boolean[] checkDays = new boolean[8];
        for (String requestDay : days) {
            int day = getDayIndex(requestDay);
            if (checkDays[day]) {
                throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
            }
            checkDays[day] = true;
        }
        return checkDays;
    }

    private void updateUnchangingDayOrder1(final Routine routine,
                                           final LocalDate requestStartDay, final LocalDate requestEndDay,
                                           final Category category, final String routineContent, final List<String> updateDays) {
        final LocalDate originStartDay = stringToLocalDate(routine.getStartDay());
        final LocalDate originEndDay = stringToLocalDate(routine.getEndDay());
        final boolean checkRemoveDate = ChronoUnit.DAYS.between(originStartDay, requestStartDay) > 0 || ChronoUnit.DAYS.between(requestEndDay, originEndDay) > 0;
        final boolean checkAddDate = ChronoUnit.DAYS.between(requestStartDay, originStartDay) > 0 || ChronoUnit.DAYS.between(originEndDay, requestEndDay) > 0;

        if (!updateDays.isEmpty()) {
            if (checkRemoveDate)
                removeRoutineTodoOrder1(routine, updateDays, originStartDay, originEndDay, requestStartDay, requestEndDay);
            updateTodo(routine, routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDayIn(routine, updateDays), category, routineContent);
            if (checkAddDate)
                addRoutineTodoOrder1(routine, updateDays, originStartDay, originEndDay, requestStartDay, requestEndDay);
        }
    }

    private void updateUnchangingDayOrder2(final Routine routine,
                                           final LocalDate requestStartDay, final LocalDate requestEndDay,
                                           final Category category, final String routineContent, final String today, final List<String> updateDays) {
        final LocalDate originStartDay = stringToLocalDate(routine.getStartDay());
        final LocalDate originEndDay = stringToLocalDate(routine.getEndDay());
        final boolean checkRemoveDate = (ChronoUnit.DAYS.between(originStartDay, requestStartDay) > 0 && ChronoUnit.DAYS.between(stringToLocalDate(today), requestStartDay) > 0) ||
                (ChronoUnit.DAYS.between(requestEndDay, originEndDay) > 0 && ChronoUnit.DAYS.between(stringToLocalDate(today), originEndDay) > 0);
        final boolean checkAddDate = (ChronoUnit.DAYS.between(requestStartDay, originStartDay) > 0 && ChronoUnit.DAYS.between(stringToLocalDate(today), originStartDay) > 0) ||
                (ChronoUnit.DAYS.between(originEndDay, requestEndDay) > 0 && ChronoUnit.DAYS.between(stringToLocalDate(today), requestEndDay) > 0);

        if (!updateDays.isEmpty()) {
            if (checkRemoveDate)
                removeRoutineTodoOrder2(routine, updateDays, today, originStartDay, originEndDay, requestStartDay, requestEndDay);
            updateTodo(routine, routineTodoRepository.findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqualAndDayIn(routine, today, updateDays), category, routineContent);
            if (checkAddDate)
                addRoutineTodoOrder2(routine, updateDays, today, originStartDay, originEndDay, requestStartDay, requestEndDay);
        }
    }

    private void updateOriginalDayOrder1(final Routine routine,
                                         final UpdateRoutineRequest updateRoutineRequest,
                                         final Category category, boolean[] checkDays) {
        final RoutineDay[] routineDays = routineDayRepository.findAllByRoutine(routine);

        final List<String> updateDays = new ArrayList<>();
        for (RoutineDay routineDay : routineDays) {
            int day = getDayIndex(routineDay.getDay());
            if (checkDays[day]) {
                updateDays.add(routineDay.getDay());
                checkDays[day] = false;
            } else {
                removeRoutineTodoDay(routine, routineDay.getDay());
                routineDay.setRoutine(null);
                routineDayRepository.deleteById(routineDay.getId());
            }
        }

        updateUnchangingDayOrder1(routine, stringToLocalDate(updateRoutineRequest.getStartDay()), stringToLocalDate(updateRoutineRequest.getEndDay()),
                category, updateRoutineRequest.getRoutineContent(), updateDays);
    }

    private void updateOriginalDayOrder2(final User user, final Routine routine,
                                         final UpdateRoutineRequest updateRoutineRequest,
                                         final Category category, final String today, boolean[] checkDays) {
        final RoutineDay[] routineDays = routineDayRepository.findAllByRoutine(routine);
        final RoutineTodo[] routineTodoList = routineTodoRepository.findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThanAndDayIn(routine, today,
                Arrays.stream(routineDays)
                        .map(RoutineDay::getDay)
                        .collect(Collectors.toList()));
        addTodo(user, routineTodoList);

        final List<String> updateDays = new ArrayList<>();
        for (RoutineDay routineDay : routineDays) {
            int day = getDayIndex(routineDay.getDay());
            if (checkDays[day]) {
                updateDays.add(routineDay.getDay());
                checkDays[day] = false;
            } else {
                removeRoutineTodoDayLessThanToday(routine, routineDay.getDay(), today);
                removeRoutineTodoDayGreaterThanEqualToday(routine, routineDay.getDay(), today);
                routineDay.setRoutine(null);
                routineDayRepository.deleteById(routineDay.getId());
            }
        }

        updateUnchangingDayOrder2(routine, stringToLocalDate(updateRoutineRequest.getStartDay()), stringToLocalDate(updateRoutineRequest.getEndDay()),
                category, updateRoutineRequest.getRoutineContent(), today, updateDays);
    }

    private void updateNewDay(final Routine routine,
                              final String startDay, final String endDay, final Integer order,
                              final String today, boolean[] checkDays) {
        final String[] strDays = new String[]{"", "월", "화", "수", "목", "금", "토", "일"};
        final LocalDate requestStartDay = stringToLocalDate(startDay);
        final LocalDate requestEndDay = stringToLocalDate(endDay);
        final List<String> addDays = new ArrayList<>();

        for (int day = 1; day <= 7; day++) {
            if (checkDays[day]) addDays.add(strDays[day]);
        }

        if (!addDays.isEmpty()) {
            if (order.equals(1)) {
                addRoutineTodo(routine, requestStartDay, requestEndDay, addDays);
                addRoutineDay(routine, addDays);
            } else if (order.equals(2)) {
                if (ChronoUnit.DAYS.between(stringToLocalDate(today), requestStartDay) >= 0) {
                    addRoutineTodo(routine, requestStartDay, requestEndDay, addDays);
                } else if (ChronoUnit.DAYS.between(stringToLocalDate(today), requestEndDay) >= 0) {
                    addRoutineTodo(routine, stringToLocalDate(today), requestEndDay, addDays);
                }
                addRoutineDay(routine, addDays);
            }
        }
    }

    private void removeRoutineTodoOrder1(final Routine routine, final List<String> updateDays,
                                         final LocalDate originStartDay, final LocalDate originEndDay,
                                         final LocalDate requestStartDay, final LocalDate requestEndDay) {
        if (ChronoUnit.DAYS.between(originStartDay, requestStartDay) > 0) {
            removeRoutineTodoDays(routine, localDateToString(originStartDay), localDateToString(requestStartDay), updateDays);
        }
        if (ChronoUnit.DAYS.between(requestEndDay, originEndDay) > 0) {
            removeRoutineTodoDays(routine, localDateToString(requestEndDay.plusDays(1)), localDateToString(originEndDay), updateDays);
        }
    }

    private void addRoutineTodoOrder1(final Routine routine, final List<String> updateDays,
                                      final LocalDate originStartDay, final LocalDate originEndDay,
                                      final LocalDate requestStartDay, final LocalDate requestEndDay) {
        if (ChronoUnit.DAYS.between(requestStartDay, originStartDay) > 0) {
            addRoutineTodo(routine, requestStartDay, originStartDay.minusDays(1), updateDays);
        }
        if (ChronoUnit.DAYS.between(originEndDay, requestEndDay) > 0) {
            addRoutineTodo(routine, originEndDay.plusDays(1), requestEndDay, updateDays);
        }
    }

    private void removeRoutineTodoOrder2(final Routine routine, final List<String> updateDays, final String today,
                                         final LocalDate originStartDay, final LocalDate originEndDay,
                                         final LocalDate requestStartDay, final LocalDate requestEndDay) {
        if (ChronoUnit.DAYS.between(originStartDay, requestStartDay) > 0 && ChronoUnit.DAYS.between(stringToLocalDate(today), requestStartDay) > 0) {
            if (ChronoUnit.DAYS.between(originStartDay, stringToLocalDate(today)) > 0) {
                removeRoutineTodoDays(routine, today, localDateToString(requestStartDay.minusDays(1)), updateDays);
            } else {
                removeRoutineTodoDays(routine, localDateToString(originStartDay), localDateToString(requestStartDay.minusDays(1)), updateDays);
            }
        }
        if (ChronoUnit.DAYS.between(requestEndDay, originEndDay) > 0 && ChronoUnit.DAYS.between(stringToLocalDate(today), originEndDay) > 0) {
            if (ChronoUnit.DAYS.between(requestEndDay, stringToLocalDate(today)) > 0) {
                removeRoutineTodoDays(routine, today, localDateToString(originEndDay), updateDays);
            } else {
                removeRoutineTodoDays(routine, localDateToString(requestEndDay.plusDays(1)), localDateToString(originEndDay), updateDays);
            }
        }
    }

    private void addRoutineTodoOrder2(final Routine routine, final List<String> updateDays, final String today,
                                      final LocalDate originStartDay, final LocalDate originEndDay,
                                      final LocalDate requestStartDay, final LocalDate requestEndDay) {
        if (ChronoUnit.DAYS.between(requestStartDay, originStartDay) > 0 && ChronoUnit.DAYS.between(stringToLocalDate(today), originStartDay) > 0) {
            if (ChronoUnit.DAYS.between(requestStartDay, stringToLocalDate(today)) > 0) {
                addRoutineTodo(routine, stringToLocalDate(today), originStartDay.minusDays(1), updateDays);
            } else {
                addRoutineTodo(routine, requestStartDay, originStartDay.minusDays(1), updateDays);
            }
        }
        if (ChronoUnit.DAYS.between(originEndDay, requestEndDay) > 0 && ChronoUnit.DAYS.between(stringToLocalDate(today), requestEndDay) > 0) {
            if (ChronoUnit.DAYS.between(originEndDay, stringToLocalDate(today)) > 0) {
                addRoutineTodo(routine, stringToLocalDate(today), requestEndDay, updateDays);
            } else {
                addRoutineTodo(routine, originEndDay.plusDays(1), requestEndDay, updateDays);
            }
        }
    }

    @Override
    @Transactional
    public AddRoutineResponse addRoutine(final User user, final Category category, final AddRoutineRequest addRoutineRequest) {
        final Routine routine = routineRepository.save(Routine.builder()
                .user(user)
                .category(category)
                .routineContent(addRoutineRequest.getRoutineContent())
                .startDay(addRoutineRequest.getStartDay())
                .endDay(addRoutineRequest.getEndDay())
                .routineDays(new ArrayList<>())
                .routineTodos(new ArrayList<>())
                .build());
        addRoutineTodo(routine, stringToLocalDate(addRoutineRequest.getStartDay()), stringToLocalDate(addRoutineRequest.getEndDay()), addRoutineRequest.getDays());
        addRoutineDay(routine, addRoutineRequest.getDays());

        return AddRoutineResponse.builder()
                .routineId(routine.getId())
                .build();
    }

    @Override
    public GetRoutineListResponse getRoutineList(final User user) {
        final List<GetRoutineResponse> routines = new ArrayList<>();
        final Routine[] routineList = routineRepository.findAllByUser(user);
        for (Routine routine : routineList) {
            final RoutineDay[] routineDayList = routineDayRepository.findAllByRoutine(routine);
            final List<String> routineDays = new ArrayList<>();
            for (RoutineDay routineDay : routineDayList) {
                routineDays.add(routineDay.getDay());
            }
            routines.add(GetRoutineResponse.builder()
                    .routineId(routine.getId())
                    .routineContent(routine.getRoutineContent())
                    .startDay(routine.getStartDay())
                    .endDay(routine.getEndDay())
                    .days(routineDays)
                    .category(routine.getCategory() == null ? null :
                            GetCategoryResponse.builder()
                                    .categoryId(routine.getCategory().getId())
                                    .categoryColorCode(routine.getCategory().getCategoryColor().getCategoryColorCode())
                                    .categoryTitle(routine.getCategory().getCategoryTitle())
                                    .categoryEmoticon(routine.getCategory().getCategoryEmoticon())
                                    .build())
                    .build());
        }
        return GetRoutineListResponse.builder().routineList(routines).build();
    }

    @Override
    @Transactional
    public void removeRoutine(final User user, final RemoveRoutineRequest removeRoutineRequest) {
        final Routine routine = routineRepository.findByIdAndUser(removeRoutineRequest.getRoutineId(), user);
        if (routine == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_ROUTINE);
        }

        final String today = String.valueOf(LocalDate.now());
        if (removeRoutineRequest.getOrder().equals(1)) {
            removeTodo(routine, false, today);
        } else if (removeRoutineRequest.getOrder().equals(2)) {
            final RoutineTodo[] routineTodoList = routineTodoRepository.findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThan(routine, today);
            addTodo(user, routineTodoList);
            removeTodo(routine, true, today);
        } else if (removeRoutineRequest.getOrder().equals(3)) {
            final RoutineTodo[] routineTodoList = routineTodoRepository.findAllByRoutineAndTodoIsNull(routine);
            addTodo(user, routineTodoList);
        } else {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_ORDER);
        }
        routineRepository.deleteByIdAndUser(routine.getId(), user);
    }

    @Override
    @Transactional
    public void updateRoutine(final User user, final Category category, final UpdateRoutineRequest updateRoutineRequest) {
        final Routine routine = routineRepository.findByIdAndUser(updateRoutineRequest.getRoutineId(), user);
        if (routine == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_ROUTINE);
        }

        if (!updateRoutineRequest.getOrder().equals(1) && !updateRoutineRequest.getOrder().equals(2)) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_ORDER);
        }

        final String today = String.valueOf(LocalDate.now());
        boolean[] checkDays = checkDays(updateRoutineRequest.getDays());

        if (updateRoutineRequest.getOrder().equals(1)) {
            updateOriginalDayOrder1(routine, updateRoutineRequest, category, checkDays);
        } else if (updateRoutineRequest.getOrder().equals(2)) {
            updateOriginalDayOrder2(user, routine, updateRoutineRequest, category, today, checkDays);
        }

        updateNewDay(routine, updateRoutineRequest.getStartDay(), updateRoutineRequest.getEndDay(),
                updateRoutineRequest.getOrder(), today, checkDays);
        routine.updateDayAndCategoryAndRoutineContent(updateRoutineRequest.getStartDay(), updateRoutineRequest.getEndDay(), category, updateRoutineRequest.getRoutineContent());
    }

    @Override
    public long getRoutineCount(final Category category) {
        return routineRepository.countByCategory(category);
    }

    @Override
    @Transactional
    public void removeRoutineTodo(final Todo todo){
        final RoutineTodo routineTodo = routineTodoRepository.findByTodo(todo);
        if (routineTodo != null) {
            routineTodo.setRoutine(null);
            routineTodoRepository.deleteById(routineTodo.getId());
        }
    }
}
