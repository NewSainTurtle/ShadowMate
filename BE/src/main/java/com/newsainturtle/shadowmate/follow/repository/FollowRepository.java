package com.newsainturtle.shadowmate.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findAllByFollowerId(final User user);

    User findByFollowerIdAndFollowingId(final User follower, final User following);
}
