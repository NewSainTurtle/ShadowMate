package com.newsainturtle.shadowmate.social.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SocialErrorResult implements BaseErrorResult {
    NOT_FOUND_PAGE_NUMBER(HttpStatus.NOT_FOUND, "페이지넘버를 찾을수 없습니다."),

    BAD_REQUEST_SORT(HttpStatus.BAD_REQUEST, "정렬정보가 일치하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
