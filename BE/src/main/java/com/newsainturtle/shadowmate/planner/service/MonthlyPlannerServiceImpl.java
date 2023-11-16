package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.response.AddVisitorBookResponse;
import com.newsainturtle.shadowmate.planner.entity.VisitorBook;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.VisitorBookRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyPlannerServiceImpl implements MonthlyPlannerService {

    private final UserRepository userRepository;
    private final VisitorBookRepository visitorBookRepository;

    private String localDateTimeToString(final LocalDateTime time) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        return time.format(formatter);
    }

    @Override
    @Transactional
    public AddVisitorBookResponse addVisitorBook(final User visitor, final long ownerId, final AddVisitorBookRequest addVisitorBookRequest) {
        final User owner = userRepository.findByIdAndWithdrawalIsFalse(ownerId);
        if (owner == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_USER);
        }
        if (owner.getId().equals(visitor.getId())) {
            throw new PlannerException(PlannerErrorResult.FAILED_SELF_VISITOR_BOOK_WRITING);
        }

        final VisitorBook visitorBook = visitorBookRepository.save(VisitorBook.builder()
                .owner(owner)
                .visitor(visitor)
                .visitorBookContent(addVisitorBookRequest.getVisitorBookContent())
                .build());

        return AddVisitorBookResponse.builder()
                .visitorBookId(visitorBook.getId())
                .visitorNickname(visitor.getNickname())
                .visitorProfileImage(visitor.getProfileImage())
                .visitorBookContent(visitorBook.getVisitorBookContent())
                .writeDateTime(localDateTimeToString(visitorBook.getCreateTime()))
                .build();
    }
}
