package com.newsainturtle.shadowmate.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DdayRepository extends JpaRepository<Dday, Long> {
}
