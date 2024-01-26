package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VisitorBookResponse {

    private long visitorBookId;
    private long visitorId;
    private String visitorNickname;
    private String visitorProfileImage;
    private String visitorBookContent;
    private String writeDateTime;

}
