package com.newsainturtle.shadowmate.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Routine;
import com.newsainturtle.shadowmate.planner_setting.entity.RoutineDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineDayRepository extends JpaRepository<RoutineDay, Long> {
    RoutineDay[] findAllByRoutine(final Routine routine);
}
