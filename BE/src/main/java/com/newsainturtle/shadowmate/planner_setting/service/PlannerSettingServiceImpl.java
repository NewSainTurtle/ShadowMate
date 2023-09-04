package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.GetCategoryColorListResponse;
import com.newsainturtle.shadowmate.planner_setting.dto.SetAccessScopeRequest;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryColorRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void addCategory(Long userId, AddCategoryRequest addCategoryRequest) {
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
    public GetCategoryColorListResponse getCategoryList() {
        final List<CategoryColor> result = categoryColorRepository.findAll();
        return GetCategoryColorListResponse.builder().categoryColorList(result).build();
    }

    @Override
    @Transactional
    public void setAccessScope(final Long userId, final User user, final SetAccessScopeRequest setAccessScopeRequest) {
        if (!userId.equals(user.getId())) {
            throw new PlannerSettingException(PlannerSettingErrorResult.UNREGISTERED_USER);
        }
        final PlannerAccessScope accessScope = PlannerAccessScope.parsing(setAccessScopeRequest.getPlannerAccessScope());
        if (accessScope == null) {
            throw new PlannerSettingException(PlannerSettingErrorResult.INVALID_PLANNER_ACCESS_SCOPE);
        }

        final User changeUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .socialLogin(user.getSocialLogin())
                .nickname(user.getNickname())
                .plannerAccessScope(accessScope)
                .withdrawal(user.getWithdrawal())
                .createTime(user.getCreateTime())
                .build();

        userRepository.save(changeUser);
    }
}
