package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.*;
import com.newsainturtle.shadowmate.planner.dto.response.AddVisitorBookResponse;
import com.newsainturtle.shadowmate.user.entity.User;

public interface MonthlyPlannerService {
    AddVisitorBookResponse addVisitorBook(final User visitor, final long ownerId, final AddVisitorBookRequest addVisitorBookRequest);
}
