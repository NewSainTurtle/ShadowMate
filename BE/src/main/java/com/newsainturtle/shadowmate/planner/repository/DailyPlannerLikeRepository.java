package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.DailyPlannerLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyPlannerLikeRepository extends JpaRepository<DailyPlannerLike, Long> {
    DailyPlannerLike findByUserIdAndDailyPlanner(final Long userId, final DailyPlanner dailyPlanner);
}
