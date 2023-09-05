package com.newsainturtle.shadowmate.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException{
    private final UserErrorResult errorResult;
}
