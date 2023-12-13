package com.newsainturtle.shadowmate.planner.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoveVisitorBookRequest {

    @NotNull
    private Long visitorBookId;

    @Builder
    public RemoveVisitorBookRequest(final Long visitorBookId) {
        this.visitorBookId = visitorBookId;
    }
}
