package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyPlannerRepository extends JpaRepository<DailyPlanner, Long> {
    DailyPlanner findByUserAndDailyPlannerDay(final User user, final String dailyPlannerDay);
    List<DailyPlanner> findAllByUser(final User user);
}
