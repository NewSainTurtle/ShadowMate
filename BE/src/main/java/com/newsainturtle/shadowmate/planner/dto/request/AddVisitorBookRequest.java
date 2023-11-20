package com.newsainturtle.shadowmate.planner.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddVisitorBookRequest {

    @NotNull
    @Size(max = 30)
    private String visitorBookContent;

    @Builder
    public AddVisitorBookRequest(final String visitorBookContent) {
        this.visitorBookContent = visitorBookContent;
    }
}
