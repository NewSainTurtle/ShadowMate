package com.newsainturtle.shadowmate.yn.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
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

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryColorRepository categoryColorRepository;

    private User user;
    private DailyPlanner dailyPlanner;

    @BeforeEach
    public void init() {
        user = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf("2023-09-25"))
                .user(user)
                .build());
    }

    @Nested
    class 일일플래너_할일등록 {

        @Test
        public void 카테고리Null() {
            //given
            final Todo todo = Todo.builder()
                    .category(null)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .build();
            //when
            final Todo saveTodo = todoRepository.save(todo);

            //then
            assertThat(saveTodo).isNotNull();
            assertThat(saveTodo.getTodoStatus()).isEqualTo(TodoStatus.EMPTY);
            assertThat(saveTodo.getTodoContent()).isEqualTo("수능완성 수학 과목별 10문제");
            assertThat(saveTodo.getCategory()).isNull();
            assertThat(saveTodo.getDailyPlanner()).isEqualTo(dailyPlanner);
        }

        @Test
        public void 카테고리있음() {
            //given
            final Category category = categoryRepository.save(Category.builder()
                    .categoryColor(categoryColorRepository.findById(1L).orElse(null))
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build());
            final Todo todo = Todo.builder()
                    .category(category)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .build();
            //when
            final Todo saveTodo = todoRepository.save(todo);

            //then
            assertThat(saveTodo).isNotNull();
            assertThat(saveTodo.getTodoStatus()).isEqualTo(TodoStatus.EMPTY);
            assertThat(saveTodo.getTodoContent()).isEqualTo("수능완성 수학 과목별 10문제");
            assertThat(saveTodo.getCategory()).isEqualTo(category);
            assertThat(saveTodo.getDailyPlanner()).isEqualTo(dailyPlanner);
        }
    }

    @Test
    public void 일일플래너_할일삭제() {
        //given
        final Category category = categoryRepository.save(Category.builder()
                .categoryColor(categoryColorRepository.findById(1L).orElse(null))
                .user(user)
                .categoryTitle("국어")
                .categoryRemove(false)
                .categoryEmoticon("🍅")
                .build());
        final Todo todo = Todo.builder()
                .category(category)
                .todoContent("수능완성 수학 과목별 10문제")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build();
        final Todo saveTodo = todoRepository.save(todo);

        //when
        todoRepository.deleteByIdAndAndDailyPlanner(saveTodo.getId(), dailyPlanner);
        final Todo findTodo = todoRepository.findById(todo.getId()).orElse(null);

        //then
        assertThat(findTodo).isNull();
    }

}
