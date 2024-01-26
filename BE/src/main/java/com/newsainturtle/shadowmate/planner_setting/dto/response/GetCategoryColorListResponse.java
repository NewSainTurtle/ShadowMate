package com.newsainturtle.shadowmate.planner_setting.dto.response;

import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetCategoryColorListResponse {
    private List<CategoryColor> categoryColorList;
}
