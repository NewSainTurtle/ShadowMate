package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.response.SearchVisitorBookResponse;
import com.newsainturtle.shadowmate.planner.dto.response.VisitorBookResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface UserPlannerService {
    VisitorBookResponse addVisitorBook(final User visitor, final long ownerId, final AddVisitorBookRequest addVisitorBookRequest);
    SearchVisitorBookResponse searchVisitorBook(final User visitor, final long ownerId, final long lastVisitorBookId);

}
