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

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyPlannerServiceImpl extends DateCommonService implements MonthlyPlannerService {

    private final VisitorBookRepository visitorBookRepository;

    @Override
    public void checkValidDate(final String date) {
        final String datePattern = "^([12]\\d{3}-(0[1-9]|1[0-2])-01)$";
        if (!Pattern.matches(datePattern, date)) {
            throw new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT);
        }
    }

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
        return SearchVisitorBookResponse.builder()
                .visitorBookResponses(visitorBooks.stream()
                        .map(visitorBook -> VisitorBookResponse.builder()
                                .visitorBookId(visitorBook.getId())
                                .visitorId(visitorBook.getVisitor().getId())
                                .visitorNickname(visitorBook.getVisitor().getNickname())
                                .visitorProfileImage(visitorBook.getVisitor().getProfileImage())
                                .visitorBookContent(visitorBook.getVisitorBookContent())
                                .writeDateTime(localDateTimeToString(visitorBook.getCreateTime()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void deleteUser(final User user) {
        visitorBookRepository.deleteAllByVisitorId(user.getId());
    }
}