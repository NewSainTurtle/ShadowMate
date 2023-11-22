package com.newsainturtle.shadowmate.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findAllByFollowerId(final User user);

    List<Follow> findAllByFollowingId(final User user);

    Follow findByFollowerIdAndFollowingId(final User follower, final User following);

    void deleteByFollowingIdAndFollowerId(final User user, final User target);

    Long countByFollowerId(final User user);

    Long countByFollowingId(final User user);
}
