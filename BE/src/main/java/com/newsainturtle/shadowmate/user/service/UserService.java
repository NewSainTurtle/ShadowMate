package com.newsainturtle.shadowmate.user.service;

import com.newsainturtle.shadowmate.user.dto.request.UpdateIntroductionRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdatePasswordRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdateUserRequest;
import com.newsainturtle.shadowmate.user.dto.response.ProfileResponse;
import com.newsainturtle.shadowmate.user.dto.response.SearchIntroductionResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    ProfileResponse getProfile(final Long userId);
    SearchIntroductionResponse searchIntroduction(final Long userId);
    void updateUser(final User user, final UpdateUserRequest updateUserRequest);
    void updatePassword(final User user, final UpdatePasswordRequest updatePasswordRequest);
    void updateIntroduction(final Long userId, final UpdateIntroductionRequest updateIntroductionRequest);
    void deleteUser(final User user);
    User getUserByNickname(final String nickname);
    User getUserById(final long userId);
    User getUserByNicknameAndScopePublic(String nickname);
    void updatePlannerAccessScope(final Long userId, final PlannerAccessScope plannerAccessScope);
}
