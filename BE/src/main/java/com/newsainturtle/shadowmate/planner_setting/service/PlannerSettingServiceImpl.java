package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryColorListResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryListResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryResponse;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryColorRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlannerSettingServiceImpl implements PlannerSettingService {

    private final CategoryRepository categoryRepository;
    private final CategoryColorRepository categoryColorRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addCategory(final Long userId, final AddCategoryRequest addCategoryRequest) {
        final User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.UNREGISTERED_USER);
        }

        final CategoryColor categoryColor = categoryColorRepository.findById(addCategoryRequest.getCategoryColorId()).orElse(null);
        if (categoryColor == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_CATEGORY_COLOR);
        }

        final Category category = Category.builder()
                .categoryTitle(addCategoryRequest.getCategoryTitle())
                .categoryEmoticon(addCategoryRequest.getCategoryEmoticon())
                .categoryRemove(false)
                .categoryColor(categoryColor)
                .user(user)
                .build();

        categoryRepository.save(category);
    }

    @Override
    public GetCategoryColorListResponse getCategoryColorList() {
        final List<CategoryColor> result = categoryColorRepository.findAll();
        return GetCategoryColorListResponse.builder().categoryColorList(result).build();
    }

    @Override
    public GetCategoryListResponse getCategoryList(final Long userId) {
        final User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.UNREGISTERED_USER);
        }

        final List<Category> result = categoryRepository.findByUser(user);
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
}
