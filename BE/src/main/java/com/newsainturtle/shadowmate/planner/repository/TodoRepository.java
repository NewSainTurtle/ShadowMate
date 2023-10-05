package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    void deleteByIdAndAndDailyPlanner(final Long id, final DailyPlanner dailyPlanner);
    long countByCategory(final Category category);
}
