package com.newsainturtle.shadowmate.follow.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FollowException extends RuntimeException{
    private final FollowErrorResult errorResult;
}
