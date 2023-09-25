package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyPlannerServiceTest {

    @InjectMocks
    private DailyPlannerServiceImpl dailyPlannerServiceImpl;

    @Mock
    private DailyPlannerRepository dailyPlannerRepository;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private CategoryRepository categoryRepository;

    final User user = User.builder()
            .email("test@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @Nested
    class 일일플래너할일등록 {

        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf("2023-09-25"))
                .user(user)
                .build();

        @Test
        public void 실패_유효하지않은카테고리ID() {
            //given
            final AddDailyTodoRequest request = AddDailyTodoRequest.builder()
                    .date("2023-09-25")
                    .categoryId(1L)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .build();
            doReturn(null).when(categoryRepository).findByUserAndId(user, request.getCategoryId());

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> dailyPlannerServiceImpl.addDailyTodo(user, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_CATEGORY);
        }

        @Test
        public void 성공_카테고리Null() {
            //given
            final AddDailyTodoRequest request = AddDailyTodoRequest.builder()
                    .date("2023-09-25")
                    .categoryId(0L)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .build();
            final Todo todo = Todo.builder()
                    .id(1L)
                    .category(null)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
            doReturn(todo).when(todoRepository).save(any(Todo.class));

            //when
            final AddDailyTodoResponse addDailyTodoResponse = dailyPlannerServiceImpl.addDailyTodo(user, request);

            //then
            assertThat(addDailyTodoResponse.getTodoId()).isNotNull();

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
            verify(todoRepository, times(1)).save(any(Todo.class));
        }

        @Test
        public void 성공() {
            //given
            final AddDailyTodoRequest request = AddDailyTodoRequest.builder()
                    .date("2023-09-25")
                    .categoryId(1L)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .build();
            final CategoryColor categoryColor = CategoryColor.builder()
                    .categoryColorCode("D9B5D9")
                    .build();
            final Category category = Category.builder()
                    .id(1L)
                    .categoryColor(categoryColor)
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build();
            final Todo todo = Todo.builder()
                    .id(1L)
                    .category(category)
                    .todoContent("수능완성 수학 과목별 10문제")
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .build();
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any());
            doReturn(category).when(categoryRepository).findByUserAndId(any(), any(Long.class));
            doReturn(todo).when(todoRepository).save(any(Todo.class));

            //when
            final AddDailyTodoResponse addDailyTodoResponse = dailyPlannerServiceImpl.addDailyTodo(user, request);

            //then
            assertThat(addDailyTodoResponse.getTodoId()).isNotNull();

            //verify
            verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any());
            verify(categoryRepository, times(1)).findByUserAndId(any(), any(Long.class));
            verify(todoRepository, times(1)).save(any(Todo.class));
        }

    }

}
