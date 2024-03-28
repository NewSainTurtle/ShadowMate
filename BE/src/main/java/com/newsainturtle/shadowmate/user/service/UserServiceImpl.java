package com.newsainturtle.shadowmate.user.service;

import com.newsainturtle.shadowmate.auth.service.RedisService;
import com.newsainturtle.shadowmate.user.dto.request.UpdateIntroductionRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdatePasswordRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdateUserRequest;
import com.newsainturtle.shadowmate.user.dto.response.ProfileResponse;
import com.newsainturtle.shadowmate.user.dto.response.SearchIntroductionResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisService redisService;

    @Override
    public ProfileResponse getProfile(final Long userId) {
        final User user = userRepository.findByIdAndWithdrawalIsFalse(userId);
        if (user == null) throw new UserException(UserErrorResult.NOT_FOUND_PROFILE);
        return ProfileResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .statusMessage(user.getStatusMessage())
                .plannerAccessScope(user.getPlannerAccessScope().getScope())
                .build();
    }

    @Override
    public SearchIntroductionResponse searchIntroduction(final Long userId) {
        getUserById(userId);
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
        userRepository.deleteUser(LocalDateTime.now(), user.getId(), PlannerAccessScope.PRIVATE, createNicknameRandomCode());
    }

    @Override
    public User getUserByNickname(final String nickname) {
        return userRepository.findByNicknameAndWithdrawalIsFalse(nickname);
    }

    @Override
    public User getUserById(final long userId) {
        final User user = userRepository.findByIdAndWithdrawalIsFalse(userId);
        if (user == null) {
            throw new UserException(UserErrorResult.NOT_FOUND_USER);
        }
        return user;
    }

    @Override
    public User getUserByNicknameAndScopePublic(final String nickname) {
        return userRepository.findByNicknameAndPlannerAccessScopeAndWithdrawalIsFalse(nickname, PlannerAccessScope.PUBLIC);
    }

    @Override
    @Transactional
    public void updatePlannerAccessScope(final Long userId, final PlannerAccessScope plannerAccessScope) {
        userRepository.updatePlannerAccessScope(plannerAccessScope, userId);
    }

    private String createNicknameRandomCode() {
        final String temp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final StringBuilder sb = new StringBuilder();
        final SecureRandom random = new SecureRandom();
        do {
            int length = random.nextInt(9) + 11;
            for (int i = 0; i < length; i++) {
                sb.append(temp.charAt(random.nextInt(temp.length())));
            }
        } while (userRepository.existsByNickname(sb.toString()));

        return sb.toString();
    }
}
