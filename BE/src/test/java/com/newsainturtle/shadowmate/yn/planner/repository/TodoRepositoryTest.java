package com.newsainturtle.shadowmate.yn.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.TimeTable;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TodoRepositoryTest {

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
    void init() {
        user = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay("2023-09-25")
                .user(user)
                .build());
    }

    @Nested
    class 일일플래너_할일등록 {

        @Test
        void 카테고리Null() {
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
        void 카테고리있음() {
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
    void 일일플래너_할일삭제() {
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
        todoRepository.deleteByIdAndDailyPlanner(saveTodo.getId(), dailyPlanner);
        final Todo findTodo = todoRepository.findById(todo.getId()).orElse(null);

        //then
        assertThat(findTodo).isNull();
    }

    @Test
    void 일일플래너_할일삭제_타임테이블있는경우() {
        //given
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        final LocalDateTime startTime = LocalDateTime.parse("2023-10-06 16:10", formatter);
        final LocalDateTime endTime = LocalDateTime.parse("2023-10-06 18:30", formatter);
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
        final TimeTable timeTable = TimeTable.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
        saveTodo.setTimeTable(timeTable);

        //when
        todoRepository.deleteByIdAndDailyPlanner(saveTodo.getId(), dailyPlanner);
        final Todo findTodo = todoRepository.findById(todo.getId()).orElse(null);

        //then
        assertThat(findTodo).isNull();
    }

    @Test
    void 일일플래너_할일수정() {
        //given
        final Category category1 = categoryRepository.save(Category.builder()
                .categoryColor(categoryColorRepository.findById(1L).orElse(null))
                .user(user)
                .categoryTitle("수학")
                .categoryRemove(false)
                .categoryEmoticon("🍅")
                .build());
        final Category category2 = categoryRepository.save(Category.builder()
                .categoryColor(categoryColorRepository.findById(1L).orElse(null))
                .user(user)
                .categoryTitle("국어")
                .categoryRemove(false)
                .categoryEmoticon("🌀")
                .build());
        final Todo todo = Todo.builder()
                .category(category1)
                .todoContent("수능완성 수학 과목별 10문제")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build();
        final Todo saveTodo = todoRepository.save(todo);

        //when
        final Todo findTodo = todoRepository.findByIdAndDailyPlanner(saveTodo.getId(), dailyPlanner);
        final Todo changeTodo = todoRepository.save(Todo.builder()
                .id(findTodo.getId())
                .createTime(findTodo.getCreateTime())
                .todoContent("비문학 2문제 풀기")
                .category(category2)
                .todoStatus(TodoStatus.COMPLETE)
                .dailyPlanner(findTodo.getDailyPlanner())
                .build());

        //then
        assertThat(changeTodo).isNotNull();
        assertThat(changeTodo.getId()).isEqualTo(findTodo.getId());
        assertThat(changeTodo.getTodoContent()).isEqualTo("비문학 2문제 풀기");
        assertThat(changeTodo.getCategory()).isEqualTo(category2);
        assertThat(changeTodo.getTodoStatus()).isEqualTo(TodoStatus.COMPLETE);
        assertThat(changeTodo.getDailyPlanner()).isEqualTo(dailyPlanner);
        assertThat(changeTodo.getCreateTime()).isNotEqualTo(changeTodo.getUpdateTime());
    }

    @Test
    void 일일플래너리스트조회() {
        //given
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        final Category category = categoryRepository.save(Category.builder()
                .categoryColor(categoryColorRepository.findById(1L).orElse(null))
                .user(user)
                .categoryTitle("국어")
                .categoryRemove(false)
                .categoryEmoticon("🍅")
                .build());

        final Todo saveTodo1 = todoRepository.save(Todo.builder()
                .category(category)
                .todoContent("수능완성 수학 과목별 10문제")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build());
        saveTodo1.setTimeTable(TimeTable.builder()
                .startTime(LocalDateTime.parse("2023-10-06 16:10", formatter))
                .endTime(LocalDateTime.parse("2023-10-06 18:30", formatter))
                .build());

        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("국어")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build());
        todoRepository.save(Todo.builder()
                .category(category)
                .todoContent("개념원리 1단원 문제 풀기")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build());

        final Todo saveTodo4 = todoRepository.save(Todo.builder()
                .category(category)
                .todoContent("개념원리 1단원 문제 풀기")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build());
        saveTodo4.setTimeTable(TimeTable.builder()
                .startTime(LocalDateTime.parse("2023-10-06 23:40", formatter))
                .endTime(LocalDateTime.parse("2023-10-07 01:10", formatter))
                .build());

        //when
        final List<Todo> todoList = todoRepository.findAllByDailyPlanner(dailyPlanner);

        //then
        assertThat(todoList).isNotNull().hasSize(4);
    }

    @Test
    void 일일플래너_할일카운트_전체() {
        //given
        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("수능완성 수학 10문제")
                .todoStatus(TodoStatus.COMPLETE)
                .dailyPlanner(dailyPlanner)
                .build());
        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("국어")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build());
        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("개념원리 1단원 문제 풀기")
                .todoStatus(TodoStatus.INCOMPLETE)
                .dailyPlanner(dailyPlanner)
                .build());
        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("쎈 1단원 문제 풀기")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build());
        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("수능완성 과학 10문제")
                .todoStatus(TodoStatus.COMPLETE)
                .dailyPlanner(dailyPlanner)
                .build());

        //when
        final int totalCount = todoRepository.countByDailyPlanner(dailyPlanner);

        //then
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    void 일일플래너_할일카운트_완료못한할일() {
        //given
        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("수능완성 수학 10문제")
                .todoStatus(TodoStatus.COMPLETE)
                .dailyPlanner(dailyPlanner)
                .build());
        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("국어")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build());
        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("개념원리 1단원 문제 풀기")
                .todoStatus(TodoStatus.INCOMPLETE)
                .dailyPlanner(dailyPlanner)
                .build());
        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("쎈 1단원 문제 풀기")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build());
        todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("수능완성 과학 10문제")
                .todoStatus(TodoStatus.COMPLETE)
                .dailyPlanner(dailyPlanner)
                .build());

        //when
        final int todoCount = todoRepository.countByDailyPlannerAndTodoStatusNot(dailyPlanner, TodoStatus.COMPLETE);

        //then
        assertThat(todoCount).isEqualTo(3);
    }
}
