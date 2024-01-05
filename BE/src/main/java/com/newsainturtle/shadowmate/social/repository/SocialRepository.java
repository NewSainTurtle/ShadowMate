package com.newsainturtle.shadowmate.social.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.social.entity.Social;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SocialRepository extends JpaRepository<Social, Long> {

    @Query("SELECT s FROM Social s where s.deleteTime is null order by s.dailyPlannerDay desc, s.createTime desc")
    List<Social> findAllByDeleteTimeIsNullSortLatest(final Pageable pageable);

    @Query("SELECT s FROM Social s LEFT JOIN fetch DailyPlannerLike l on s.dailyPlanner.id = l.dailyPlanner.id where s.deleteTime is null GROUP BY s.id order by COUNT(l.dailyPlanner.id) desc, s.dailyPlannerDay desc, s.createTime desc")
    List<Social> findAllByDeleteTimeIsNullSortPopularity(final Pageable pageable);

    Integer countByDeleteTimeIsNull();

    @Query("SELECT s FROM Social s where s.ownerId = :id and s.deleteTime is null order by s.dailyPlannerDay desc, s.createTime desc")
    List<Social> findAllByOwnerIdAndDeleteTimeIsNullSortLatest(@Param("id") final long id, final Pageable pageable);

    @Query("SELECT s FROM Social s LEFT JOIN fetch DailyPlannerLike l on s.dailyPlanner.id = l.dailyPlanner.id where s.ownerId = :id and s.deleteTime is null GROUP BY s.id order by COUNT(l.dailyPlanner.id) desc, s.dailyPlannerDay desc, s.createTime desc")
    List<Social> findAllByOwnerIdAndDeleteTimeIsNullSortPopularity(@Param("id") final long id, final Pageable pageable);

    Integer countByOwnerIdAndDeleteTimeIsNull(final long ownerId);

    Social findByDailyPlanner(final DailyPlanner dailyPlanner);

    Social findByDailyPlannerAndDeleteTimeIsNull(final DailyPlanner dailyPlanner);

    @Modifying(clearAutomatically = true)
    @Query("update Social s set s.deleteTime = :time where s.dailyPlanner in (:dailyPlanners)")
    void updateDeleteTimeAll(@Param("time") final LocalDateTime time, @Param("dailyPlanners") final List<DailyPlanner> dailyPlanners);

}
