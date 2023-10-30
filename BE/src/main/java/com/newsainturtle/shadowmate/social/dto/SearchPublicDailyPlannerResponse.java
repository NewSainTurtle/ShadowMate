package com.newsainturtle.shadowmate.social.dto;

import com.newsainturtle.shadowmate.social.entity.Social;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchPublicDailyPlannerResponse {

    private long pageNumber;

    private long totalPage;

    private String sort;

    List<Social> socialList;
}
