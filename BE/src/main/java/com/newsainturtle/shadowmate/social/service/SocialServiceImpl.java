package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.social.dto.SearchSocialPlannerResponse;
import com.newsainturtle.shadowmate.social.dto.SearchSocialResponse;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.exception.SocialErrorResult;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.newsainturtle.shadowmate.social.exception.SocialErrorResult.NOT_FOUND_SOCIAL;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialServiceImpl implements SocialService {

    private final SocialRepository socialRepository;

    private final UserRepository userRepository;

    private void checkValidDate(final String startDate, final String endDate) {
        final String datePattern = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$";
        if (!Pattern.matches(datePattern, startDate) || !Pattern.matches(datePattern, endDate)) {
            throw new SocialException(SocialErrorResult.INVALID_DATE_FORMAT);
        }
        if (startDate.compareTo(endDate) > 0) {
            throw new SocialException(SocialErrorResult.INVALID_DATE_PERIOD);
        }

    }

    private void checkSortFormat(String sort) {
        if (!sort.equals("latest") && !sort.equals("popularity")) {
            throw new SocialException(SocialErrorResult.BAD_REQUEST_SORT);
        }
    }

    @Override
    public SearchSocialPlannerResponse getSocial(final String sort, final int pageNumber, final String nickname, final String startDate, final String endDate) {
        checkSortFormat(sort);
        if ((startDate == null && endDate == null) || (startDate.isEmpty() && endDate.isEmpty())) {
            if (nickname == null || nickname.isEmpty()) return getSocialList(sort, pageNumber);
            else return getSocialList(sort, pageNumber, nickname);
        } else {
            checkValidDate(startDate, endDate);
            if (nickname == null || nickname.isEmpty()) return getSocialList(sort, pageNumber, startDate, endDate);
            else return getSocialList(sort, pageNumber, nickname, startDate, endDate);
        }
    }

    private SearchSocialPlannerResponse getSocialList(final String sort, final int pageNumber) {
        List<Social> socialList;
        int totalCount = socialRepository.countByDeleteTimeIsNull();
        int totalPage = totalCount / 6 + 1;
        if (totalCount == 0) {
            return SearchSocialPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(totalPage)
                    .sort(sort)
                    .socialList(new ArrayList<>())
                    .build();
        }
        if (sort.equals("latest")) {
            socialList = socialRepository.findAllByDeleteTimeIsNullSortLatest(PageRequest.of(pageNumber - 1, 6));
        } else {
            socialList = socialRepository.findAllByDeleteTimeIsNullSortPopularity(PageRequest.of(pageNumber - 1, 6));
        }

        if (0 == socialList.size() % 6) {
            totalPage--;
        }

        return SearchSocialPlannerResponse.builder()
                .pageNumber(pageNumber)
                .totalPage(totalPage)
                .sort(sort)
                .socialList(makeSearchSocialResponseList(socialList))
                .build();
    }

    private SearchSocialPlannerResponse getSocialList(final String sort, final int pageNumber, final String nickname) {
        List<Social> socialList;
        User owner = userRepository.findByNicknameAndPlannerAccessScope(nickname, PlannerAccessScope.PUBLIC);
        int totalCount = owner == null ? 0 : socialRepository.countByOwnerIdAndDeleteTimeIsNull(owner.getId());
        int totalPage = totalCount / 6 + 1;
        if (totalCount == 0) {
            return SearchSocialPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(totalPage)
                    .sort(sort)
                    .socialList(new ArrayList<>())
                    .build();
        }

        if (sort.equals("latest")) {
            socialList = socialRepository.findAllByOwnerIdAndDeleteTimeIsNullSortLatest(owner.getId(), PageRequest.of(pageNumber - 1, 6));
        } else {
            socialList = socialRepository.findAllByOwnerIdAndDeleteTimeIsNullSortPopularity(owner.getId(), PageRequest.of(pageNumber - 1, 6));
        }

        if (0 == socialList.size() % 6) {
            totalPage--;
        }
        return SearchSocialPlannerResponse.builder()
                .pageNumber(pageNumber)
                .totalPage(totalPage)
                .sort(sort)
                .socialList(makeSearchSocialResponseList(socialList))
                .build();
    }

    private SearchSocialPlannerResponse getSocialList(final String sort, final int pageNumber, final String startDate, final String endDate) {
        List<Social> socialList;
        int totalCount = socialRepository.countByDeleteTimeIsNullAndPeriod(startDate, endDate);
        int totalPage = totalCount / 6 + 1;
        if (totalCount == 0) {
            return SearchSocialPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(totalPage)
                    .sort(sort)
                    .socialList(new ArrayList<>())
                    .build();
        }
        if (sort.equals("latest")) {
            socialList = socialRepository.findAllByDeleteTimeIsNullAndPeriodSortLatest(startDate, endDate, PageRequest.of(pageNumber - 1, 6));
        } else {
            socialList = socialRepository.findAllByDeleteTimeIsNullAndPeriodSortPopularity(startDate, endDate, PageRequest.of(pageNumber - 1, 6));
        }

        if (0 == socialList.size() % 6) {
            totalPage--;
        }
        return SearchSocialPlannerResponse.builder()
                .pageNumber(pageNumber)
                .totalPage(totalPage)
                .sort(sort)
                .socialList(makeSearchSocialResponseList(socialList))
                .build();
    }

    private SearchSocialPlannerResponse getSocialList(final String sort, final int pageNumber, final String nickname, final String startDate, final String endDate) {
        List<Social> socialList;
        User owner = userRepository.findByNicknameAndPlannerAccessScope(nickname, PlannerAccessScope.PUBLIC);
        int totalCount = owner == null ? 0 : socialRepository.countByOwnerIdAndDeleteTimeIsNullAndPeriod(owner.getId(), startDate, endDate);
        int totalPage = totalCount / 6 + 1;
        if (totalCount == 0) {
            return SearchSocialPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(totalPage)
                    .sort(sort)
                    .socialList(new ArrayList<>())
                    .build();
        }
        if (sort.equals("latest")) {
            socialList = socialRepository.findAllByOwnerIdAndDeleteTimeIsNullAndPeriodSortLatest(owner.getId(), startDate, endDate, PageRequest.of(pageNumber - 1, 6));
        } else {
            socialList = socialRepository.findAllByOwnerIdAndDeleteTimeIsNullAndPeriodSortPopularity(owner.getId(), startDate, endDate, PageRequest.of(pageNumber - 1, 6));
        }


        if (0 == socialList.size() % 6) {
            totalPage--;
        }
        return SearchSocialPlannerResponse.builder()
                .pageNumber(pageNumber)
                .totalPage(totalPage)
                .sort(sort)
                .socialList(makeSearchSocialResponseList(socialList))
                .build();
    }

    @Override
    @Transactional
    public void deleteSocial(final long socialId) {
        Optional<Social> social = socialRepository.findById(socialId);
        if (social.isPresent()) {
            socialRepository.delete(social.get());
        } else {
            throw new SocialException(NOT_FOUND_SOCIAL);
        }
    }

    private List<SearchSocialResponse> makeSearchSocialResponseList(List<Social> socialList) {
        return socialList.stream()
                .map(social -> SearchSocialResponse.builder()
                        .socialId(social.getId())
                        .socialImage(social.getSocialImage())
                        .dailyPlannerDay(social.getDailyPlanner().getDailyPlannerDay())
                        .userId(social.getDailyPlanner().getUser().getId())
                        .statusMessage(social.getDailyPlanner().getUser().getStatusMessage())
                        .nickname(social.getDailyPlanner().getUser().getNickname())
                        .profileImage(social.getDailyPlanner().getUser().getProfileImage())
                        .build())
                .collect(Collectors.toList());
    }
}