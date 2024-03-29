package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.AddDailyLikeRequest;
import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveDailyLikeRequest;
import com.newsainturtle.shadowmate.planner.dto.response.*;
import com.newsainturtle.shadowmate.user.entity.User;

public interface UserPlannerService {
    void addDailyLike(final User user, final Long plannerWriterId, final AddDailyLikeRequest addDailyPlannerLikeRequest);
    void removeDailyLike(final User user, final Long plannerWriterId, final RemoveDailyLikeRequest removeDailyLikeRequest);
    VisitorBookResponse addVisitorBook(final User visitor, final long ownerId, final AddVisitorBookRequest addVisitorBookRequest);
    SearchVisitorBookResponse searchVisitorBook(final User visitor, final long ownerId, final long lastVisitorBookId);
    SearchDailyPlannerResponse searchDailyPlanner(final User user, final Long plannerWriterId, final String date);
    SearchCalendarResponse searchCalendar(final User user, final Long plannerWriterId, final String date);
    SearchWeeklyPlannerResponse searchWeeklyPlanner(final User user, final Long plannerWriterId, final String startDate, final String endDate);
}
