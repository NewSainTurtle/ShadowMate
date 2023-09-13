package com.newsainturtle.shadowmate.yn.planner_setting;

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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryColorRepository categoryColorRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Test
    public void 카테고리등록() {
        //given
        final Optional<CategoryColor> categoryColor = categoryColorRepository.findById(1L);
        final User user = userRepository.findByEmail("test1234@naver.com");

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

    @Test
    public void 카테고리조회() {
        //given
        final Optional<CategoryColor> categoryColor = categoryColorRepository.findById(1L);
        final User user = userRepository.findByEmail("test1234@naver.com");

        final Category category = Category.builder()
                .categoryColor(categoryColor.get())
                .user(user)
                .categoryTitle("국어")
                .categoryRemove(false)
                .categoryEmoticon("🍅")
                .build();
        categoryRepository.save(category);

        //when
        final List<Category> categoryList = categoryRepository.findByUser(user);

        //then
        assertThat(categoryList).isNotNull();
        assertThat(categoryList.size()).isEqualTo(1);
    }

    @Nested
    class 카테고리수정{
        final Optional<CategoryColor> categoryColor = categoryColorRepository.findById(1L);
        final User user = userRepository.findByEmail("test1234@naver.com");

        final Category category = Category.builder()
                .categoryColor(categoryColor.get())
                .user(user)
                .categoryTitle("국어")
                .categoryRemove(false)
                .categoryEmoticon("🍅")
                .build();

        @Test
        public void 카테고리_이름수정(){
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
        public void 카테고리_색상수정(){
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
        public void 카테고리_이모티콘수정(){
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

}
