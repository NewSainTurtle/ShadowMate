package com.newsainturtle.shadowmate.yn.planner_setting.repository;

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
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

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Test
    void 카테고리등록() {
        //given
        final Optional<CategoryColor> categoryColor = categoryColorRepository.findById(1L);

        final Category category = Category.builder()
                .categoryColor(categoryColor.get())
                .user(user)
                .categoryTitle("국어")
                .categoryRemove(false)
                .categoryEmoticon("🍅")
                .build();

        //when
        final Category saveCategory = categoryRepository.save(category);

        //then
        assertThat(saveCategory).isNotNull();
        assertThat(saveCategory.getUser().getId()).isEqualTo(user.getId());
        assertThat(saveCategory.getCategoryColor().getCategoryColorId()).isEqualTo(categoryColor.get().getCategoryColorId());
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
            assertThat(categoryList).isNotNull();
            assertThat(categoryList).isEmpty();
        }

        @Test
        void 카테고리1개() {
            //given
            final Optional<CategoryColor> categoryColor = categoryColorRepository.findById(1L);
            categoryRepository.save(Category.builder()
                    .categoryColor(categoryColor.get())
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build());

            //when
            final List<Category> categoryList = categoryRepository.findByUserAndAndCategoryRemoveIsFalse(user);

            //then
            assertThat(categoryList).isNotNull();
            assertThat(categoryList).hasSize(1);
        }

        @Test
        void 카테고리1개_삭제된카테고리1개() {
            //given
            final Optional<CategoryColor> categoryColor = categoryColorRepository.findById(1L);
            categoryRepository.save(Category.builder()
                    .categoryColor(categoryColor.get())
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build());
            categoryRepository.save(Category.builder()
                    .categoryColor(categoryColor.get())
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(true)
                    .categoryEmoticon("🍅")
                    .build());

            //when
            final List<Category> categoryList = categoryRepository.findByUserAndAndCategoryRemoveIsFalse(user);

            //then
            assertThat(categoryList).isNotNull();
            assertThat(categoryList).hasSize(1);
        }
    }

    @Nested
    class 카테고리수정 {
        final Optional<CategoryColor> categoryColor = categoryColorRepository.findById(1L);
        final Category category = Category.builder()
                .categoryColor(categoryColor.get())
                .user(user)
                .categoryTitle("국어")
                .categoryRemove(false)
                .categoryEmoticon("🍅")
                .build();

        @Test
        void 카테고리_이름수정() {
            //given
            final String changeTitle = "수학";
            final Category saveCategory = categoryRepository.save(category);
            final Category findCategory = categoryRepository.findById(saveCategory.getId()).get();
            final Category changCategory = Category.builder()
                    .id(findCategory.getId())
                    .categoryColor(findCategory.getCategoryColor())
                    .user(findCategory.getUser())
                    .categoryTitle(changeTitle)
                    .categoryRemove(findCategory.getCategoryRemove())
                    .categoryEmoticon(findCategory.getCategoryEmoticon())
                    .build();

            //when
            final Category result = categoryRepository.save(changCategory);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getCategoryTitle()).isEqualTo(changeTitle);
            assertThat(result.getCategoryTitle()).isNotEqualTo("국어");
            assertThat(result.getCategoryColor()).isEqualTo(categoryColor.get());
        }

        @Test
        void 카테고리_색상수정() {
            //given
            final Optional<CategoryColor> changeCategoryColor = categoryColorRepository.findById(2L);
            final Category saveCategory = categoryRepository.save(category);
            final Category findCategory = categoryRepository.findById(saveCategory.getId()).get();
            final Category changCategory = Category.builder()
                    .id(findCategory.getId())
                    .categoryColor(changeCategoryColor.get())
                    .user(findCategory.getUser())
                    .categoryTitle(findCategory.getCategoryTitle())
                    .categoryRemove(findCategory.getCategoryRemove())
                    .categoryEmoticon(findCategory.getCategoryEmoticon())
                    .build();

            //when
            final Category result = categoryRepository.save(changCategory);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getCategoryColor()).isEqualTo(changeCategoryColor.get());
            assertThat(result.getCategoryColor()).isNotEqualTo(categoryColor.get());
            assertThat(result.getCategoryTitle()).isEqualTo("국어");
        }

        @Test
        void 카테고리_이모티콘수정() {
            //given
            final String changeEmoticon = "☘️";
            final Category saveCategory = categoryRepository.save(category);
            final Category findCategory = categoryRepository.findById(saveCategory.getId()).get();
            final Category changCategory = Category.builder()
                    .id(findCategory.getId())
                    .categoryColor(findCategory.getCategoryColor())
                    .user(findCategory.getUser())
                    .categoryTitle(findCategory.getCategoryTitle())
                    .categoryRemove(findCategory.getCategoryRemove())
                    .categoryEmoticon(changeEmoticon)
                    .build();

            //when
            final Category result = categoryRepository.save(changCategory);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getCategoryEmoticon()).isEqualTo(changeEmoticon);
            assertThat(result.getCategoryEmoticon()).isNotEqualTo("🍅");
            assertThat(result.getCategoryColor()).isEqualTo(categoryColor.get());
        }
    }

    @Nested
    class 카테고리삭제 {

        @Test
        void 카테고리_삭제필드변경() {
            //given
            final Optional<CategoryColor> categoryColor = categoryColorRepository.findById(1L);
            final Category category = categoryRepository.save(Category.builder()
                    .categoryColor(categoryColor.get())
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build());
            final String changeTitle = "수학";
            final Category findCategory = categoryRepository.findById(category.getId()).orElse(null);
            final Category changCategory = Category.builder()
                    .id(findCategory.getId())
                    .createTime(findCategory.getCreateTime())
                    .deleteTime(LocalDateTime.now())
                    .categoryColor(findCategory.getCategoryColor())
                    .user(findCategory.getUser())
                    .categoryTitle(changeTitle)
                    .categoryRemove(true)
                    .categoryEmoticon(findCategory.getCategoryEmoticon())
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
            final Optional<CategoryColor> categoryColor = categoryColorRepository.findById(1L);
            final Category category = categoryRepository.save(Category.builder()
                    .categoryColor(categoryColor.get())
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build());

            //when
            categoryRepository.deleteByUserAndId(user, category.getId());
            final Category findCategory = categoryRepository.findById(category.getId()).orElse(null);

            //then
            assertThat(findCategory).isNull();
        }

    }

}
