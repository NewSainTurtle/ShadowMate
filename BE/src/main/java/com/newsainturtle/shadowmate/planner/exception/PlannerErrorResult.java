package com.newsainturtle.shadowmate.planner.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlannerErrorResult implements BaseErrorResult {
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리가 유효하지 않습니다."),
    INVALID_DAILY_PLANNER(HttpStatus.BAD_REQUEST, "일일 플래너가 유효하지 않습니다."),
    INVALID_WEEKLY_PLANNER(HttpStatus.BAD_REQUEST, "주간 플래너가 유효하지 않습니다."),
    INVALID_TODO(HttpStatus.BAD_REQUEST, "플래너의 할일이 유효하지 않습니다."),
    INVALID_TODO_STATUS(HttpStatus.BAD_REQUEST, "플래너의 할일 상태가 유효하지 않습니다."),
    ALREADY_ADDED_LIKE(HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다."),
    UNABLE_TO_LIKE_YOUR_OWN_PLANNER(HttpStatus.BAD_REQUEST, "본인 플래너에 좋아요를 누를 수 없습니다."),
    INVALID_TIME(HttpStatus.BAD_REQUEST, "타임테이블 시간값이 유효하지 않습니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "일주일 시작 날짜, 끝 날짜가 올바르지 않습니다."),
    INVALID_USER(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자입니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 날짜형식입니다."),
    FAILED_ADDED_TIMETABLE(HttpStatus.BAD_REQUEST, "타임테이블 등록은 할일이 진행중이거나 완료한 상태에서만 가능합니다."),
    FAILED_SHARE_SOCIAL(HttpStatus.BAD_REQUEST, "소셜공유는 전채공개 상태에서만 가능합니다."),
    ALREADY_SHARED_SOCIAL(HttpStatus.BAD_REQUEST, "해당 플래너는 이미 소셜에 공유했습니다."),
    FAILED_SELF_VISITOR_BOOK_WRITING(HttpStatus.BAD_REQUEST, "자신의 방명록에 글을 작성할 수 없습니다."),
    INVALID_VISITOR_BOOK(HttpStatus.BAD_REQUEST, "방명록이 유효하지 않습니다."),
    NO_PERMISSION_TO_REMOVE_VISITOR_BOOK(HttpStatus.FORBIDDEN, "해당 방명록을 삭제할 수 있는 권한이 없습니다."),
    INVALID_TIMETABLE(HttpStatus.BAD_REQUEST, "타임테이블이 유효하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
