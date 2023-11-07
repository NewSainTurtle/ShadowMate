package com.newsainturtle.shadowmate.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    FollowRequest findByRequesterIdAndReceiverId(final User requester, final User receiver);

    List<FollowRequest> findAllByReceiverId(final User receiver);

    void deleteByRequesterIdAndReceiverId(final User requester, final User receiver);

    @Modifying(clearAutomatically = true)
    @Query("delete from FollowRequest f where f.receiverId.id = :receiverId")
    void deleteAllByReceiverId(@Param("receiverId") final long receiverId);
}
