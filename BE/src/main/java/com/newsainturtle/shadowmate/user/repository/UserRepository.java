package com.newsainturtle.shadowmate.user.repository;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(final String email);

    User findByNickname(final String nickname);
  
    User findByEmailAndSocialLogin(final String email, final SocialType socialType);

    User findByIdAndWithdrawalIsFalse(final Long userId);

    User findByNicknameAndPlannerAccessScope(final String nickname, final PlannerAccessScope plannerAccessScope);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.nickname = :nickname, u.profileImage = :profileImage, u.statusMessage = :statusMessage where  u.id = :userId")
    void updateUser(@Param("nickname") final String nickname, @Param("profileImage") final String profileImage, @Param("statusMessage") final String statusMessage, @Param("userId") final long userId);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.password = :password where u.id = :userId")
    void updatePassword(@Param("password") final String password, @Param("userId") long userId);
}
