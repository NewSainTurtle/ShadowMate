package com.newsainturtle.shadowmate.follow.service;

import com.newsainturtle.shadowmate.follow.constant.FollowConstant;
import com.newsainturtle.shadowmate.follow.dto.*;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.enums.FollowStatus;
import com.newsainturtle.shadowmate.follow.exception.FollowErrorResult;
import com.newsainturtle.shadowmate.follow.exception.FollowException;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
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
        List<Follow> followingList = followRepository.findAllByFollowerId(user);
        return followingList.stream()
                .map(follow -> FollowingResponse.builder()
                        .followId(follow.getId())
                        .email(follow.getFollowingId().getEmail())
                        .nickname(follow.getFollowingId().getNickname())
                        .profileImage(follow.getFollowingId().getProfileImage())
                        .statusMessage(follow.getFollowingId().getStatusMessage())
                        .followingId(follow.getFollowingId().getId())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<FollowerResponse> getFollower(final User user) {
        List<Follow> followerList = followRepository.findAllByFollowingId(user);
        return followerList.stream()
                .map(follow -> FollowerResponse.builder()
                        .followId(follow.getId())
                        .email(follow.getFollowerId().getEmail())
                        .nickname(follow.getFollowerId().getNickname())
                        .profileImage(follow.getFollowerId().getProfileImage())
                        .followerId(follow.getFollowerId().getId())
                        .statusMessage(follow.getFollowerId().getStatusMessage())
                        .isFollow(isFollow(user, follow.getFollowerId()))
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<FollowRequestResponse> getFollowRequestList(final User user) {
        List<FollowRequest> followRequestList = followRequestRepository.findAllByReceiverId(user);
        return followRequestList.stream()
                .map(followRequest -> FollowRequestResponse.builder()
                        .followRequestId(followRequest.getId())
                        .requesterId(followRequest.getRequesterId().getId())
                        .email(followRequest.getRequesterId().getEmail())
                        .nickname(followRequest.getRequesterId().getNickname())
                        .profileImage(followRequest.getRequesterId().getProfileImage())
                        .statusMessage(followRequest.getRequesterId().getStatusMessage())
                        .plannerAccessScope(followRequest.getRequesterId().getPlannerAccessScope())
                        .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddFollowResponse addFollow(final User user, final Long targetUserId) {
        User targetUser = certifyFollowUser(targetUserId);
        if(targetUser.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC)) {
            return addFollowPublic(user, targetUser);
        }
        else {
            return addFollowNonPublic(user, targetUser);
        }
    }

    @Override
    @Transactional
    public void deleteFollowing(final User user, final Long targetUserId) {
        User targetUser = certifyFollowUser(targetUserId);
        followRepository.deleteByFollowingIdAndFollowerId(targetUser, user);
    }

    @Override
    @Transactional
    public void deleteFollower(final User user, final Long targetUserId) {
        User targetUser = certifyFollowUser(targetUserId);
        followRepository.deleteByFollowingIdAndFollowerId(user, targetUser);
    }

    @Override
    @Transactional
    public void deleteFollowRequest(final User user, final Long targetUserId) {
        User targetUser = certifyFollowUser(targetUserId);
        followRequestRepository.deleteByRequesterIdAndReceiverId(user, targetUser);
    }

    @Override
    @Transactional
    public String receiveFollow(final User user, final Long targetUserId, final boolean followReceive) {
        User targetUser = certifyFollowUser(targetUserId);
        FollowRequest followRequest = followRequestRepository.findByRequesterIdAndReceiverId(targetUser, user);
        if(followRequest == null) {
            throw new FollowException(FollowErrorResult.NOTFOUND_FOLLOW_REQUEST);
        }
        followRequestRepository.deleteByRequesterIdAndReceiverId(targetUser, user);
        if(followReceive) {
            followRepository.save(Follow.builder()
                    .followerId(targetUser)
                    .followingId(user)
                    .build());
            return FollowConstant.SUCCESS_FOLLOW_RECEIVE_TRUE;
        }
        return FollowConstant.SUCCESS_FOLLOW_RECEIVE_FALSE;
    }

    @Override
    public FollowStatus isFollow(final User user, final User searchUser) {
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

    @Override
    public CountFollowResponse countFollow(User user) {
        return CountFollowResponse.builder()
                .followerCount(followRepository.countByFollowerId(user))
                .followingCount(followRepository.countByFollowingId(user))
                .build();
    }

    private AddFollowResponse addFollowPublic(final User follower, final User following) {
        if(followRepository.findByFollowerIdAndFollowingId(follower, following) != null) {
            throw new FollowException(FollowErrorResult.DUPLICATED_FOLLOW);
        }
        Follow result = followRepository.save(Follow.builder()
                .followerId(follower)
                .followingId(following)
                .build());
        return AddFollowResponse.builder()
                .followId(result.getId())
                .plannerAccessScope(following.getPlannerAccessScope())
                .build();
    }

    private AddFollowResponse addFollowNonPublic(final User requester, final User receiver) {
        if (followRequestRepository.findByRequesterIdAndReceiverId(requester, receiver) != null) {
            throw new FollowException(FollowErrorResult.DUPLICATED_FOLLOW);
        }
        FollowRequest result = followRequestRepository.save(FollowRequest.builder()
                .requesterId(requester)
                .receiverId(receiver)
                .build());
        return AddFollowResponse.builder()
                .followId(result.getId())
                .plannerAccessScope(receiver.getPlannerAccessScope())
                .build();
    }

    private User certifyFollowUser(final Long userId) {
        Optional<User> result = userRepository.findById(userId);
        if(!result.isPresent() || result.get().getWithdrawal()) {
            throw new FollowException(FollowErrorResult.NOTFOUND_FOLLOW_USER);
        }
        return result.get();
    }
}
