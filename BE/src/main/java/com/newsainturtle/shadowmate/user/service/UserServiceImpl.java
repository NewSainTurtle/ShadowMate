package com.newsainturtle.shadowmate.user.service;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.user.dto.ProfileResponse;
import com.newsainturtle.shadowmate.user.dto.UserResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final FollowRepository followRepository;

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
        User searchUser = userRepository.findByNickname(nickname);
        if(searchUser==null) {
            throw new UserException(UserErrorResult.NOT_FOUND_NICKNAME);
        }
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
    public void updateProfileImage(final Long userId, final String newProfileImage) {
        Optional<User> oldUser = userRepository.findById(userId);
        if(!oldUser.isPresent()) {
            throw new UserException(UserErrorResult.NOT_FOUND_USER);
        }
        User newUser = User.builder()
                .id(oldUser.get().getId())
                .email(oldUser.get().getEmail())
                .password(oldUser.get().getPassword())
                .socialLogin(oldUser.get().getSocialLogin())
                .profileImage(newProfileImage)
                .nickname(oldUser.get().getNickname())
                .statusMessage(oldUser.get().getStatusMessage())
                .withdrawal(oldUser.get().getWithdrawal())
                .plannerAccessScope(oldUser.get().getPlannerAccessScope())
                .createTime(oldUser.get().getCreateTime())
                .updateTime(oldUser.get().getUpdateTime())
                .deleteTime(oldUser.get().getDeleteTime())
                .build();
        userRepository.save(newUser);
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
}
