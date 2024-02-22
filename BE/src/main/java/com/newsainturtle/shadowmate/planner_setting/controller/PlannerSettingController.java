package com.newsainturtle.shadowmate.planner_setting.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.planner_setting.dto.request.*;
import com.newsainturtle.shadowmate.planner_setting.service.UserPlannerSettingService;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerRoutineService;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingService;
import com.newsainturtle.shadowmate.planner_setting.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.newsainturtle.shadowmate.planner_setting.constant.PlannerSettingConstant.*;

@RestController
@RequestMapping("/api/planner-settings")
@RequiredArgsConstructor
public class PlannerSettingController {

    private final PlannerSettingService plannerSettingService;
    private final PlannerRoutineService plannerRoutineService;
    private final UserPlannerSettingService plannerAccessScopeService;
    private final RoutineService routineService;
    private final AuthService authService;

    @PostMapping("/{userId}/categories")
    public ResponseEntity<BaseResponse> addCategory(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                    @PathVariable("userId") final Long userId,
                                                    @RequestBody @Valid final AddCategoryRequest addCategoryRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_CATEGORY, plannerSettingService.addCategory(principalDetails.getUser(), addCategoryRequest)));
    }

    @DeleteMapping("/{userId}/categories")
    public ResponseEntity<BaseResponse> removeCategory(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestBody @Valid final RemoveCategoryRequest removeCategoryRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        plannerRoutineService.removeCategory(principalDetails.getUser(), removeCategoryRequest);
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_REMOVE_CATEGORY));
    }

    @PutMapping("/{userId}/categories")
    public ResponseEntity<BaseResponse> updateCategory(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestBody @Valid final UpdateCategoryRequest updateCategoryRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        plannerSettingService.updateCategory(principalDetails.getUser(), updateCategoryRequest);
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_UPDATE_CATEGORY));
    }

    @GetMapping("/{userId}/categories")
    public ResponseEntity<BaseResponse> getCategoryColor(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                         @PathVariable("userId") final Long userId) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_GET_CATEGORY_LIST, plannerSettingService.getCategoryList(principalDetails.getUser())));
    }

    @GetMapping("/{userId}/categories/colors")
    public ResponseEntity<BaseResponse> getCategoryColorList(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                             @PathVariable("userId") final Long userId) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_GET_CATEGORY_COLOR_LIST, plannerSettingService.getCategoryColorList()));
    }

    @PutMapping("/{userId}/access-scopes")
    public ResponseEntity<BaseResponse> setAccessScope(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestBody @Valid final SetAccessScopeRequest setAccessScopeRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        plannerAccessScopeService.setAccessScope(principalDetails.getUser(), setAccessScopeRequest);
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_SET_PLANNER_ACCESS_SCOPE));
    }

    @PostMapping("/{userId}/d-days")
    public ResponseEntity<BaseResponse> addDday(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                @PathVariable("userId") final Long userId,
                                                @RequestBody @Valid final AddDdayRequest addDdayRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_DDAY, plannerSettingService.addDday(principalDetails.getUser(), addDdayRequest)));
    }

    @GetMapping("/{userId}/d-days")
    public ResponseEntity<BaseResponse> getDdayList(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                    @PathVariable("userId") final Long userId) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_GET_DDAY_LIST, plannerSettingService.getDdayList(principalDetails.getUser())));
    }

    @DeleteMapping("/{userId}/d-days")
    public ResponseEntity<BaseResponse> removeDday(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                   @PathVariable("userId") final Long userId,
                                                   @RequestBody @Valid final RemoveDdayRequest removeDdayRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        plannerSettingService.removeDday(principalDetails.getUser(), removeDdayRequest);
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_REMOVE_DDAY));
    }

    @PutMapping("/{userId}/d-days")
    public ResponseEntity<BaseResponse> updateDday(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                   @PathVariable("userId") final Long userId,
                                                   @RequestBody @Valid final UpdateDdayRequest updateDdayRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        plannerSettingService.updateDday(principalDetails.getUser(), updateDdayRequest);
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_UPDATE_DDAY));
    }

    @PostMapping("/{userId}/routines")
    public ResponseEntity<BaseResponse> addRoutine(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                   @PathVariable("userId") final Long userId,
                                                   @RequestBody @Valid final AddRoutineRequest addRoutineRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_ROUTINE, plannerRoutineService.addRoutine(principalDetails.getUser(), addRoutineRequest)));
    }

    @GetMapping("/{userId}/routines")
    public ResponseEntity<BaseResponse> getRoutineList(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_GET_ROUTINE_LIST, routineService.getRoutineList(principalDetails.getUser())));
    }

    @DeleteMapping("/{userId}/routines")
    public ResponseEntity<BaseResponse> removeRoutine(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                      @PathVariable("userId") final Long userId,
                                                      @RequestBody @Valid final RemoveRoutineRequest removeRoutineRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        routineService.removeRoutine(principalDetails.getUser(), removeRoutineRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_REMOVE_ROUTINE));
    }

    @PutMapping("/{userId}/routines")
    public ResponseEntity<BaseResponse> updateRoutine(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                      @PathVariable("userId") final Long userId,
                                                      @RequestBody @Valid final UpdateRoutineRequest updateRoutineRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        plannerRoutineService.updateRoutine(principalDetails.getUser(), updateRoutineRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_ROUTINE));
    }
}
