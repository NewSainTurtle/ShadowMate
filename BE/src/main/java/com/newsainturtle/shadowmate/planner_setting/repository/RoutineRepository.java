package com.newsainturtle.shadowmate.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.Routine;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    Routine[] findAllByUser(final User user);
    Routine findByIdAndUser(final Long id, final User user);
    void deleteByIdAndUser(final Long id, final User user);
    long countByCategory(final Category category);
}
