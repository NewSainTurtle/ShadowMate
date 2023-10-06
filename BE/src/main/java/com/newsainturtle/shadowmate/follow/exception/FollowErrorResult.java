package com.newsainturtle.shadowmate.follow.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowErrorResult implements BaseErrorResult {

    NOTFOUND_FOLLOWING(HttpStatus.NOT_FOUND, "등록된 팔로잉이 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
