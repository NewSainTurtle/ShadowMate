package com.newsainturtle.shadowmate.user.repository;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(final String email);

    User findByNickname(final String nickname);
  
    User findByEmailAndSocialLogin(final String email, final SocialType socialType);

    User findByIdAndWithdrawalIsFalse(final Long userId);

    User findByNicknameAndPlannerAccessScope(final String nickname, final PlannerAccessScope plannerAccessScope);
}
