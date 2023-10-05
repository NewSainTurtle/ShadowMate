package com.newsainturtle.shadowmate.planner.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlannerErrorResult implements BaseErrorResult {
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리가 유효하지 않습니다."),
    INVALID_DAILY_PLANNER(HttpStatus.BAD_REQUEST, "플래너가 유효하지 않습니다."),
    ALREADY_ADDED_LIKE(HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다."),
    UNABLE_TO_LIKE_YOUR_OWN_PLANNER(HttpStatus.BAD_REQUEST, "본인 플래너에 좋아요를 누를 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
