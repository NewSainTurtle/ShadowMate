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

    @Builder
    public VisitorBookResponse(final long visitorBookId, final long visitorId, final String visitorNickname,
                               final String visitorProfileImage, final String visitorBookContent, final String writeDateTime) {
        this.visitorBookId = visitorBookId;
        this.visitorId = visitorId;
        this.visitorNickname = visitorNickname;
        this.visitorProfileImage = visitorProfileImage;
        this.visitorBookContent = visitorBookContent;
        this.writeDateTime = writeDateTime;
    }

}
