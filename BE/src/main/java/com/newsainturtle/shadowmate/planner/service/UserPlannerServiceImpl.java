package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.AddDailyLikeRequest;
import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveDailyLikeRequest;
import com.newsainturtle.shadowmate.planner.dto.response.SearchVisitorBookResponse;
import com.newsainturtle.shadowmate.planner.dto.response.VisitorBookResponse;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPlannerServiceImpl implements UserPlannerService {

    private final DailyPlannerService dailyPlannerService;
    private final MonthlyPlannerService monthlyPlannerService;
    private final UserService userService;

    @Override
    @Transactional
    public void addDailyLike(final User user, final Long plannerWriterId, final AddDailyLikeRequest addDailyPlannerLikeRequest) {
        dailyPlannerService.addDailyLike(user, userService.getUserById(plannerWriterId), addDailyPlannerLikeRequest);
    }

    @Override
    @Transactional
    public void removeDailyLike(final User user, final Long plannerWriterId, final RemoveDailyLikeRequest removeDailyLikeRequest) {
        dailyPlannerService.removeDailyLike(user, userService.getUserById(plannerWriterId), removeDailyLikeRequest);
    }

    @Override
    @Transactional
    public VisitorBookResponse addVisitorBook(final User visitor, final long ownerId, final AddVisitorBookRequest addVisitorBookRequest) {
        return monthlyPlannerService.addVisitorBook(visitor, userService.getUserById(ownerId), addVisitorBookRequest);
    }

    @Override
    public SearchVisitorBookResponse searchVisitorBook(final User visitor, final long ownerId, final long lastVisitorBookId) {
        return monthlyPlannerService.searchVisitorBook(visitor, userService.getUserById(ownerId), lastVisitorBookId);
    }
}
