package com.newsainturtle.shadowmate.planner_setting.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlannerSettingException extends RuntimeException {
    private final PlannerSettingErrorResult errorResult;
}
