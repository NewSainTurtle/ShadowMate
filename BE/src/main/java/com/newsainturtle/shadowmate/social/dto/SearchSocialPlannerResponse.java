package com.newsainturtle.shadowmate.social.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchSocialPlannerResponse {

    private long pageNumber;

    private long totalPage;

    private String sort;

    private List<SearchSocialResponse> socialList;
}
