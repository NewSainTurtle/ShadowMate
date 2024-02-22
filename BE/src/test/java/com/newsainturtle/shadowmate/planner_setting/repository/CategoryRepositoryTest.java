package com.newsainturtle.shadowmate.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryColorRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryColorRepository categoryColorRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Category category;
    private CategoryColor categoryColor;

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        categoryColor = categoryColorRepository.findById(1L).get();
        category = Category.builder()
                .categoryColor(categoryColor)
                .user(user)
                .categoryTitle("국어")
                .categoryRemove(false)
                .categoryEmoticon("🍅")
                .build();
    }

    @Test
    void 카테고리등록() {
        //given

        //when
        final Category saveCategory = categoryRepository.save(category);

        //then
        assertThat(saveCategory).isNotNull();
        assertThat(saveCategory.getUser().getId()).isEqualTo(user.getId());
        assertThat(saveCategory.getCategoryColor().getCategoryColorId()).isEqualTo(categoryColor.getCategoryColorId());
        assertThat(saveCategory.getCategoryRemove()).isFalse();
        assertThat(saveCategory.getCategoryEmoticon()).isEqualTo("🍅");
    }

    @Nested
    class 카테고리조회 {
        @Test
        void 카테고리0개() {
            //given

            //when
            final List<Category> categoryList = categoryRepository.findByUserAndAndCategoryRemoveIsFalse(user);

            //then
            assertThat(categoryList).isNotNull().isEmpty();
        }

        @Test
        void 카테고리1개() {
            //given
            categoryRepository.save(category);

            //when
            final List<Category> categoryList = categoryRepository.findByUserAndAndCategoryRemoveIsFalse(user);

            //then
            assertThat(categoryList).isNotNull().hasSize(1);
        }

        @Test
        void 카테고리1개_삭제된카테고리1개() {
            //given
            categoryRepository.save(category);
            categoryRepository.save(Category.builder()
                    .categoryColor(categoryColor)
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(true)
                    .categoryEmoticon("🍅")
                    .build());

            //when
            final List<Category> categoryList = categoryRepository.findByUserAndAndCategoryRemoveIsFalse(user);

            //then
            assertThat(categoryList).isNotNull().hasSize(1);
        }
    }

    @Nested
    class 카테고리수정 {

        @Test
        void 카테고리_이름수정() {
            //given
            final String changeTitle = "수학";
            final Category saveCategory = categoryRepository.save(category);
            final Category changCategory = Category.builder()
                    .id(saveCategory.getId())
                    .categoryColor(saveCategory.getCategoryColor())
                    .user(saveCategory.getUser())
                    .categoryTitle(changeTitle)
                    .categoryRemove(saveCategory.getCategoryRemove())
                    .categoryEmoticon(saveCategory.getCategoryEmoticon())
                    .build();

            //when
            final Category result = categoryRepository.save(changCategory);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getCategoryTitle()).isEqualTo(changeTitle);
            assertThat(result.getCategoryTitle()).isNotEqualTo("국어");
            assertThat(result.getCategoryColor()).isEqualTo(categoryColor);
        }

        @Test
        void 카테고리_색상수정() {
            //given
            final Category saveCategory = categoryRepository.save(category);
            final CategoryColor changeCategoryColor = categoryColorRepository.findById(2L).get();
            final Category changCategory = Category.builder()
                    .id(saveCategory.getId())
                    .categoryColor(changeCategoryColor)
                    .user(saveCategory.getUser())
                    .categoryTitle(saveCategory.getCategoryTitle())
                    .categoryRemove(saveCategory.getCategoryRemove())
                    .categoryEmoticon(saveCategory.getCategoryEmoticon())
                    .build();

            //when
            final Category result = categoryRepository.save(changCategory);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getCategoryColor()).isEqualTo(changeCategoryColor);
            assertThat(result.getCategoryColor()).isNotEqualTo(categoryColor);
            assertThat(result.getCategoryTitle()).isEqualTo("국어");
        }

        @Test
        void 카테고리_이모티콘수정() {
            //given
            final Category saveCategory = categoryRepository.save(category);
            final String changeEmoticon = "☘️";
            final Category changCategory = Category.builder()
                    .id(saveCategory.getId())
                    .categoryColor(saveCategory.getCategoryColor())
                    .user(saveCategory.getUser())
                    .categoryTitle(saveCategory.getCategoryTitle())
                    .categoryRemove(saveCategory.getCategoryRemove())
                    .categoryEmoticon(changeEmoticon)
                    .build();

            //when
            final Category result = categoryRepository.save(changCategory);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getCategoryEmoticon()).isEqualTo(changeEmoticon);
            assertThat(result.getCategoryEmoticon()).isNotEqualTo("🍅");
            assertThat(result.getCategoryColor()).isEqualTo(categoryColor);
        }
    }

    @Nested
    class 카테고리삭제 {

        @Test
        void 카테고리_삭제필드변경() {
            //given
            final String changeTitle = "수학";
            final Category saveCategory = categoryRepository.save(category);
            final Category changCategory = Category.builder()
                    .id(saveCategory.getId())
                    .createTime(saveCategory.getCreateTime())
                    .deleteTime(LocalDateTime.now())
                    .categoryColor(saveCategory.getCategoryColor())
                    .user(saveCategory.getUser())
                    .categoryTitle(changeTitle)
                    .categoryRemove(true)
                    .categoryEmoticon(saveCategory.getCategoryEmoticon())
                    .build();

            //when
            final Category result = categoryRepository.save(changCategory);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getCategoryRemove()).isTrue();
            assertThat(result.getDeleteTime()).isNotNull();
        }

        @Test
        void 카테고리_DB삭제() {
            //given
            final Category saveCategory = categoryRepository.save(category);

            //when
            categoryRepository.deleteByUserAndId(user, saveCategory.getId());
            final Category findCategory = categoryRepository.findById(saveCategory.getId()).orElse(null);

            //then
            assertThat(findCategory).isNull();
        }

    }

}
