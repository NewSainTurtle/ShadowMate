package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VisitorBookResponse {

    private long visitorBookId;
    private String visitorNickname;
    private String visitorProfileImage;
    private String visitorBookContent;
    private String writeDateTime;

    @Builder
    public VisitorBookResponse(long visitorBookId, String visitorNickname, String visitorProfileImage, String visitorBookContent, String writeDateTime) {
        this.visitorBookId = visitorBookId;
        this.visitorNickname = visitorNickname;
        this.visitorProfileImage = visitorProfileImage;
        this.visitorBookContent = visitorBookContent;
        this.writeDateTime = writeDateTime;
    }

}
