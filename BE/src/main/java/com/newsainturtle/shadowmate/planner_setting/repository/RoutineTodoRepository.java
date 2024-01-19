package com.newsainturtle.shadowmate.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Routine;
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineTodoRepository extends JpaRepository<RoutineTodo, Long> {

    RoutineTodo[] findAllByRoutineAndTodoIsNull(final Routine routine);
    RoutineTodo[] findAllByRoutineAndTodoIsNullAndDay(final Routine routine, final String day);
    RoutineTodo[] findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThan(final Routine routine, final String dailyPlannerDay);
    RoutineTodo[] findAllByRoutineAndTodoIsNullAndDailyPlannerDayBetweenAndDayIn(final Routine routine, final String startDay, final String endDay, final List<String> days);
    RoutineTodo[] findAllByRoutineAndTodoIsNotNull(final Routine routine);
    RoutineTodo[] findAllByRoutineAndTodoIsNotNullAndDay(final Routine routine, final String day);
    RoutineTodo[] findAllByRoutineAndTodoIsNotNullAndDayIn(final Routine routine, final List<String> days);
    RoutineTodo[] findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqual(final Routine routine, final String dailyPlannerDay);
    RoutineTodo[] findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayBetweenAndDayIn(final Routine routine, final String startDay, final String endDay, final List<String> days);

    @Query("SELECT rt FROM RoutineTodo rt LEFT JOIN fetch Routine r on rt.routine.id = r.id where r.user.id = :userId and rt.dailyPlannerDay = :dailyPlannerDay and rt.todo is null")
    RoutineTodo[] findAllByUserAndDailyPlannerDayAndTodoIsNull(@Param("userId") final Long userId, @Param("dailyPlannerDay") final String dailyPlannerDay);

    @Query("SELECT count(rt) FROM RoutineTodo rt LEFT JOIN fetch Routine r on rt.routine.id = r.id where r.user.id = :userId and rt.dailyPlannerDay = :dailyPlannerDay and rt.todo is null")
    int countByUserAndDailyPlannerDayAndTodoIsNull(@Param("userId") final Long userId, @Param("dailyPlannerDay") final String dailyPlannerDay);

    }
