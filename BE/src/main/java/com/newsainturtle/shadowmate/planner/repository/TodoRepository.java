package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    Todo findByIdAndDailyPlanner(final Long id, final DailyPlanner dailyPlanner);
    void deleteById(final Long id);
    long countByCategory(final Category category);
    List<Todo> findAllByDailyPlanner(final DailyPlanner dailyPlanner);
    int countByDailyPlanner(final DailyPlanner dailyPlanner);
    int countByDailyPlannerAndTodoStatusNot(final DailyPlanner dailyPlanner, final TodoStatus todoStatus);
}
