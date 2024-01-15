package com.newsainturtle.shadowmate.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.RoutineTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineTodoRepository extends JpaRepository<RoutineTodo, Long> {

    @Query("SELECT rt FROM RoutineTodo rt LEFT JOIN fetch Routine r on rt.routine.id = r.id where r.user.id = :userId and rt.dailyPlannerDay = :dailyPlannerDay and rt.todo is null")
    RoutineTodo[] findAllByUserAndDailyPlannerDayAndTodoIsNull(@Param("userId") final Long userId, @Param("dailyPlannerDay") final String dailyPlannerDay);
}
