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
    INVALID_TODO(HttpStatus.BAD_REQUEST, "플래너의 할일이 유효하지 않습니다."),
    INVALID_TODO_STATUS(HttpStatus.BAD_REQUEST, "플래너의 할일 상태가 유효하지 않습니다."),
    ALREADY_ADDED_LIKE(HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다."),
    UNABLE_TO_LIKE_YOUR_OWN_PLANNER(HttpStatus.BAD_REQUEST, "본인 플래너에 좋아요를 누를 수 없습니다."),
    INVALID_TIME(HttpStatus.BAD_REQUEST, "타임테이블 시간값이 유효하지 않습니다."),
    ALREADY_ADDED_TIME_TABLE(HttpStatus.BAD_REQUEST, "해당 할일에 대한 타임테이블 데이터가 이미 존재합니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "일주일 시작 날짜, 끝 날짜가 올바르지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
