package com.newsainturtle.shadowmate.planner.dto.response;

import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchDailyPlannerResponse {

    private String date;
    private String plannerAccessScope;
    private String dday;
    private String ddayTitle;
    private String todayGoal;
    private String retrospection;
    private String retrospectionImage;
    private String tomorrowGoal;
    private Long shareSocial;
    private boolean like;
    private long likeCount;
    private int studyTimeHour;
    private int studyTimeMinute;
    private List<DailyPlannerTodoResponse> dailyTodos;

    public void setDday(final Dday dday) {
        if (dday != null) {
            this.dday = dday.getDdayDate();
            this.ddayTitle = dday.getDdayTitle();
        }
    }

    public void setShareSocial(final Long socialId) {
        this.shareSocial = socialId;
    }

}
