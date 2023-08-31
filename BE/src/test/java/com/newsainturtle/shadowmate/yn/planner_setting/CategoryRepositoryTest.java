package com.newsainturtle.shadowmate.yn.planner_setting;

import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryColorRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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
                .socialLogin(false)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Nested
    class 카테고리등록 {

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
    }
}
