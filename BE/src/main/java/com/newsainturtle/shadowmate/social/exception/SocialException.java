package com.newsainturtle.shadowmate.social.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SocialException extends RuntimeException{
    private final SocialErrorResult errorResult;
}
