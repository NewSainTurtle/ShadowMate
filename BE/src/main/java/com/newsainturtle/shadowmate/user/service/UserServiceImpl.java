package com.newsainturtle.shadowmate.user.service;

import com.newsainturtle.shadowmate.auth.service.RedisServiceImpl;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.follow.service.FollowServiceImpl;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.dto.request.UpdateIntroductionRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdatePasswordRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdateUserRequest;
import com.newsainturtle.shadowmate.user.dto.response.ProfileResponse;
import com.newsainturtle.shadowmate.user.dto.response.SearchIntroductionResponse;
import com.newsainturtle.shadowmate.user.dto.response.UserResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
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
    private final FollowRequestRepository followRequestRepository;
    private final SocialRepository socialRepository;
    private final DailyPlannerRepository dailyPlannerRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisServiceImpl redisService;
    private final FollowServiceImpl followService;

    @Override
    public ProfileResponse getProfile(final Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) throw new UserException(UserErrorResult.NOT_FOUND_PROFILE);
        User user = optionalUser.get();
        return ProfileResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .statusMessage(user.getStatusMessage())
                .plannerAccessScope(user.getPlannerAccessScope().getScope())
                .build();
    }

    @Override
    public UserResponse searchNickname(final User user, final String nickname) {
        final User searchUser = userRepository.findByNicknameAndWithdrawalIsFalse(nickname);
        if (searchUser == null) {
            return UserResponse.builder().build();
        }
        return UserResponse.builder()
                .userId(searchUser.getId())
                .email(searchUser.getEmail())
                .profileImage(searchUser.getProfileImage())
                .nickname(searchUser.getNickname())
                .statusMessage(searchUser.getStatusMessage())
                .plannerAccessScope(searchUser.getPlannerAccessScope())
                .isFollow(followService.isFollow(user, searchUser))
                .build();
    }

    @Override
    public SearchIntroductionResponse searchIntroduction(final Long userId) {
        findUser(userId);
        return SearchIntroductionResponse.builder()
                .introduction(userRepository.findIntroduction(userId))
                .build();
    }

    @Override
    @Transactional
    public void updateUser(final User user, final UpdateUserRequest updateUserRequest) {
        if (!user.getNickname().equals(updateUserRequest.getNewNickname())) {
            final Boolean getHashNickname = redisService.getNicknameData(updateUserRequest.getNewNickname());
            if (getHashNickname == null || !getHashNickname) {
                throw new UserException(UserErrorResult.RETRY_NICKNAME);
            } else {
                redisService.deleteNicknameData(updateUserRequest.getNewNickname());
            }
        }
        userRepository.updateUser(updateUserRequest.getNewNickname(),
                updateUserRequest.getNewProfileImage(),
                updateUserRequest.getNewStatusMessage(),
                user.getId());
    }

    @Override
    @Transactional
    public void updatePassword(final User user, final UpdatePasswordRequest updatePasswordRequest) {
        if (bCryptPasswordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            userRepository.updatePassword(bCryptPasswordEncoder.encode(updatePasswordRequest.getNewPassword()), user.getId());
        } else {
            throw new UserException(UserErrorResult.DIFFERENT_PASSWORD);
        }
    }

    @Override
    @Transactional
    public void updateIntroduction(final Long userId, final UpdateIntroductionRequest updateIntroductionRequest) {
        userRepository.updateIntroduction(updateIntroductionRequest.getIntroduction(), userId);
    }

    @Override
    @Transactional
    public void deleteUser(final User user) {
        followRepository.deleteAllByFollowingOrFollower(user, user);
        followRequestRepository.deleteAllByRequesterOrReceiver(user, user);
        socialRepository.updateDeleteTimeAll(LocalDateTime.now(), dailyPlannerRepository.findAllByUser(user));
        userRepository.deleteUser(LocalDateTime.now(), user.getId(), PlannerAccessScope.PRIVATE);
    }

    private void findUser(final Long userId) {
        if (userRepository.findByIdAndWithdrawalIsFalse(userId) == null) {
            throw new UserException(UserErrorResult.NOT_FOUND_USER);
        }
    }
}
