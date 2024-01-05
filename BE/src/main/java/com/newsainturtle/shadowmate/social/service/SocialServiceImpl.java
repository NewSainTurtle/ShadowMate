package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.social.dto.SearchPublicDailyPlannerResponse;
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
import java.util.stream.Collectors;

import static com.newsainturtle.shadowmate.social.exception.SocialErrorResult.NOT_FOUND_SOCIAL;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialServiceImpl implements SocialService {

    private final SocialRepository socialRepository;

    private final UserRepository userRepository;

    @Override
    public SearchPublicDailyPlannerResponse searchPublicDailyPlanner(final String sort, final int pageNumber, final String nickname) {
        if (!sort.equals("latest") && !sort.equals("popularity")) {
            throw new SocialException(SocialErrorResult.BAD_REQUEST_SORT);
        }
        return nickname == null || nickname.isEmpty() ? getSocial(sort, pageNumber) : getSocialSearchNickname(sort, pageNumber, nickname);
    }

    private SearchPublicDailyPlannerResponse getSocial(final String sort, final int pageNumber) {
        List<Social> socialList;
        int totalCount = socialRepository.countByDeleteTimeIsNull();
        int totalPage = totalCount / 6 + 1;
        if (totalCount == 0) {
            return SearchPublicDailyPlannerResponse.builder()
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
        return SearchPublicDailyPlannerResponse.builder()
                .pageNumber(pageNumber)
                .totalPage(totalPage)
                .sort(sort)
                .socialList(makeSearchSocialResponseList(socialList))
                .build();
    }

    private SearchPublicDailyPlannerResponse getSocialSearchNickname(final String sort, final int pageNumber, final String nickname) {
        List<Social> socialList;
        User owner = userRepository.findByNicknameAndPlannerAccessScope(nickname, PlannerAccessScope.PUBLIC);
        int totalCount = owner == null ? 0 : socialRepository.countByOwnerIdAndDeleteTimeIsNull(owner.getId());
        int totalPage = totalCount / 6 + 1;
        if (totalCount == 0) {
            return SearchPublicDailyPlannerResponse.builder()
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
        return SearchPublicDailyPlannerResponse.builder()
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