package com.newsainturtle.shadowmate.planner.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.service.*;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerRoutineService;
import com.newsainturtle.shadowmate.social.service.UserPlannerSocialService;
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

    private final DailyPlannerService dailyPlannerService;
    private final WeeklyPlannerService weeklyPlannerService;
    private final MonthlyPlannerService monthlyPlannerService;
    private final UserPlannerService userPlannerService;
    private final SettingPlannerService settingPlannerService;
    private final UserPlannerSocialService userPlannerSocialService;
    private final PlannerRoutineService plannerRoutineService;
    private final AuthService authService;

    @PostMapping("/{userId}/daily/todos")
    public ResponseEntity<BaseResponse> addDailyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                     @PathVariable("userId") final Long userId,
                                                     @RequestBody @Valid final AddDailyTodoRequest addDailyTodoRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_DAILY_TODO, settingPlannerService.addDailyTodo(principalDetails.getUser(), addDailyTodoRequest)));
    }

    @PutMapping("/{userId}/daily/todos")
    public ResponseEntity<BaseResponse> updateDailyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final UpdateDailyTodoRequest updateDailyTodoRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        settingPlannerService.updateDailyTodo(principalDetails.getUser(), updateDailyTodoRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_DAILY_TODO));
    }

    @PutMapping("/{userId}/daily/todo-sequence")
    public ResponseEntity<BaseResponse> changeDailyTodoSequence(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                                @PathVariable("userId") final Long userId,
                                                                @RequestBody @Valid final ChangeDailyTodoSequenceRequest changeDailyTodoSequenceRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        dailyPlannerService.changeDailyTodoSequence(principalDetails.getUser(), changeDailyTodoSequenceRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_CHANGE_DAILY_TODO_SEQUENCE));
    }

    @DeleteMapping("/{userId}/daily/todos")
    public ResponseEntity<BaseResponse> removeDailyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final RemoveDailyTodoRequest removeDailyTodoRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        plannerRoutineService.removeDailyTodo(principalDetails.getUser(), removeDailyTodoRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_DAILY_TODO));
    }

    @PutMapping("/{userId}/daily/today-goals")
    public ResponseEntity<BaseResponse> updateTodayGoal(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final UpdateTodayGoalRequest updateTodayGoalRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        dailyPlannerService.updateTodayGoal(principalDetails.getUser(), updateTodayGoalRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_TODAY_GOAL));
    }

    @PutMapping("/{userId}/daily/tomorrow-goals")
    public ResponseEntity<BaseResponse> updateTomorrowGoal(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                           @PathVariable("userId") final Long userId,
                                                           @RequestBody @Valid final UpdateTomorrowGoalRequest updateTomorrowGoalRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        dailyPlannerService.updateTomorrowGoal(principalDetails.getUser(), updateTomorrowGoalRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_TOMORROW_GOAL));
    }

    @PutMapping("/{userId}/daily/retrospections")
    public ResponseEntity<BaseResponse> updateRetrospection(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                            @PathVariable("userId") final Long userId,
                                                            @RequestBody @Valid final UpdateRetrospectionRequest updateRetrospectionRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        dailyPlannerService.updateRetrospection(principalDetails.getUser(), updateRetrospectionRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_RETROSPECTION));

    }

    @PutMapping("/{userId}/daily/retrospection-images")
    public ResponseEntity<BaseResponse> updateRetrospectionImage(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                                 @PathVariable("userId") final Long userId,
                                                                 @RequestBody @Valid final UpdateRetrospectionImageRequest updateRetrospectionImageRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        dailyPlannerService.updateRetrospectionImage(principalDetails.getUser(), updateRetrospectionImageRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_RETROSPECTION_IMAGE));

    }

    @PostMapping("/{userId}/daily/likes")
    public ResponseEntity<BaseResponse> addDailyLike(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                     @PathVariable("userId") final Long userId,
                                                     @RequestBody @Valid final AddDailyLikeRequest addDailyLikeRequest) {
        userPlannerService.addDailyLike(principalDetails.getUser(), userId, addDailyLikeRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_DAILY_LIKE));
    }

    @DeleteMapping("/{userId}/daily/likes")
    public ResponseEntity<BaseResponse> removeDailyLike(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final RemoveDailyLikeRequest removeDailyLikeRequest) {
        userPlannerService.removeDailyLike(principalDetails.getUser(), userId, removeDailyLikeRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_DAILY_LIKE));
    }

    @PostMapping("/{userId}/daily/timetables")
    public ResponseEntity<BaseResponse> addTimeTable(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                     @PathVariable("userId") final Long userId,
                                                     @RequestBody @Valid final AddTimeTableRequest addTimeTableRequest) {
        authService.certifyUser(userId, principalDetails.getUser());

        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_TIME_TABLE, dailyPlannerService.addTimeTable(principalDetails.getUser(), addTimeTableRequest)));
    }

    @DeleteMapping("/{userId}/daily/timetables")
    public ResponseEntity<BaseResponse> removeTimeTable(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final RemoveTimeTableRequest removeTimeTableRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        dailyPlannerService.removeTimeTable(principalDetails.getUser(), removeTimeTableRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_TIME_TABLE));
    }

    @PostMapping("/{userId}/weekly/todos")
    public ResponseEntity<BaseResponse> addWeeklyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                      @PathVariable("userId") final Long userId,
                                                      @RequestBody @Valid final AddWeeklyTodoRequest addWeeklyTodoRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_WEEKLY_TODO, weeklyPlannerService.addWeeklyTodo(principalDetails.getUser(), addWeeklyTodoRequest)));
    }

    @PutMapping("/{userId}/weekly/todos")
    public ResponseEntity<BaseResponse> updateWeeklyTodoContent(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                                @PathVariable("userId") final Long userId,
                                                                @RequestBody @Valid final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        weeklyPlannerService.updateWeeklyTodoContent(principalDetails.getUser(), updateWeeklyTodoContentRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_WEEKLY_TODO_CONTENT));
    }

    @PutMapping("/{userId}/weekly/todos-status")
    public ResponseEntity<BaseResponse> updateWeeklyTodoStatus(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                               @PathVariable("userId") final Long userId,
                                                               @RequestBody @Valid final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        weeklyPlannerService.updateWeeklyTodoStatus(principalDetails.getUser(), updateWeeklyTodoStatusRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_WEEKLY_TODO_STATUS));
    }

    @DeleteMapping("/{userId}/weekly/todos")
    public ResponseEntity<BaseResponse> removeWeeklyTodo(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                         @PathVariable("userId") final Long userId,
                                                         @RequestBody @Valid final RemoveWeeklyTodoRequest removeWeeklyTodoRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        weeklyPlannerService.removeWeeklyTodo(principalDetails.getUser(), removeWeeklyTodoRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_WEEKLY_TODO));
    }

    @PostMapping("/{userId}/daily/social")
    public ResponseEntity<BaseResponse> shareSocial(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                    @PathVariable("userId") final Long userId,
                                                    @RequestBody @Valid final ShareSocialRequest shareSocialRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_SHARE_SOCIAL, userPlannerSocialService.shareSocial(principalDetails.getUser(), shareSocialRequest)));
    }

    @GetMapping("/{userId}/daily")
    public ResponseEntity<BaseResponse> searchDailyPlanner(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                           @PathVariable("userId") final Long userId,
                                                           @RequestParam(name = "date") final String date) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_DAILY_PLANNER,
                userPlannerService.searchDailyPlanner(principalDetails.getUser(), userId, date)));
    }

    @GetMapping("/{userId}/weekly")
    public ResponseEntity<BaseResponse> searchWeeklyPlanner(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                            @PathVariable("userId") final Long userId,
                                                            @RequestParam(name = "start-date") final String startDate,
                                                            @RequestParam(name = "end-date") final String endDate) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_WEEKLY_PLANNER,
                userPlannerService.searchWeeklyPlanner(principalDetails.getUser(), userId, startDate, endDate)));
    }

    @GetMapping("/{userId}/calendars")
    public ResponseEntity<BaseResponse> searchCalendar(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestParam(name = "date") final String date) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_CALENDAR,
                userPlannerService.searchCalendar(principalDetails.getUser(), userId, date)));
    }

    @PostMapping("/{userId}/monthly/visitor-books")
    public ResponseEntity<BaseResponse> addVisitorBook(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestBody @Valid final AddVisitorBookRequest addVisitorBookRequest) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_VISITOR_BOOK, userPlannerService.addVisitorBook(principalDetails.getUser(), userId, addVisitorBookRequest)));
    }

    @DeleteMapping("/{userId}/monthly/visitor-books")
    public ResponseEntity<BaseResponse> removeVisitorBook(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                          @PathVariable("userId") final Long userId,
                                                          @RequestBody @Valid final RemoveVisitorBookRequest removeVisitorBookRequest) {
        monthlyPlannerService.removeVisitorBook(principalDetails.getUser(), userId, removeVisitorBookRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_VISITOR_BOOK));
    }

    @GetMapping("/{userId}/monthly/visitor-books")
    public ResponseEntity<BaseResponse> searchVisitorBook(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                          @PathVariable("userId") final Long userId,
                                                          @RequestParam(name = "last") final long lastVisitorBookId) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_VISITOR_BOOK, userPlannerService.searchVisitorBook(principalDetails.getUser(), userId, lastVisitorBookId)));
    }

}
