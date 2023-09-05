package com.newsainturtle.shadowmate.planner_setting.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.planner_setting.dto.AddCategoryRequest;
import com.newsainturtle.shadowmate.planner_setting.dto.SetAccessScopeRequest;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingService;
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

    private final PlannerSettingService plannerSettingServiceImpl;
    private final AuthService authServiceImpl;

    @PostMapping("/{userId}/categories")
    public ResponseEntity<BaseResponse> addCategory(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable("userId") final Long userId,
                                                    @RequestBody @Valid final AddCategoryRequest addCategoryRequest) {
        plannerSettingServiceImpl.addCategory(userId, addCategoryRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_CATEGORY));
    }

    @GetMapping("/{userId}/categories")
    public ResponseEntity<BaseResponse> getCategoryColor(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                         @PathVariable("userId") final Long userId) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_GET_CATEGORY_LIST, plannerSettingServiceImpl.getCategoryList(userId)));
    }

    @GetMapping("/{userId}/categories/colors")
    public ResponseEntity<BaseResponse> getCategoryColorList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                             @PathVariable("userId") final Long userId) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_GET_CATEGORY_COLOR_LIST, plannerSettingServiceImpl.getCategoryColorList()));
    }

    @PutMapping("/{userId}/access-scopes")
    public ResponseEntity<BaseResponse> setAccessScope(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestBody @Valid final SetAccessScopeRequest setAccessScopeRequest) {
        authServiceImpl.certifyUser(userId, principalDetails.getUser());
        plannerSettingServiceImpl.setAccessScope(principalDetails.getUser(), setAccessScopeRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SET_PLANNER_ACCESS_SCOPE));
    }
}
