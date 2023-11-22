package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.dto.request.*;
import com.newsainturtle.shadowmate.planner_setting.dto.response.*;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryColorRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlannerSettingServiceImpl implements PlannerSettingService {

    private final CategoryRepository categoryRepository;
    private final CategoryColorRepository categoryColorRepository;
    private final DdayRepository ddayRepository;
    private final TodoRepository todoRepository;
    private final FollowRequestRepository followRequestRepository;
    private final FollowRepository followRepository;
    private final SocialRepository socialRepository;
    private final DailyPlannerRepository dailyPlannerRepository;

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
        user.updatePlannerAccessScope(accessScope);
    }

    @Override
    @Transactional
    public AddDdayResponse addDday(final User user, final AddDdayRequest addDdayRequest) {
        final Dday dday = Dday.builder()
                .ddayDate(Date.valueOf(addDdayRequest.getDdayDate()))
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
        findDday.updateDdayDateAndTitle(Date.valueOf(updateDdayRequest.getDdayDate()), updateDdayRequest.getDdayTitle());
    }
}
