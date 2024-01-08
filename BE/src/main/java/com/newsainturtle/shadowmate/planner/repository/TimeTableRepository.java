package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.TimeTable;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    void deleteByIdAndTodoId(final Long id, final Long todoId);
    List<TimeTable> findAllByTodo(final Todo todo);

    @Modifying(clearAutomatically = true)
    @Query("delete from TimeTable t where t.todo.id = :todoId")
    void deleteAllByTodoId(@Param("todoId") final long todoId);
}
