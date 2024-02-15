package com.newsainturtle.shadowmate.follow.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowErrorResult implements BaseErrorResult {

    DUPLICATED_FOLLOW(HttpStatus.BAD_REQUEST, "이미 신청된 요청입니다."),
    NOT_FOUND_FOLLOW_REQUEST(HttpStatus.NOT_FOUND, "팔로우 신청이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
