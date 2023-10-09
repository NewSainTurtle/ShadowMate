package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.TimeTable;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    TimeTable findByTodo(final Todo todo);
}
