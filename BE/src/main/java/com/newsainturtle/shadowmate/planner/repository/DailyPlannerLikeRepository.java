package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.DailyPlannerLike;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyPlannerLikeRepository extends JpaRepository<DailyPlannerLike, Long> {
    boolean existsByUserAndDailyPlanner(final User user, final DailyPlanner dailyPlanner);
    void deleteByUserAndDailyPlanner(final User user, final DailyPlanner dailyPlanner);
    long countByDailyPlanner(final DailyPlanner dailyPlanner);
    long countByDailyPlannerIdIn(final List<Long> dailyPlannerIdList);
}
