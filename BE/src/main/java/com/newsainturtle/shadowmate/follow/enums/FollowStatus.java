package com.newsainturtle.shadowmate.follow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FollowStatus {

    EMPTY("팔로우아닌상태"),
    FOLLOW("팔로우상태"),
    REQUESTED("친구요청상태")
    ;

    private final String status;
}
