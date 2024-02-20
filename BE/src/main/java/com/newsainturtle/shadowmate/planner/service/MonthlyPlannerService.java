package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.response.SearchVisitorBookResponse;
import com.newsainturtle.shadowmate.planner.dto.response.VisitorBookResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface MonthlyPlannerService {
    VisitorBookResponse addVisitorBook(final User visitor, final User owner, final AddVisitorBookRequest addVisitorBookRequest);
    void removeVisitorBook(final User visitor, final long ownerId, final RemoveVisitorBookRequest removeVisitorBookRequest);
    SearchVisitorBookResponse searchVisitorBook(final User visitor, final User owner, final long lastVisitorBookId);
    void deleteUser(final User user);
    void checkValidDate(final String date);
}
