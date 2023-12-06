package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.Weekly;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface WeeklyRepository extends JpaRepository<Weekly, Long> {
    Weekly findByUserAndStartDayAndEndDay(final User user, final LocalDate startDay, final LocalDate endDay);
}
