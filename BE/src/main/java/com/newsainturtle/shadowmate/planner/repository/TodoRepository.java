package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.dto.response.TodoIndexResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    Todo findByIdAndDailyPlanner(final Long id, final DailyPlanner dailyPlanner);
    void deleteById(final Long id);
    long countByCategory(final Category category);
    List<Todo> findAllByDailyPlannerOrderByTodoIndex(final DailyPlanner dailyPlanner);
    int countByDailyPlanner(final DailyPlanner dailyPlanner);
    int countByDailyPlannerAndTodoStatusNot(final DailyPlanner dailyPlanner, final TodoStatus todoStatus);
    TodoIndexResponse findTopByDailyPlannerOrderByTodoIndexDesc(final DailyPlanner dailyPlanner);
    TodoIndexResponse findTopByDailyPlannerAndTodoIndexGreaterThanOrderByTodoIndex(final DailyPlanner dailyPlanner, final double todoIndex);

    @Modifying(clearAutomatically = true)
    @Query("update Todo t set t.todoContent = :todoContent, t.category = :category, t.todoStatus = :todoStatus , t.updateTime = :updateTime where t.id = :todoId")
    void updateAllByTodoId(@Param("todoContent") final String todoContent, @Param("category") final Category category,
                           @Param("todoStatus") final TodoStatus todoStatus, @Param("updateTime") final LocalDateTime updateTime, @Param("todoId") final long todoId);
}
