package com.newsainturtle.shadowmate.planner.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.planner.dto.AddDailyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.UpdateRetrospectionRequest;
import com.newsainturtle.shadowmate.planner.dto.UpdateTodayGoalRequest;
import com.newsainturtle.shadowmate.planner.dto.UpdateTomorrowGoalRequest;
import com.newsainturtle.shadowmate.planner.dto.*;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerService;
import com.newsainturtle.shadowmate.planner.service.WeeklyPlannerService;
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
    private final WeeklyPlannerService weeklyPlannerServiceImpl;
    private final AuthService authServiceImpl;

    @PostMapping("/{userId}/daily/todos")
    public ResponseEntity<BaseResponse> addDailyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                     @PathVariable("userId") final Long userId,
                                                     @RequestBody @Valid final AddDailyTodoRequest addDailyTodoRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_DAILY_TODO, dailyPlannerServiceImpl.addDailyTodo(principalDetails.getUser(), addDailyTodoRequest)));
    }

    @PutMapping("/{userId}/daily/todos")
    public ResponseEntity<BaseResponse> updateDailyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final UpdateDailyTodoRequest updateDailyTodoRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        dailyPlannerServiceImpl.updateDailyTodo(principalDetails.getUser(), updateDailyTodoRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_DAILY_TODO));
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

    @PutMapping("/{userId}/daily/retrospections")
    public ResponseEntity<BaseResponse> updateRetrospection(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                            @PathVariable("userId") final Long userId,
                                                            @RequestBody @Valid final UpdateRetrospectionRequest updateRetrospectionRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        dailyPlannerServiceImpl.updateRetrospection(principalDetails.getUser(), updateRetrospectionRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_RETROSPECTION));

    }

    @PutMapping("/{userId}/daily/retrospection-images")
    public ResponseEntity<BaseResponse> updateRetrospectionImage(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                                 @PathVariable("userId") final Long userId,
                                                                 @RequestBody @Valid final UpdateRetrospectionImageRequest updateRetrospectionImageRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        dailyPlannerServiceImpl.updateRetrospectionImage(principalDetails.getUser(), updateRetrospectionImageRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_RETROSPECTION_IMAGE));

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

    @PostMapping("/{userId}/daily/timetables")
    public ResponseEntity<BaseResponse> addTimeTable(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                     @PathVariable("userId") final Long userId,
                                                     @RequestBody @Valid final AddTimeTableRequest addTimeTableRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_TIME_TABLE, dailyPlannerServiceImpl.addTimeTable(principalDetails.getUser(), addTimeTableRequest)));
    }

    @DeleteMapping("/{userId}/daily/timetables")
    public ResponseEntity<BaseResponse> removeTimeTable(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final RemoveTimeTableRequest removeTimeTableRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        dailyPlannerServiceImpl.removeTimeTable(principalDetails.getUser(), removeTimeTableRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_TIME_TABLE));
    }

    @PostMapping("/{userId}/weekly/todos")
    public ResponseEntity<BaseResponse> addWeeklyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                      @PathVariable("userId") final Long userId,
                                                      @RequestBody @Valid final AddWeeklyTodoRequest addWeeklyTodoRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_WEEKLY_TODO, weeklyPlannerServiceImpl.addWeeklyTodo(principalDetails.getUser(), addWeeklyTodoRequest)));
    }

    @PutMapping("/{userId}/weekly/todos")
    public ResponseEntity<BaseResponse> updateWeeklyTodoContent(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                                @PathVariable("userId") final Long userId,
                                                                @RequestBody @Valid final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        weeklyPlannerServiceImpl.updateWeeklyTodoContent(principalDetails.getUser(), updateWeeklyTodoContentRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_WEEKLY_TODO_CONTENT));
    }

}
