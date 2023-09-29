package com.newsainturtle.shadowmate.planner.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.planner.dto.*;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.newsainturtle.shadowmate.planner.constant.PlannerConstant.*;

@RestController
@RequestMapping("/api/planners")
@RequiredArgsConstructor
public class PlannerController {

    private final DailyPlannerService dailyPlannerServiceImpl;
    private final AuthService authServiceImpl;

    @PostMapping("/{userId}/daily/todos")
    public ResponseEntity<BaseResponse> addDailyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                     @PathVariable("userId") final Long userId,
                                                     @RequestBody @Valid final AddDailyTodoRequest addDailyTodoRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_DAILY_TODO, dailyPlannerServiceImpl.addDailyTodo(principalDetails.getUser(), addDailyTodoRequest)));
    }

    @DeleteMapping("/{userId}/daily/todos")
    public ResponseEntity<BaseResponse> removeDailyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final RemoveDailyTodoRequest removeDailyTodoRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        dailyPlannerServiceImpl.removeDailyTodo(principalDetails.getUser(), removeDailyTodoRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_DAILY_TODO));
    }

    @PutMapping("/{userId}/daily/today-goals")
    public ResponseEntity<BaseResponse> updateTodayGoal(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final UpdateTodayGoalRequest updateTodayGoalRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        dailyPlannerServiceImpl.updateTodayGoal(principalDetails.getUser(), updateTodayGoalRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_TODAY_GOAL));
    }

    @PutMapping("/{userId}/daily/tomorrow-goals")
    public ResponseEntity<BaseResponse> updateTomorrowGoal(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                           @PathVariable("userId") final Long userId,
                                                           @RequestBody @Valid final UpdateTomorrowGoalRequest updateTomorrowGoalRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        dailyPlannerServiceImpl.updateTomorrowGoal(principalDetails.getUser(), updateTomorrowGoalRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_TOMORROW_GOAL));
    }

    @PostMapping("/{userId}/daily/likes")
    public ResponseEntity<BaseResponse> addDailyLike(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                     @PathVariable("userId") final Long userId,
                                                     @RequestBody @Valid final AddDailyLikeRequest addDailyLikeRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        dailyPlannerServiceImpl.addDailyLike(principalDetails.getUser(), addDailyLikeRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_DAILY_LIKE));
    }

    @DeleteMapping("/{userId}/daily/likes")
    public ResponseEntity<BaseResponse> removeDailyLike(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final RemoveDailyLikeRequest removeDailyLikeRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        dailyPlannerServiceImpl.removeDailyLike(principalDetails.getUser(), removeDailyLikeRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_DAILY_LIKE));
    }

}
