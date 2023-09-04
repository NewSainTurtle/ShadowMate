package com.newsainturtle.shadowmate.yn.planner_setting;

import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryColorRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlannerSettingServiceTest {

    @InjectMocks
    private PlannerSettingServiceImpl plannerSettingService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryColorRepository categoryColorRepository;

    @Mock
    private UserRepository userRepository;


    @Nested
    class 카테고리등록 {
        final User user = User.builder()
                .email("test@test.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build();
        final CategoryColor categoryColor = CategoryColor.builder()
                .categoryColorCode("D9B5D9")
                .build();
        final Long userId = 1L;
        final AddCategoryRequest request = AddCategoryRequest.builder()
                .categoryTitle("국어")
                .categoryEmoticon("🍅")
                .categoryColorId(1L)
                .build();
        final Category category = Category.builder()
                .categoryTitle(request.getCategoryTitle())
                .categoryEmoticon(request.getCategoryEmoticon())
                .categoryRemove(false)
                .categoryColor(categoryColor)
                .user(user)
                .build();

        @Test
        public void 실패_없는사용자() {
            //given
            doReturn(Optional.empty()).when(userRepository).findById(userId);

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.addCategory(userId, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.UNREGISTERED_USER);
        }

        @Test
        public void 실패_없는카테고리색상() {
            //given

            doReturn(Optional.of(user)).when(userRepository).findById(userId);
            doReturn(Optional.empty()).when(categoryColorRepository).findById(request.getCategoryColorId());

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.addCategory(userId, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY_COLOR);
        }

        @Test
        public void 성공_카테고리등록_이모티콘없음() {
            //given
            final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                    .categoryTitle("국어")
                    .categoryEmoticon(null)
                    .categoryColorId(1L)
                    .build();

            doReturn(Optional.of(user)).when(userRepository).findById(userId);
            doReturn(Optional.of(categoryColor)).when(categoryColorRepository).findById(request.getCategoryColorId());
            doReturn(category).when(categoryRepository).save(any(Category.class));

            //when
            plannerSettingService.addCategory(userId, addCategoryRequest);

            //then

            //verify
            verify(userRepository, times(1)).findById(userId);
            verify(categoryColorRepository, times(1)).findById(request.getCategoryColorId());
            verify(categoryRepository, times(1)).save(any(Category.class));
        }

        @Test
        public void 성공_카테고리등록_이모티콘있음() {
            //given
            doReturn(Optional.of(user)).when(userRepository).findById(userId);
            doReturn(Optional.of(categoryColor)).when(categoryColorRepository).findById(request.getCategoryColorId());
            doReturn(category).when(categoryRepository).save(any(Category.class));

            //when
            plannerSettingService.addCategory(userId, request);

            //then

            //verify
            verify(userRepository, times(1)).findById(userId);
            verify(categoryColorRepository, times(1)).findById(request.getCategoryColorId());
            verify(categoryRepository, times(1)).save(any(Category.class));
        }
    }


}