package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.Weekly;
import com.newsainturtle.shadowmate.planner.entity.WeeklyTodo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyTodoRepository extends JpaRepository<WeeklyTodo, Long> {
    WeeklyTodo findByIdAndWeekly(final Long id, final Weekly weekly);
}
