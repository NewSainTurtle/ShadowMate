package com.newsainturtle.shadowmate.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    FollowRequest findByRequesterIdAndReceiverId(final User requester, final User receiver);
}
