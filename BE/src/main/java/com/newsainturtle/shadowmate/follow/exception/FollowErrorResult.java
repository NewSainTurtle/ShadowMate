package com.newsainturtle.shadowmate.follow.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowErrorResult implements BaseErrorResult {

    NOTFOUND_FOLLOWING_USER(HttpStatus.NOT_FOUND, "팔로우신청할 유저가 없습니다."),
    DUPLICATED_FOLLOW(HttpStatus.BAD_REQUEST, "이미 신청된 요청입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
