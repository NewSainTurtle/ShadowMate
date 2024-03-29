package com.newsainturtle.shadowmate.user.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.planner_setting.service.UserPlannerSettingService;
import com.newsainturtle.shadowmate.user.dto.request.UpdateIntroductionRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdatePasswordRequest;
import com.newsainturtle.shadowmate.user.dto.request.UpdateUserRequest;
import com.newsainturtle.shadowmate.user.dto.response.ProfileResponse;
import com.newsainturtle.shadowmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.newsainturtle.shadowmate.user.constant.UserConstant.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final UserPlannerSettingService userPlannerSettingService;

    @GetMapping("/{userId}/profiles")
    public ResponseEntity<BaseResponse> getProfile(@PathVariable final Long userId) {
        ProfileResponse profileResponse = userService.getProfile(userId);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_PROFILE, profileResponse));
    }

    @GetMapping("/{userId}/introduction")
    public ResponseEntity<BaseResponse> searchIntroduction(@PathVariable("userId") final Long userId) {
        return ResponseEntity
                .ok(BaseResponse.from(SUCCESS_SEARCH_INTRODUCTION, userService.searchIntroduction(userId)));
    }

    @PutMapping("/{userId}/mypages")
    public ResponseEntity<BaseResponse> updateUser(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                   @PathVariable("userId") final Long userId,
                                                   @RequestBody @Valid final UpdateUserRequest updateUserRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        userService.updateUser(principalDetails.getUser(), updateUserRequest);
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_UPDATE_USER));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<BaseResponse> updatePassword(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestBody @Valid final UpdatePasswordRequest updatePasswordRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        userService.updatePassword(principalDetails.getUser(), updatePasswordRequest);
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_UPDATE_PASSWORD));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<BaseResponse> deleteUser(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                   @PathVariable("userId") final Long userId) {
        authService.certifyUser(userId, principalDetails.getUser());
        userPlannerSettingService.deleteUser(principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_DELETE_USER));
    }

    @PutMapping("/{userId}/introduction")
    public ResponseEntity<BaseResponse> updateIntroduction(
            @AuthenticationPrincipal final PrincipalDetails principalDetails,
            @PathVariable("userId") final Long userId,
            @RequestBody @Valid final UpdateIntroductionRequest updateIntroductionRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        userService.updateIntroduction(userId, updateIntroductionRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_UPDATE_INTRODUCTION));
    }
}
