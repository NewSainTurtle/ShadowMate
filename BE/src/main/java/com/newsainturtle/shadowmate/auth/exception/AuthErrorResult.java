package com.newsainturtle.shadowmate.auth.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorResult implements BaseErrorResult {
    UNREGISTERED_USER(HttpStatus.FORBIDDEN, "등록되지 않은 사용자입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    FAIL_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),
    FAIL_VALIDATE_TOKEN(HttpStatus.FORBIDDEN, "토큰 인증에 실패했습니다."),
    FAIL_VALIDATE_TOKEN_SOCIAL_TYPE(HttpStatus.FORBIDDEN, "토큰 소셜타입 인증에 실패했습니다."),
    EMAIL_AUTHENTICATION_TIME_OUT(HttpStatus.BAD_REQUEST, "이메일 인증 유효시간이 지났습니다."),
    ALREADY_AUTHENTICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 인증된 이메일입니다."),
    UNAUTHENTICATED_EMAIL(HttpStatus.BAD_REQUEST, "인증되지 않은 이메일입니다."),
    INVALID_EMAIL_AUTHENTICATION_CODE(HttpStatus.BAD_REQUEST, "이메일 인증 코드가 틀렸습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
