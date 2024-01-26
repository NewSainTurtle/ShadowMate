package com.newsainturtle.shadowmate.user.repository;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(final String nickname);
    User findByNicknameAndWithdrawalIsFalse(final String nickname);
    User findByEmailAndSocialLogin(final String email, final SocialType socialType);
    User findByIdAndWithdrawalIsFalse(final Long userId);
    User findByNicknameAndPlannerAccessScopeAndWithdrawalIsFalse(final String nickname, final PlannerAccessScope plannerAccessScope);
    User findByEmailAndSocialLoginAndWithdrawalIsFalse(final String email, final SocialType socialType);

    @Query("SELECT u.introduction FROM User u WHERE u.id = :userId")
    String findIntroduction(@Param("userId") final long userId);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.nickname = :nickname, u.profileImage = :profileImage, u.statusMessage = :statusMessage where  u.id = :userId")
    void updateUser(@Param("nickname") final String nickname, @Param("profileImage") final String profileImage, @Param("statusMessage") final String statusMessage, @Param("userId") final long userId);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.password = :password where u.id = :userId")
    void updatePassword(@Param("password") final String password, @Param("userId") final long userId);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.introduction = :introduction where u.id = :userId")
    void updateIntroduction(@Param("introduction") final String introduction, @Param("userId") final long userId);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.withdrawal = TRUE, u.deleteTime = :deleteTime, u.password = 'shadowmate' , u.plannerAccessScope = :plannerAccessScope, u.nickname = :nickname WHERE u.id = :userId")
    void deleteUser(@Param("deleteTime") final LocalDateTime deleteTime, @Param("userId") final long userId, @Param("plannerAccessScope") final PlannerAccessScope plannerAccessScope , @Param("nickname") final String nickname);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.plannerAccessScope = :plannerAccessScope where u.id = :userId")
    void updatePlannerAccessScope(@Param("plannerAccessScope") final PlannerAccessScope plannerAccessScope, @Param("userId") final long userId);
}
