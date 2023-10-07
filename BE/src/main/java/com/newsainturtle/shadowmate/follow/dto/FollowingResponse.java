package com.newsainturtle.shadowmate.follow.dto;

import com.newsainturtle.shadowmate.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowingResponse {

    private Long followId;

    private String email;

    private String nickname;

    private String profileImage;

    private Long followingId;
}
/*
"followId" : 1,
"email": String,
"nickname": String,
"profileImage" : String,
"followingId" : 2,
 */