package com.newsainturtle.shadowmate.follow.service;

import com.newsainturtle.shadowmate.follow.constant.FollowConstant;
import com.newsainturtle.shadowmate.follow.dto.response.*;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.follow.exception.FollowErrorResult;
import com.newsainturtle.shadowmate.follow.exception.FollowException;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;
    private final UserRepository userRepository;

    @Override
    public List<FollowingResponse> getFollowing(final User user) {
        List<Follow> followingList = followRepository.findAllByFollower(user);
        return followingList.stream()
                .map(follow -> FollowingResponse.builder()
                        .followId(follow.getId())
                        .email(follow.getFollowing().getEmail())
                        .nickname(follow.getFollowing().getNickname())
                        .profileImage(follow.getFollowing().getProfileImage())
                        .statusMessage(follow.getFollowing().getStatusMessage())
                        .followingId(follow.getFollowing().getId())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<FollowerResponse> getFollower(final User user) {
        List<Follow> followerList = followRepository.findAllByFollowing(user);
        return followerList.stream()
                .map(follow -> FollowerResponse.builder()
                        .followId(follow.getId())
                        .email(follow.getFollower().getEmail())
                        .nickname(follow.getFollower().getNickname())
                        .profileImage(follow.getFollower().getProfileImage())
                        .followerId(follow.getFollower().getId())
                        .statusMessage(follow.getFollower().getStatusMessage())
                        .isFollow(isFollow(user, follow.getFollower()))
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<FollowRequestResponse> getFollowRequestList(final User user) {
        List<FollowRequest> followRequestList = followRequestRepository.findAllByReceiver(user);
        return followRequestList.stream()
                .map(followRequest -> FollowRequestResponse.builder()
                        .followRequestId(followRequest.getId())
                        .requesterId(followRequest.getRequester().getId())
                        .email(followRequest.getRequester().getEmail())
                        .nickname(followRequest.getRequester().getNickname())
                        .profileImage(followRequest.getRequester().getProfileImage())
                        .statusMessage(followRequest.getRequester().getStatusMessage())
                        .plannerAccessScope(followRequest.getRequester().getPlannerAccessScope())
                        .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddFollowResponse addFollow(final User user, final Long targetUserId) {
        User targetUser = certifyFollowUser(targetUserId);
        if (targetUser.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC)) {
            return addFollowPublic(user, targetUser);
        } else {
            return addFollowNonPublic(user, targetUser);
        }
    }

    @Override
    @Transactional
    public void deleteFollowing(final User user, final Long targetUserId) {
        User targetUser = certifyFollowUser(targetUserId);
        followRepository.deleteByFollowingAndFollower(targetUser, user);
    }

    @Override
    @Transactional
    public void deleteFollower(final User user, final Long targetUserId) {
        User targetUser = certifyFollowUser(targetUserId);
        followRepository.deleteByFollowingAndFollower(user, targetUser);
    }

    @Override
    @Transactional
    public void deleteFollowRequest(final User user, final Long targetUserId) {
        User targetUser = certifyFollowUser(targetUserId);
        followRequestRepository.deleteByRequesterAndReceiver(user, targetUser);
    }

    @Override
    @Transactional
    public String receiveFollow(final User user, final Long targetUserId, final boolean followReceive) {
        User targetUser = certifyFollowUser(targetUserId);
        FollowRequest followRequest = followRequestRepository.findByRequesterAndReceiver(targetUser, user);
        if (followRequest == null) {
            throw new FollowException(FollowErrorResult.NOTFOUND_FOLLOW_REQUEST);
        }
        followRequestRepository.deleteByRequesterAndReceiver(targetUser, user);
        if (followReceive) {
            followRepository.save(Follow.builder()
                    .follower(targetUser)
                    .following(user)
                    .build());
            return FollowConstant.SUCCESS_FOLLOW_RECEIVE_TRUE;
        }
        return FollowConstant.SUCCESS_FOLLOW_RECEIVE_FALSE;
    }

    @Override
    public FollowStatus isFollow(final User user, final User searchUser) {
        Follow follow = followRepository.findByFollowingAndFollower(searchUser, user);
        if (follow == null) {
            FollowRequest followRequest = followRequestRepository.findByRequesterAndReceiver(user, searchUser);
            if (followRequest == null) {
                return FollowStatus.EMPTY;
            }
            return FollowStatus.REQUESTED;
        }
        return FollowStatus.FOLLOW;
    }

    @Override
    public CountFollowResponse countFollow(final Long userId) {
        Optional<User> result = userRepository.findById(userId);
        if (!result.isPresent()) {
            throw new UserException(UserErrorResult.NOT_FOUND_USER);
        }
        final User user = result.get();
        return CountFollowResponse.builder()
                .followerCount(followRepository.countByFollowing(user))
                .followingCount(followRepository.countByFollower(user))
                .build();
    }

    private AddFollowResponse addFollowPublic(final User follower, final User following) {
        if (followRepository.findByFollowingAndFollower(following, follower) != null) {
            throw new FollowException(FollowErrorResult.DUPLICATED_FOLLOW);
        }
        Follow result = followRepository.save(Follow.builder()
                .follower(follower)
                .following(following)
                .build());
        return AddFollowResponse.builder()
                .followId(result.getId())
                .plannerAccessScope(following.getPlannerAccessScope())
                .build();
    }

    private AddFollowResponse addFollowNonPublic(final User requester, final User receiver) {
        if (followRequestRepository.findByRequesterAndReceiver(requester, receiver) != null) {
            throw new FollowException(FollowErrorResult.DUPLICATED_FOLLOW);
        }
        FollowRequest result = followRequestRepository.save(FollowRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .build());
        return AddFollowResponse.builder()
                .followId(result.getId())
                .plannerAccessScope(receiver.getPlannerAccessScope())
                .build();
    }

    private User certifyFollowUser(final Long userId) {
        Optional<User> result = userRepository.findById(userId);
        if (!result.isPresent() || result.get().getWithdrawal().booleanValue()) {
            throw new FollowException(FollowErrorResult.NOTFOUND_FOLLOW_USER);
        }
        return result.get();
    }
}
