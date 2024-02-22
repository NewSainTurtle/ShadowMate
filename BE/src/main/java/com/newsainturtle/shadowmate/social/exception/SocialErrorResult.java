package com.newsainturtle.shadowmate.social.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SocialErrorResult implements BaseErrorResult {
    BAD_REQUEST_SORT(HttpStatus.BAD_REQUEST, "정렬정보가 일치하지 않습니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 날짜형식입니다."),
    INVALID_DATE_PERIOD(HttpStatus.BAD_REQUEST, "유효하지 않은 날짜기간입니다."),
    ALREADY_SHARED_SOCIAL(HttpStatus.BAD_REQUEST, "해당 플래너는 이미 소셜에 공유했습니다."),
    FAILED_SHARE_SOCIAL(HttpStatus.BAD_REQUEST, "소셜공유는 전채공개 상태에서만 가능합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
