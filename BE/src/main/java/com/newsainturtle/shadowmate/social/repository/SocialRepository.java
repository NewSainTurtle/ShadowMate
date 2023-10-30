package com.newsainturtle.shadowmate.social.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.social.entity.Social;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SocialRepository extends JpaRepository<Social, Long> {

    @Query(value = "SELECT s FROM Social s WHERE s.deleteTime is null")
    List<Social> findAllByDeleteTime();

    Social findByDailyPlanner(final DailyPlanner dailyPlanner);

    @Modifying(clearAutomatically = true)
    @Query("update Social s set s.deleteTime = :time where s.dailyPlanner in (:dailyPlanners)")
    void updateDeleteTimeAll(@Param("time") final LocalDateTime time, @Param("dailyPlanners") final List<DailyPlanner> dailyPlanners);
}
