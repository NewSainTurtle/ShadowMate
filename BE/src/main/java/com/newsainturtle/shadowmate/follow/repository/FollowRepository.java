package com.newsainturtle.shadowmate.follow.repository;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> findAllByFollower(final User follower);
    List<Follow> findAllByFollowing(final User following);
    Follow findByFollowingAndFollower(final User following, final User follower);
    void deleteByFollowingAndFollower(final User user, final User target);
    Long countByFollower(final User follower);
    Long countByFollowing(final User following);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Follow f WHERE f.follower = :follower OR f.following = :following")
    void deleteAllByFollowingOrFollower(@Param("follower") final User follower, @Param("following") final User following);
}
