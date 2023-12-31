package com.newsainturtle.shadowmate.common;

import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.follow.exception.FollowException;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.user.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {

        final List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("Invalid DTO Parameter errors : {}", errorList);
        return this.makeErrorResponseEntity(errorList.toString());
    }

    private ResponseEntity<Object> makeErrorResponseEntity(final String errorDescription) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorDescription));
    }

    @ExceptionHandler({AuthException.class})
    public ResponseEntity<ErrorResponse> handleAuthException(final AuthException exception) {
        log.warn("AuthException occur: ", exception);
        return this.makeErrorResponseEntity(exception.getErrorResult());
    }

    @ExceptionHandler({PlannerSettingException.class})
    public ResponseEntity<ErrorResponse> handlePlannerSettingException(final PlannerSettingException exception) {
        log.warn("PlannerSettingException occur: ", exception);
        return this.makeErrorResponseEntity(exception.getErrorResult());
    }

    @ExceptionHandler({UserException.class})
    public ResponseEntity<ErrorResponse> handleUserException(final UserException exception) {
        log.warn("UserException occur: ", exception);
        return this.makeErrorResponseEntity(exception.getErrorResult());
    }

    @ExceptionHandler({PlannerException.class})
    public ResponseEntity<ErrorResponse> handlePlannerException(final PlannerException exception) {
        log.warn("PlannerException occur: ", exception);
        return this.makeErrorResponseEntity(exception.getErrorResult());
    }

    @ExceptionHandler({FollowException.class})
    public ResponseEntity<ErrorResponse> handleFollowException(final FollowException exception) {
        log.warn("FollowException occur: ", exception);
        return this.makeErrorResponseEntity(exception.getErrorResult());
    }

    @ExceptionHandler({SocialException.class})
    public ResponseEntity<ErrorResponse> handleSocialException(final SocialException exception) {
        log.warn("SocialException occur: ", exception);
        return this.makeErrorResponseEntity(exception.getErrorResult());
    }


    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final BaseErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }

}

