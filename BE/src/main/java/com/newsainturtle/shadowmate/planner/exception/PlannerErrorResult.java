package com.newsainturtle.shadowmate.planner.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlannerErrorResult implements BaseErrorResult {
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리 ID가 유효하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
