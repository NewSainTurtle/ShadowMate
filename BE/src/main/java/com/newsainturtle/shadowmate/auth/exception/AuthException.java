package com.newsainturtle.shadowmate.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthException extends RuntimeException{
    private final AuthErrorResult errorResult;
}
