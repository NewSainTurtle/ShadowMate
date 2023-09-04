package com.newsainturtle.shadowmate.user.controller;

import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.user.dto.ProfileResponse;
import com.newsainturtle.shadowmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.newsainturtle.shadowmate.user.constant.UserConstant.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/profiles")
    public ResponseEntity<BaseResponse> getProfile(@PathVariable Long userId) {
        System.out.println("get");
        ProfileResponse profileResponse = userService.getProfile(userId);
        return ResponseEntity.ok(BaseResponse.from(
                SUCCESS_PROFILE, profileResponse));
    }
}
