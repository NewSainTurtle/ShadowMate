package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.planner.dto.response.TodoIndexResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.dto.request.*;
import com.newsainturtle.shadowmate.planner_setting.dto.response.*;
import com.newsainturtle.shadowmate.planner_setting.entity.*;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.repository.*;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlannerSettingServiceImpl extends DateCommonService implements PlannerSettingService {

    private final CategoryRepository categoryRepository;
    private final CategoryColorRepository categoryColorRepository;
    private final DdayRepository ddayRepository;
    private final TodoRepository todoRepository;
    private final FollowRequestRepository followRequestRepository;
    private final FollowRepository followRepository;
    private final SocialRepository socialRepository;
    private final DailyPlannerRepository dailyPlannerRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final RoutineDayRepository routineDayRepository;
    private final RoutineTodoRepository routineTodoRepository;

    private CategoryColor getCategoryColor(final Long categoryColorId) {
        final CategoryColor categoryColor = categoryColorRepository.findById(categoryColorId).orElse(null);
        if (categoryColor == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY_COLOR);
        }
        return categoryColor;
    }

    private Category getCategory(final User user, final Long categoryId) {
        if (categoryId == 0) return null;
        Category category = categoryRepository.findByUserAndId(user, categoryId);
        if (category == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY);
        }
        return category;
    }

    private void checkValidDay(final String startDateStr, final String endDateStr) {
        if (ChronoUnit.DAYS.between(stringToLocalDate(startDateStr), stringToLocalDate(endDateStr)) < 0) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_DATE);
        }
    }

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
        DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, date);
        if (dailyPlanner == null) {
            dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
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

    private boolean[] updateOriginalDayOrder1(final Routine routine,
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

        return checkDays;
    }

    private boolean[] updateOriginalDayOrder2(final User user, final Routine routine,
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

        return checkDays;
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
    public AddCategoryResponse addCategory(final User user, final AddCategoryRequest addCategoryRequest) {
        final CategoryColor categoryColor = getCategoryColor(addCategoryRequest.getCategoryColorId());
        final Category category = Category.builder()
                .categoryTitle(addCategoryRequest.getCategoryTitle())
                .categoryEmoticon(addCategoryRequest.getCategoryEmoticon())
                .categoryRemove(false)
                .categoryColor(categoryColor)
                .user(user)
                .build();

        final Category saveCategory = categoryRepository.save(category);
        return AddCategoryResponse.builder().categoryId(saveCategory.getId()).build();
    }

    @Override
    @Transactional
    public void updateCategory(final User user, final UpdateCategoryRequest updateCategoryRequest) {
        final Category findCategory = getCategory(user, updateCategoryRequest.getCategoryId());
        final CategoryColor categoryColor = getCategoryColor(updateCategoryRequest.getCategoryColorId());
        findCategory.updateCategoryTitleAndEmoticonAndColor(updateCategoryRequest.getCategoryTitle(),
                updateCategoryRequest.getCategoryEmoticon(),
                categoryColor);
    }

    @Override
    @Transactional
    public void removeCategory(final User user, final RemoveCategoryRequest removeCategoryRequest) {
        final Category findCategory = getCategory(user, removeCategoryRequest.getCategoryId());
        final long count = todoRepository.countByCategory(findCategory) + routineRepository.countByCategory(findCategory);

        if (count == 0) {
            categoryRepository.deleteByUserAndId(user, removeCategoryRequest.getCategoryId());
        } else {
            findCategory.deleteCategory();
        }
    }

    @Override
    public GetCategoryColorListResponse getCategoryColorList() {
        final List<CategoryColor> result = categoryColorRepository.findAll();
        return GetCategoryColorListResponse.builder().categoryColorList(result).build();
    }

    @Override
    public GetCategoryListResponse getCategoryList(final User user) {
        final List<Category> result = categoryRepository.findByUserAndAndCategoryRemoveIsFalse(user);
        List<GetCategoryResponse> categoryList = new ArrayList<>();

        for (Category category : result) {
            categoryList.add(GetCategoryResponse.builder()
                    .categoryId(category.getId())
                    .categoryColorCode(category.getCategoryColor().getCategoryColorCode())
                    .categoryEmoticon(category.getCategoryEmoticon())
                    .categoryTitle(category.getCategoryTitle())
                    .build());
        }
        return GetCategoryListResponse.builder().categoryList(categoryList).build();
    }

    @Override
    @Transactional
    public void setAccessScope(final User user, final SetAccessScopeRequest setAccessScopeRequest) {
        final PlannerAccessScope accessScope = PlannerAccessScope.parsing(setAccessScopeRequest.getPlannerAccessScope());
        if (accessScope == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_PLANNER_ACCESS_SCOPE);
        }

        if (!user.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC) && accessScope.equals(PlannerAccessScope.PUBLIC)) {
            List<FollowRequest> followRequestList = followRequestRepository.findAllByReceiver(user);
            for (FollowRequest followRequest : followRequestList) {
                followRepository.save(Follow.builder()
                        .follower(followRequest.getRequester())
                        .following(user)
                        .build());
            }
            followRequestRepository.deleteAllByReceiver(user.getId());
            final List<DailyPlanner> dailyPlanners = dailyPlannerRepository.findAllByUser(user);
            socialRepository.updateDeleteTimeAll(null, dailyPlanners);
        } else if (user.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC) && !accessScope.equals(PlannerAccessScope.PUBLIC)) {
            final List<DailyPlanner> dailyPlanners = dailyPlannerRepository.findAllByUser(user);
            final LocalDateTime time = LocalDateTime.now();
            socialRepository.updateDeleteTimeAll(time, dailyPlanners);
        }
        userRepository.updatePlannerAccessScope(accessScope, user.getId());
    }

    @Override
    @Transactional
    public AddDdayResponse addDday(final User user, final AddDdayRequest addDdayRequest) {
        final Dday dday = Dday.builder()
                .ddayDate(addDdayRequest.getDdayDate())
                .ddayTitle(addDdayRequest.getDdayTitle())
                .user(user)
                .build();

        final Dday saveDday = ddayRepository.save(dday);
        return AddDdayResponse.builder().ddayId(saveDday.getId()).build();
    }

    @Override
    public GetDdayListResponse getDdayList(final User user) {
        final List<Dday> result = ddayRepository.findByUserOrderByDdayDateDesc(user);
        List<GetDdayResponse> ddayList = new ArrayList<>();
        for (Dday dday : result) {
            ddayList.add(GetDdayResponse.builder()
                    .ddayId(dday.getId())
                    .ddayTitle(dday.getDdayTitle())
                    .ddayDate(String.valueOf(dday.getDdayDate()))
                    .build());
        }
        return GetDdayListResponse.builder().ddayList(ddayList).build();
    }

    @Override
    @Transactional
    public void removeDday(final User user, final RemoveDdayRequest removeDdayRequest) {
        ddayRepository.deleteByUserAndId(user, removeDdayRequest.getDdayId());
    }

    @Override
    @Transactional
    public void updateDday(final User user, final UpdateDdayRequest updateDdayRequest) {
        final Dday findDday = ddayRepository.findByUserAndId(user, updateDdayRequest.getDdayId());
        if (findDday == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_DDAY);
        }
        findDday.updateDdayDateAndTitle(updateDdayRequest.getDdayDate(), updateDdayRequest.getDdayTitle());
    }

    @Override
    @Transactional
    public AddRoutineResponse addRoutine(final User user, final AddRoutineRequest addRoutineRequest) {
        checkValidDay(addRoutineRequest.getStartDay(), addRoutineRequest.getEndDay());
        final Category category = getCategory(user, addRoutineRequest.getCategoryId());
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
    public void updateRoutine(final User user, final UpdateRoutineRequest updateRoutineRequest) {
        final Routine routine = routineRepository.findByIdAndUser(updateRoutineRequest.getRoutineId(), user);
        if (routine == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_ROUTINE);
        }
        checkValidDay(updateRoutineRequest.getStartDay(), updateRoutineRequest.getEndDay());
        final Category category = getCategory(user, updateRoutineRequest.getCategoryId());
        if (!updateRoutineRequest.getOrder().equals(1) && !updateRoutineRequest.getOrder().equals(2)) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_ORDER);
        }

        final String today = String.valueOf(LocalDate.now());
        boolean[] checkDays = checkDays(updateRoutineRequest.getDays());

        if (updateRoutineRequest.getOrder().equals(1)) {
            checkDays = updateOriginalDayOrder1(routine, updateRoutineRequest, category, checkDays);
        } else if (updateRoutineRequest.getOrder().equals(2)) {
            checkDays = updateOriginalDayOrder2(user, routine, updateRoutineRequest, category, today, checkDays);
        }

        updateNewDay(routine, updateRoutineRequest.getStartDay(), updateRoutineRequest.getEndDay(),
                updateRoutineRequest.getOrder(), today, checkDays);
        routine.updateDayAndCategoryAndRoutineContent(updateRoutineRequest.getStartDay(), updateRoutineRequest.getEndDay(), category, updateRoutineRequest.getRoutineContent());
    }

}
