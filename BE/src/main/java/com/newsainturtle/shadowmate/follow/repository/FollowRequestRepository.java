package com.newsainturtle.shadowmate.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    FollowRequest findByRequesterAndReceiver(final User requester, final User receiver);
    List<FollowRequest> findAllByReceiver(final User receiver);
    void deleteByRequesterAndReceiver(final User requester, final User receiver);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM FollowRequest f WHERE f.requester = :requester OR f.receiver = :receiver")
    void deleteAllByRequesterOrReceiver(@Param("requester") final User requester, @Param("receiver") final User receiver);

    @Modifying(clearAutomatically = true)
    @Query("delete from FollowRequest f where f.receiver.id = :receiverId")
    void deleteAllByReceiver(@Param("receiverId") final long receiverId);
}
