package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.VisitorBook;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorBookRepository extends JpaRepository<VisitorBook, Long> {
    VisitorBook findByIdAndOwnerId(final long visitorBookId, final long ownerId);
    List<VisitorBook> findTop5ByOwnerAndIdLessThanOrderByIdDesc(final User owner, final long lastVisitorBookId);
    List<VisitorBook> findTop5ByOwnerOrderByIdDesc(final User owner);
}
