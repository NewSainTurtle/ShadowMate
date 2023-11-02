package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.response.SearchCalendarResponse;
import com.newsainturtle.shadowmate.planner.dto.response.SearchDailyPlannerResponse;
import com.newsainturtle.shadowmate.planner.dto.response.SearchWeeklyPlannerResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface SearchPlannerService {
    SearchDailyPlannerResponse searchDailyPlanner(final User user, final Long plannerWriterId, final String date);
    SearchCalendarResponse searchCalendar(final User user, final Long plannerWriterId, final String date);
    SearchWeeklyPlannerResponse searchWeeklyPlanner(final User user, final Long plannerWriterId, final String startDate, final String endDate);
}
