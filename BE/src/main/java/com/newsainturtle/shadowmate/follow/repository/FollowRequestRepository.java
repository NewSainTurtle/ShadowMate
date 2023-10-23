package com.newsainturtle.shadowmate.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    FollowRequest findByRequesterIdAndReceiverId(final User requester, final User receiver);

    List<FollowRequest> findAllByReceiverId(final User receiver);

    void deleteByRequesterIdAndReceiverId(final User requester, final User receiver);
}
