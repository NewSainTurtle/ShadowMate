package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.DailyPlannerLike;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyPlannerLikeRepository extends JpaRepository<DailyPlannerLike, Long> {
    DailyPlannerLike findByUserAndDailyPlanner(final User user, final DailyPlanner dailyPlanner);
    void deleteByUserAndDailyPlanner(final User user, final DailyPlanner dailyPlanner);
}
