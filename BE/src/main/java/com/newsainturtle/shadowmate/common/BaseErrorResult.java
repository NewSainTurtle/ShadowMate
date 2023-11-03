package com.newsainturtle.shadowmate.common;

import org.springframework.http.HttpStatus;

public interface BaseErrorResult {

    HttpStatus getHttpStatus();
    String getMessage();
    String name();
}
