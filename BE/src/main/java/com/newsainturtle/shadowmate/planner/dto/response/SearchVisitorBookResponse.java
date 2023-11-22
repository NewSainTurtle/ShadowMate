package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchVisitorBookResponse {

    private List<VisitorBookResponse> visitorBookResponses;

}
