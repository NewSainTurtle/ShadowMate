package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.planner.dto.response.ShareSocialResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.social.dto.response.SearchSocialPlannerResponse;
import com.newsainturtle.shadowmate.social.dto.response.SearchSocialResponse;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.exception.SocialErrorResult;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.newsainturtle.shadowmate.common.constant.CommonConstant.DATE_PATTERN;
import static com.newsainturtle.shadowmate.social.constant.SocialConstant.SORT_LATEST;
import static com.newsainturtle.shadowmate.social.constant.SocialConstant.SORT_POPULARITY;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialServiceImpl implements SocialService {

    private final SocialRepository socialRepository;

    private void checkValidDate(final String startDate, final String endDate) {
        if (startDate == null || endDate == null || !Pattern.matches(DATE_PATTERN, startDate) || !Pattern.matches(DATE_PATTERN, endDate)) {
            throw new SocialException(SocialErrorResult.INVALID_DATE_FORMAT);
        }
        if (startDate.compareTo(endDate) > 0) {
            throw new SocialException(SocialErrorResult.INVALID_DATE_PERIOD);
        }
    }

    private void checkSortFormat(String sort) {
        if (!sort.equals(SORT_LATEST) && !sort.equals(SORT_POPULARITY)) {
            throw new SocialException(SocialErrorResult.BAD_REQUEST_SORT);
        }
    }

    @Override
    public SearchSocialPlannerResponse getSocial(final String sort, final int pageNumber, final String startDate, final String endDate) {
        checkSortFormat(sort);
        int totalCount;
        if ((startDate == null || startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
            totalCount = socialRepository.countByDeleteTimeIsNull();
        } else {
            checkValidDate(startDate, endDate);
            totalCount = socialRepository.countByDeleteTimeIsNullAndPeriod(startDate, endDate);
        }

        int totalPage = totalCount / 6 + 1;
        if (totalCount == 0) {
            return SearchSocialPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(totalPage)
                    .sort(sort)
                    .socialList(new ArrayList<>())
                    .build();
        }

        return getSocialList(sort, pageNumber, totalPage, startDate, endDate);
    }

    @Override
    public SearchSocialPlannerResponse getSocial(final String sort, final int pageNumber, final User owner, final String startDate, final String endDate) {
        checkSortFormat(sort);
        int totalCount;
        if ((startDate == null || startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
            totalCount = owner == null ? 0 : socialRepository.countByOwnerIdAndDeleteTimeIsNull(owner.getId());
        } else {
            checkValidDate(startDate, endDate);
            totalCount = owner == null ? 0 : socialRepository.countByOwnerIdAndDeleteTimeIsNullAndPeriod(owner.getId(), startDate, endDate);
        }

        int totalPage = totalCount / 6 + 1;
        if (totalCount == 0) {
            return SearchSocialPlannerResponse.builder()
                    .pageNumber(pageNumber)
                    .totalPage(totalPage)
                    .sort(sort)
                    .socialList(new ArrayList<>())
                    .build();
        }

        return getSocialList(sort, pageNumber, totalPage, owner, startDate, endDate);
    }

    private List<Social> getSocialList(final String sort, final int pageNumber) {
        if (sort.equals(SORT_LATEST)) {
            return socialRepository.findAllByDeleteTimeIsNullSortLatest(PageRequest.of(pageNumber - 1, 6));
        }
        return socialRepository.findAllByDeleteTimeIsNullSortPopularity(PageRequest.of(pageNumber - 1, 6));
    }

    private List<Social> getSocialList(final String sort, final int pageNumber, final String startDate, final String endDate) {
        if (sort.equals(SORT_LATEST)) {
            return socialRepository.findAllByDeleteTimeIsNullAndPeriodSortLatest(startDate, endDate, PageRequest.of(pageNumber - 1, 6));
        }
        return socialRepository.findAllByDeleteTimeIsNullAndPeriodSortPopularity(startDate, endDate, PageRequest.of(pageNumber - 1, 6));
    }

    private List<Social> getSocialList(final String sort, final int pageNumber, final User owner) {
        if (sort.equals(SORT_LATEST)) {
            return socialRepository.findAllByOwnerIdAndDeleteTimeIsNullSortLatest(owner.getId(), PageRequest.of(pageNumber - 1, 6));
        }
        return socialRepository.findAllByOwnerIdAndDeleteTimeIsNullSortPopularity(owner.getId(), PageRequest.of(pageNumber - 1, 6));
    }

    private List<Social> getSocialList(final String sort, final int pageNumber, final User owner, final String startDate, final String endDate) {
        if (sort.equals(SORT_LATEST)) {
            return socialRepository.findAllByOwnerIdAndDeleteTimeIsNullAndPeriodSortLatest(owner.getId(), startDate, endDate, PageRequest.of(pageNumber - 1, 6));
        }
        return socialRepository.findAllByOwnerIdAndDeleteTimeIsNullAndPeriodSortPopularity(owner.getId(), startDate, endDate, PageRequest.of(pageNumber - 1, 6));
    }

    private SearchSocialPlannerResponse getSocialList(final String sort, final int pageNumber, int totalPage, final String startDate, final String endDate) {
        List<Social> socialList;
        if ((startDate == null || startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
            socialList = getSocialList(sort, pageNumber);
        } else {
            socialList = getSocialList(sort, pageNumber, startDate, endDate);
        }

        if (socialList.size() % 6 == 0) totalPage--;
        return SearchSocialPlannerResponse.builder()
                .pageNumber(pageNumber)
                .totalPage(totalPage)
                .sort(sort)
                .socialList(makeSearchSocialResponseList(socialList))
                .build();
    }

    private SearchSocialPlannerResponse getSocialList(final String sort, final int pageNumber, int totalPage, final User owner, final String startDate, final String endDate) {
        List<Social> socialList;
        if ((startDate == null || startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
            socialList = getSocialList(sort, pageNumber, owner);
        } else {
            socialList = getSocialList(sort, pageNumber, owner, startDate, endDate);
        }

        if (socialList.size() % 6 == 0) totalPage--;
        return SearchSocialPlannerResponse.builder()
                .pageNumber(pageNumber)
                .totalPage(totalPage)
                .sort(sort)
                .socialList(makeSearchSocialResponseList(socialList))
                .build();
    }

    @Override
    @Transactional
    public void deleteSocial(final User user, final long socialId) {
        socialRepository.deleteByIdAndOwnerId(socialId, user.getId());
    }

    @Override
    @Transactional
    public ShareSocialResponse shareSocial(final User user, final DailyPlanner dailyPlanner, final String socialImage) {
        final Social social = socialRepository.findByDailyPlanner(dailyPlanner);
        if (social != null) {
            throw new SocialException(SocialErrorResult.ALREADY_SHARED_SOCIAL);
        }
        final long socialId = socialRepository.save(Social.builder()
                .dailyPlanner(dailyPlanner)
                .socialImage(socialImage)
                .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                .ownerId(user.getId())
                .build()).getId();
        return ShareSocialResponse.builder().socialId(socialId).build();
    }

    @Override
    @Transactional
    public void updateDeleteTimeAll(final LocalDateTime time, final List<DailyPlanner> dailyPlannerList) {
        socialRepository.updateDeleteTimeAll(time, dailyPlannerList);
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

    @Override
    public Long getSocialId(final DailyPlanner dailyPlanner){
        final Social shareSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner);
        if(shareSocial == null) return null;
        return shareSocial.getId();
    }
}