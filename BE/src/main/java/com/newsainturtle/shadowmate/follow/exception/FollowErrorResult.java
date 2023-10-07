package com.newsainturtle.shadowmate.follow.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowErrorResult implements BaseErrorResult {

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
