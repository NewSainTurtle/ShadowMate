package com.newsainturtle.shadowmate.user.service;

import com.newsainturtle.shadowmate.user.dto.*;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ProfileResponse getProfile(final Long userId);

    UserResponse searchNickname(final User user, final String nickname);

    SearchIntroductionResponse searchIntroduction(final Long userId);

    void updateUser(final Long userId, final UpdateUserRequest updateUserRequest);

    void updatePassword(final Long userId, final String oldPassword, final String newPassword);

    void updateIntroduction(final UpdateIntroductionRequest updateIntroductionRequest, final Long userId);

    void deleteUser(final Long userId);
}
