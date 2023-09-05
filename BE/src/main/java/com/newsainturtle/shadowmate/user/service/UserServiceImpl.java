package com.newsainturtle.shadowmate.user.service;

import com.newsainturtle.shadowmate.user.dto.ProfileResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ProfileResponse getProfile(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()) throw new UserException(UserErrorResult.NOT_FOUND_PROFILE);
        User user = optionalUser.get();
        return ProfileResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .statusMessage(user.getStatusMessage())
                .plannerAccessScope(user.getPlannerAccessScope())
                .build();
    }
}
