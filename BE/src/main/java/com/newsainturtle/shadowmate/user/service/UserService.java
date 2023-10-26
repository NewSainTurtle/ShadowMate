package com.newsainturtle.shadowmate.user.service;

import com.newsainturtle.shadowmate.user.dto.ProfileResponse;
import com.newsainturtle.shadowmate.user.dto.UserResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ProfileResponse getProfile(final Long userId);

    UserResponse searchNickname(final User user, final String nickname);

    void updateProfileImage(final Long userId, final String newProfileImage);

    void deleteUser(final Long userId);
}
