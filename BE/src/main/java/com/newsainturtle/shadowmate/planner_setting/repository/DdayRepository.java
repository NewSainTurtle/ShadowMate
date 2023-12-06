package com.newsainturtle.shadowmate.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DdayRepository extends JpaRepository<Dday, Long> {
    List<Dday> findByUserOrderByDdayDateDesc(final User user);
    Dday findByUserAndId(final User user, final Long id);
    void deleteByUserAndId(final User user, final Long id);
    Dday findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(final User user, final String today);
    Dday findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(final User user, final String today);
}
