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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;

    @Override
    public List<FollowingResponse> getFollowing(final User user) {
        final List<Follow> followingList = followRepository.findAllByFollower(user);
        return followingList.stream()
                .map(follow -> FollowingResponse.builder()
                        .followId(follow.getId())
                        .email(follow.getFollowing().getEmail())
                        .nickname(follow.getFollowing().getNickname())
                        .profileImage(follow.getFollowing().getProfileImage())
                        .statusMessage(follow.getFollowing().getStatusMessage())
                        .followingId(follow.getFollowing().getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<FollowerResponse> getFollower(final User user) {
        final List<Follow> followerList = followRepository.findAllByFollowing(user);
        return followerList.stream()
                .map(follow -> FollowerResponse.builder()
                        .followId(follow.getId())
                        .email(follow.getFollower().getEmail())
                        .nickname(follow.getFollower().getNickname())
                        .profileImage(follow.getFollower().getProfileImage())
                        .followerId(follow.getFollower().getId())
                        .statusMessage(follow.getFollower().getStatusMessage())
                        .isFollow(isFollow(user, follow.getFollower()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<FollowRequestResponse> getFollowRequestList(final User user) {
        final List<FollowRequest> followRequestList = followRequestRepository.findAllByReceiver(user);
        return followRequestList.stream()
                .map(followRequest -> FollowRequestResponse.builder()
                        .followRequestId(followRequest.getId())
                        .requesterId(followRequest.getRequester().getId())
                        .email(followRequest.getRequester().getEmail())
                        .nickname(followRequest.getRequester().getNickname())
                        .profileImage(followRequest.getRequester().getProfileImage())
                        .statusMessage(followRequest.getRequester().getStatusMessage())
                        .plannerAccessScope(followRequest.getRequester().getPlannerAccessScope())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddFollowResponse addFollow(final User user, final User targetUser) {
        if (targetUser.getPlannerAccessScope().equals(PlannerAccessScope.PUBLIC)) {
            return addFollowPublic(user, targetUser);
        } else {
            return addFollowNonPublic(user, targetUser);
        }
    }

    @Override
    @Transactional
    public void deleteFollowing(final User user, final User targetUser) {
        followRepository.deleteByFollowingAndFollower(targetUser, user);
    }

    @Override
    @Transactional
    public void deleteFollower(final User user, final User targetUser) {
        followRepository.deleteByFollowingAndFollower(user, targetUser);
    }

    @Override
    @Transactional
    public void deleteFollowRequest(final User user, final User targetUser) {
        followRequestRepository.deleteByRequesterAndReceiver(user, targetUser);
    }

    @Override
    @Transactional
    public String receiveFollow(final User user, final User targetUser, final boolean followReceive) {
        final FollowRequest followRequest = followRequestRepository.findByRequesterAndReceiver(targetUser, user);
        if (followRequest == null) {
            throw new FollowException(FollowErrorResult.NOT_FOUND_FOLLOW_REQUEST);
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
        final Follow follow = followRepository.findByFollowingAndFollower(searchUser, user);
        if (follow == null) {
            final FollowRequest followRequest = followRequestRepository.findByRequesterAndReceiver(user, searchUser);
            if (followRequest == null) {
                return FollowStatus.EMPTY;
            }
            return FollowStatus.REQUESTED;
        }
        return FollowStatus.FOLLOW;
    }

    @Override
    public CountFollowResponse countFollow(final User targetUser) {
        return CountFollowResponse.builder()
                .followerCount(followRepository.countByFollowing(targetUser))
                .followingCount(followRepository.countByFollower(targetUser))
                .build();
    }

    private AddFollowResponse addFollowPublic(final User follower, final User following) {
        if (followRepository.findByFollowingAndFollower(following, follower) != null) {
            throw new FollowException(FollowErrorResult.DUPLICATED_FOLLOW);
        }
        final long followId = followRepository.save(Follow.builder()
                .follower(follower)
                .following(following)
                .build()).getId();
        return AddFollowResponse.builder()
                .followId(followId)
                .plannerAccessScope(following.getPlannerAccessScope())
                .build();
    }

    private AddFollowResponse addFollowNonPublic(final User requester, final User receiver) {
        if (followRequestRepository.findByRequesterAndReceiver(requester, receiver) != null) {
            throw new FollowException(FollowErrorResult.DUPLICATED_FOLLOW);
        }
        final long followRequestId = followRequestRepository.save(FollowRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .build()).getId();

        return AddFollowResponse.builder()
                .followId(followRequestId)
                .plannerAccessScope(receiver.getPlannerAccessScope())
                .build();
    }

    @Override
    public SearchUserResponse searchNickname(final User user, final User searchUser) {
        if (searchUser == null) {
            return SearchUserResponse.builder().build();
        }
        return SearchUserResponse.builder()
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
    public void acceptAllFollowRequest(final User user) {
        List<FollowRequest> followRequestList = followRequestRepository.findAllByReceiver(user);
        for (FollowRequest followRequest : followRequestList) {
            followRepository.save(Follow.builder()
                    .follower(followRequest.getRequester())
                    .following(user)
                    .build());
        }
        followRequestRepository.deleteAllByReceiver(user.getId());
    }

    @Override
    @Transactional
    public void deleteUser(final User user) {
        followRepository.deleteAllByFollowingOrFollower(user, user);
        followRequestRepository.deleteAllByRequesterOrReceiver(user, user);
    }
}
