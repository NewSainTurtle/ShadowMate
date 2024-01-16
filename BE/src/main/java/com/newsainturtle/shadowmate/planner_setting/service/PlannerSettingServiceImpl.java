package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
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
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

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
        final Category category = categoryRepository.findByUserAndId(user, categoryId);
        if (category == null || category.getCategoryRemove()) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY);
        }
        return category;
    }

    private void checkValidDay(final String startDateStr, final String endDateStr) {
        if (Period.between(stringToLocalDate(startDateStr), stringToLocalDate(endDateStr)).getDays() < 0) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_DATE);
        }
    }

    private void addRoutineTodo(final Routine routine, final LocalDate startDate, final LocalDate endDate, List<String> days) {
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
            while (Period.between(target, endDate).getDays() >= 0) {
                routineTodoRepository.save(RoutineTodo.builder()
                        .day(requestDay)
                        .dailyPlannerDay(localDateToString(target))
                        .routine(routine)
                        .build());
                target = target.plusDays(7);
            }

            routineDayRepository.save(RoutineDay.builder()
                    .routine(routine)
                    .day(requestDay)
                    .build());
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
        final long count = todoRepository.countByCategory(findCategory);

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
            List<FollowRequest> followRequestList = followRequestRepository.findAllByReceiverId(user);
            for (FollowRequest followRequest : followRequestList) {
                followRepository.save(Follow.builder()
                        .followerId(followRequest.getRequesterId())
                        .followingId(user)
                        .build());
            }
            followRequestRepository.deleteAllByReceiverId(user.getId());
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
        final Category category = addRoutineRequest.getCategoryId() == 0L ? null
                : categoryRepository.findByUserAndId(user, addRoutineRequest.getCategoryId());
        final Routine routine = routineRepository.save(Routine.builder()
                .user(user)
                .category(category)
                .routineContent(addRoutineRequest.getRoutineContent())
                .startDay(addRoutineRequest.getStartDay())
                .endDay(addRoutineRequest.getEndDay())
                .build());
        addRoutineTodo(routine, stringToLocalDate(addRoutineRequest.getStartDay()), stringToLocalDate(addRoutineRequest.getEndDay()), addRoutineRequest.getDays());
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
}
