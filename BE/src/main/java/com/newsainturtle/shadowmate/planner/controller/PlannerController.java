package com.newsainturtle.shadowmate.planner.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateRetrospectionRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateTodayGoalRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateTomorrowGoalRequest;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerService;
import com.newsainturtle.shadowmate.planner.service.MonthlyPlannerService;
import com.newsainturtle.shadowmate.planner.service.SearchPlannerService;
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
    private final SearchPlannerService searchPlannerServiceImpl;
    private final MonthlyPlannerService monthlyPlannerServiceImpl;
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
        dailyPlannerServiceImpl.addDailyLike(principalDetails.getUser(), userId, addDailyLikeRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_DAILY_LIKE));
    }

    @DeleteMapping("/{userId}/daily/likes")
    public ResponseEntity<BaseResponse> removeDailyLike(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final RemoveDailyLikeRequest removeDailyLikeRequest) {
        dailyPlannerServiceImpl.removeDailyLike(principalDetails.getUser(), userId, removeDailyLikeRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_DAILY_LIKE));
    }

    @PostMapping("/{userId}/daily/timetables")
    public ResponseEntity<BaseResponse> addTimeTable(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                     @PathVariable("userId") final Long userId,
                                                     @RequestBody @Valid final AddTimeTableRequest addTimeTableRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        dailyPlannerServiceImpl.addTimeTable(principalDetails.getUser(), addTimeTableRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_TIME_TABLE));
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

    @PutMapping("/{userId}/weekly/todos-status")
    public ResponseEntity<BaseResponse> updateWeeklyTodoStatus(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                               @PathVariable("userId") final Long userId,
                                                               @RequestBody @Valid final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        weeklyPlannerServiceImpl.updateWeeklyTodoStatus(principalDetails.getUser(), updateWeeklyTodoStatusRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_WEEKLY_TODO_STATUS));
    }

    @DeleteMapping("/{userId}/weekly/todos")
    public ResponseEntity<BaseResponse> removeWeeklyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                         @PathVariable("userId") final Long userId,
                                                         @RequestBody @Valid final RemoveWeeklyTodoRequest removeWeeklyTodoRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        weeklyPlannerServiceImpl.removeWeeklyTodo(principalDetails.getUser(), removeWeeklyTodoRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_WEEKLY_TODO));
    }

    @PostMapping("/{userId}/daily/social")
    public ResponseEntity<BaseResponse> shareSocial(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                    @PathVariable("userId") final Long userId,
                                                    @RequestBody @Valid final ShareSocialRequest shareSocialRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_SHARE_SOCIAL, dailyPlannerServiceImpl.shareSocial(principalDetails.getUser(), shareSocialRequest)));
    }

    @GetMapping("/{userId}/daily")
    public ResponseEntity<BaseResponse> searchDailyPlanner(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                           @PathVariable("userId") final Long userId,
                                                           @RequestParam(name = "date") final String date) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_DAILY_PLANNER,
                searchPlannerServiceImpl.searchDailyPlanner(principalDetails.getUser(), userId, date)));
    }

    @GetMapping("/{userId}/weekly")
    public ResponseEntity<BaseResponse> searchWeeklyPlanner(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                            @PathVariable("userId") final Long userId,
                                                            @RequestParam(name = "start-date") final String startDate,
                                                            @RequestParam(name = "end-date") final String endDate) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_WEEKLY_PLANNER,
                searchPlannerServiceImpl.searchWeeklyPlanner(principalDetails.getUser(), userId, startDate, endDate)));
    }

    @GetMapping("/{userId}/calendars")
    public ResponseEntity<BaseResponse> searchCalendar(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestParam(name = "date") final String date) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_CALENDAR,
                searchPlannerServiceImpl.searchCalendar(principalDetails.getUser(), userId, date)));
    }

    @PostMapping("/{userId}/monthly/visitor-books")
    public ResponseEntity<BaseResponse> addVisitorBook(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestBody @Valid final AddVisitorBookRequest addVisitorBookRequest) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_VISITOR_BOOK, monthlyPlannerServiceImpl.addVisitorBook(principalDetails.getUser(), userId, addVisitorBookRequest)));
    }

    @DeleteMapping("/{userId}/monthly/visitor-books")
    public ResponseEntity<BaseResponse> removeVisitorBook(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                          @PathVariable("userId") final Long userId,
                                                          @RequestBody @Valid final RemoveVisitorBookRequest removeVisitorBookRequest) {
        monthlyPlannerServiceImpl.removeVisitorBook(principalDetails.getUser(), userId, removeVisitorBookRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_VISITOR_BOOK));
    }

    @GetMapping("/{userId}/monthly/visitor-books")
    public ResponseEntity<BaseResponse> searchVisitorBook(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                          @PathVariable("userId") final Long userId,
                                                          @RequestParam(name = "last") final long lastVisitorBookId) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_VISITOR_BOOK, monthlyPlannerServiceImpl.searchVisitorBook(principalDetails.getUser(), userId, lastVisitorBookId)));
    }

}
