package com.newsainturtle.shadowmate.follow.service;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.follow.dto.FollowingResponse;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.exception.FollowErrorResult;
import com.newsainturtle.shadowmate.follow.exception.FollowException;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.user.entity.User;
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

    @Override
    public List<FollowingResponse> getFollowing(final User user) {
        List<Follow> followingList = followRepository.findAllByFollowerId(user);
        return followingList.stream()
                .map(follow -> FollowingResponse.builder()
                        .followId(follow.getId())
                        .email(follow.getFollowingId().getEmail())
                        .nickname(follow.getFollowingId().getNickname())
                        .profileImage(follow.getFollowingId().getProfileImage())
                        .followingId(follow.getFollowingId().getId())
                        .build()).collect(Collectors.toList());
    }
}
