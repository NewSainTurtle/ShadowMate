package com.newsainturtle.shadowmate.follow.service;

import com.newsainturtle.shadowmate.follow.dto.AddFollowResponse;
import com.newsainturtle.shadowmate.follow.dto.FollowingResponse;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
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
                        .followingId(follow.getFollowingId())
                        .followerId(follow.getFollowerId())
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

    private AddFollowResponse addFollowPublic(final User follower, final User following) {
        duplicatedCheckFollow(followRepository.findByFollowerIdAndFollowingId(follower, following));
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
        duplicatedCheckFollow(followRequestRepository.findByFollowerIdAndFollowingId(requester, receiver));
        FollowRequest result = followRequestRepository.save(FollowRequest.builder()
                .requesterId(requester)
                .receiverId(receiver)
                .build());
        return AddFollowResponse.builder()
                .followId(result.getId())
                .plannerAccessScope(receiver.getPlannerAccessScope())
                .build();
    }

    private User certifyFollowUser(final Long followingId) {
        Optional<User> result = userRepository.findById(followingId);
        if(!result.isPresent() || result.get().getWithdrawal()) {
            throw new FollowException(FollowErrorResult.NOTFOUND_FOLLOWING_USER);
        }
        return result.get();
    }

    private void duplicatedCheckFollow(final User user) {
        if(user!=null) {
            throw new FollowException(FollowErrorResult.DUPLICATED_FOLLOW);
        }
    }
}
