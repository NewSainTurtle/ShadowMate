package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddVisitorBookResponse {

    private String visitorNickname;
    private String visitorProfileImage;
    private String visitorBookContent;
    private String writeDateTime;

    @Builder
    public AddVisitorBookResponse(String visitorNickname, String visitorProfileImage, String visitorBookContent, String writeDateTime) {
        this.visitorNickname = visitorNickname;
        this.visitorProfileImage = visitorProfileImage;
        this.visitorBookContent = visitorBookContent;
        this.writeDateTime = writeDateTime;
    }
}
