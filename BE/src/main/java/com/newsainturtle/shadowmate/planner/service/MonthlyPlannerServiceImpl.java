package com.newsainturtle.shadowmate.planner.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.response.SearchVisitorBookResponse;
import com.newsainturtle.shadowmate.planner.dto.response.VisitorBookResponse;
import com.newsainturtle.shadowmate.planner.entity.VisitorBook;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.VisitorBookRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyPlannerServiceImpl extends DateCommonService implements MonthlyPlannerService {

    private final VisitorBookRepository visitorBookRepository;

    @Override
    @Transactional
    public VisitorBookResponse addVisitorBook(final User visitor, final User owner, final AddVisitorBookRequest addVisitorBookRequest) {
        if (owner.getId().equals(visitor.getId())) {
            throw new PlannerException(PlannerErrorResult.FAILED_SELF_VISITOR_BOOK_WRITING);
        }

        final VisitorBook visitorBook = visitorBookRepository.save(VisitorBook.builder()
                .owner(owner)
                .visitor(visitor)
                .visitorBookContent(addVisitorBookRequest.getVisitorBookContent())
                .build());

        return VisitorBookResponse.builder()
                .visitorBookId(visitorBook.getId())
                .visitorId(visitor.getId())
                .visitorNickname(visitor.getNickname())
                .visitorProfileImage(visitor.getProfileImage())
                .visitorBookContent(visitorBook.getVisitorBookContent())
                .writeDateTime(localDateTimeToString(visitorBook.getCreateTime()))
                .build();
    }

    @Override
    @Transactional
    public void removeVisitorBook(final User visitor, final long ownerId, final RemoveVisitorBookRequest removeVisitorBookRequest) {
        final VisitorBook visitorBook = visitorBookRepository.findByIdAndOwnerId(removeVisitorBookRequest.getVisitorBookId(), ownerId);
        if (visitorBook == null) {
            throw new PlannerException(PlannerErrorResult.INVALID_VISITOR_BOOK);
        }

        if (!visitorBook.getVisitor().getId().equals(visitor.getId())
                && !visitorBook.getOwner().getId().equals(visitor.getId())) {
            throw new PlannerException(PlannerErrorResult.NO_PERMISSION_TO_REMOVE_VISITOR_BOOK);
        }

        visitorBookRepository.deleteById(removeVisitorBookRequest.getVisitorBookId());

    }

    @Override
    public SearchVisitorBookResponse searchVisitorBook(final User visitor, final User owner, final long lastVisitorBookId) {
        final List<VisitorBook> visitorBooks = lastVisitorBookId == 0 ? visitorBookRepository.findTop10ByOwnerOrderByIdDesc(owner) :
                visitorBookRepository.findTop10ByOwnerAndIdLessThanOrderByIdDesc(owner, lastVisitorBookId);
        final List<VisitorBookResponse> visitorBookResponses = new ArrayList<>();
        for (VisitorBook visitorBook : visitorBooks) {
            visitorBookResponses.add(VisitorBookResponse.builder()
                    .visitorBookId(visitorBook.getId())
                    .visitorId(visitorBook.getVisitor().getId())
                    .visitorNickname(visitorBook.getVisitor().getNickname())
                    .visitorProfileImage(visitorBook.getVisitor().getProfileImage())
                    .visitorBookContent(visitorBook.getVisitorBookContent())
                    .writeDateTime(localDateTimeToString(visitorBook.getCreateTime()))
                    .build());
        }

        return SearchVisitorBookResponse.builder().visitorBookResponses(visitorBookResponses).build();
    }

    @Override
    @Transactional
    public void deleteUser(final User user) {
        visitorBookRepository.deleteAllByVisitorId(user.getId());
    }
}