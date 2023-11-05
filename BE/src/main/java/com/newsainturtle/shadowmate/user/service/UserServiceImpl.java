package com.newsainturtle.shadowmate.user.service;

import com.newsainturtle.shadowmate.auth.service.RedisServiceImpl;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.user.dto.ProfileResponse;
import com.newsainturtle.shadowmate.user.dto.UpdateUserRequest;
import com.newsainturtle.shadowmate.user.dto.UserResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final FollowRepository followRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final RedisServiceImpl redisService;

    private final FollowRequestRepository followRequestRepository;

    @Override
    public ProfileResponse getProfile(final Long userId) {
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

    @Override
    public UserResponse searchNickname(final User user, final String nickname) {
        final User searchUser = searchUserNickname(nickname);
        return UserResponse.builder()
                .userId(searchUser.getId())
                .email(searchUser.getEmail())
                .profileImage(searchUser.getProfileImage())
                .nickname(searchUser.getNickname())
                .statusMessage(searchUser.getStatusMessage())
                .plannerAccessScope(searchUser.getPlannerAccessScope())
                .isFollow(isFollow(user, searchUser))
                .build();
    }

    @Override
    @Transactional
    public void updateUser(final Long userId, final UpdateUserRequest updateUserRequest) {
        userRepository.updateUser(updateUserRequest.getNewNickname(),
                updateUserRequest.getNewProfileImage(),
                updateUserRequest.getNewStatusMessage(),
                userId);
        redisService.deleteNicknameData(updateUserRequest.getNewNickname());
    }

    @Override
    @Transactional
    public void updatePassword(final Long userId, final String oldPassword, final String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new UserException(UserErrorResult.NOT_FOUND_USER);
        }
        if(bCryptPasswordEncoder.matches(oldPassword, newPassword)) {
            userRepository.updatePassword(bCryptPasswordEncoder.encode(newPassword), userId);
        }
        else {
            throw new UserException(UserErrorResult.DIFFERENT_PASSWORD);
        }
    }

    @Override
    @Transactional
    public void deleteUser(final Long userId) {
        User user = searchUserId(userId);
        User deleteUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .socialLogin(user.getSocialLogin())
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .statusMessage(user.getStatusMessage())
                .withdrawal(true)
                .plannerAccessScope(user.getPlannerAccessScope())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .deleteTime(LocalDateTime.now())
                .build();
        userRepository.save(deleteUser);
    }

    private FollowStatus isFollow(final User user, final User searchUser) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(user, searchUser);
        if (follow == null) {
            FollowRequest followRequest = followRequestRepository.findByRequesterIdAndReceiverId(user, searchUser);
            if(followRequest == null) {
                return FollowStatus.EMPTY;
            }
            return FollowStatus.REQUESTED;
        }
        return FollowStatus.FOLLOW;
    }

    private User searchUserNickname(String nickname) {
        User searchUser = userRepository.findByNickname(nickname);
        if(searchUser==null) {
            throw new UserException(UserErrorResult.NOT_FOUND_NICKNAME);
        }
        return searchUser;
    }

    private User searchUserId(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()) {
            throw new UserException(UserErrorResult.NOT_FOUND_USER);
        }
        return user.get();
    }
}
