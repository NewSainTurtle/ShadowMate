package com.newsainturtle.shadowmate.planner.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlannerException extends RuntimeException {
    private final PlannerErrorResult errorResult;
}
