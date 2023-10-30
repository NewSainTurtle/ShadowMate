package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.planner.repository.DailyPlannerLikeRepository;
import com.newsainturtle.shadowmate.social.dto.SearchPublicDailyPlannerResponse;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.newsainturtle.shadowmate.social.exception.SocialErrorResult.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialServiceImpl implements SocialService {

    private final SocialRepository socialRepository;

    private final DailyPlannerLikeRepository dailyPlannerLikeRepository;

    class SocialLike implements Comparable<SocialLike> {
        Social social;
        long cnt;

        public SocialLike(Social social, long cnt) {
            this.social = social;
            this.cnt = cnt;
        }

        @Override
        public int compareTo(SocialLike o) {
            return (int)(o.cnt - this.cnt);
        }
    }

    @Override
    public SearchPublicDailyPlannerResponse searchPublicDailyPlanner(final String sort, final long pageNumber) {
        List<Social> socialList = socialRepository.findAllByDeleteTime();
        int totalPage = socialList.size()/6;
        if(0 < socialList.size() % 6) totalPage++;
        if(totalPage < pageNumber) throw new SocialException(NOT_FOUND_PAGE_NUMBER);
        if(sort.equals("latest")) {
            if(totalPage == 1) {
                return SearchPublicDailyPlannerResponse.builder()
                        .pageNumber(pageNumber)
                        .totalPage(totalPage)
                        .sort(sort)
                        .socialList(socialList)
                        .build();
            }
            else {
                List<Social> newList = new ArrayList<>();
                for(int i=(int)(pageNumber-1)*6, cnt=0; i<socialList.size(); i++, cnt++) {
                    if(cnt==6) break;
                    newList.add(socialList.get(i));
                }
                return SearchPublicDailyPlannerResponse.builder()
                        .pageNumber(pageNumber)
                        .totalPage(totalPage)
                        .sort(sort)
                        .socialList(newList)
                        .build();
            }
        }
        else if(sort.equals("popularity")) {
            List<SocialLike> socialLikeList = socialList.stream()
                    .map(social -> new SocialLike(social,dailyPlannerLikeRepository.countByDailyPlanner(social.getDailyPlanner())))
                    .collect(Collectors.toList());
            Collections.sort(socialLikeList);
            if(totalPage == 1) {
                return SearchPublicDailyPlannerResponse.builder()
                        .pageNumber(pageNumber)
                        .totalPage(totalPage)
                        .sort(sort)
                        .socialList(socialLikeList.stream()
                                .map(socialLike -> socialLike.social)
                                .collect(Collectors.toList()))
                        .build();
            }
            else {
                List<Social> newList = new ArrayList<>();
                for(int i=(int)(pageNumber-1)*6, cnt=0; i<socialList.size(); i++, cnt++) {
                    if(cnt==6) break;
                    newList.add(socialLikeList.get(i).social);
                }
                return SearchPublicDailyPlannerResponse.builder()
                        .pageNumber(pageNumber)
                        .totalPage(totalPage)
                        .sort(sort)
                        .socialList(newList)
                        .build();
            }
        }
        else {
            throw new SocialException(BAD_REQUEST_SORT);
        }
    }
}
