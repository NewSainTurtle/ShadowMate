package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.VisitorBook;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorBookRepository extends JpaRepository<VisitorBook, Long> {
    VisitorBook findByIdAndOwnerId(final long visitorBookId, final long ownerId);
    List<VisitorBook> findTop10ByOwnerAndIdLessThanOrderByIdDesc(final User owner, final long lastVisitorBookId);
    List<VisitorBook> findTop10ByOwnerOrderByIdDesc(final User owner);

    @Modifying(clearAutomatically = true)
    @Query("delete from VisitorBook v where v.visitor.id = :visitorId")
    void deleteAllByVisitorId(@Param("visitorId") final long visitorId);
}
