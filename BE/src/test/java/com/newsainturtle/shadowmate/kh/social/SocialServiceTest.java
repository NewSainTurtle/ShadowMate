package com.newsainturtle.shadowmate.kh.social;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerLikeRepository;
import com.newsainturtle.shadowmate.social.dto.SearchNicknamePublicDailyPlannerRequest;
import com.newsainturtle.shadowmate.social.dto.SearchPublicDailyPlannerResponse;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.exception.SocialErrorResult;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.social.service.SocialServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SocialServiceTest {

    @InjectMocks
    private SocialServiceImpl socialService;

    @Mock
    private SocialRepository socialRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DailyPlannerLikeRepository dailyPlannerLikeRepository;
    final String date = "2023-10-30";
    final String Image = "testImage";
    final User user1 = User.builder()
            .id(Long.MAX_VALUE)
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    final DailyPlanner dailyPlanner = DailyPlanner.builder()
            .dailyPlannerDay(Date.valueOf(date))
            .user(user1)
            .build();
    final Social social = Social.builder()
            .id(Long.MAX_VALUE)
            .dailyPlanner(dailyPlanner)
            .socialImage(Image)
            .build();

    @Test
    void 실패_공개된플래너조회_페이지넘버초과() {
        // given
        final String sort = "latest";
        final Long pageNumber = 3L;
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        doReturn(socialList).when(socialRepository).findAllByDeleteTime();

        // when
        final SocialException result = assertThrows(SocialException.class, () -> socialService.searchPublicDailyPlanner(sort, pageNumber));

        // then
        assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.NOT_FOUND_PAGE_NUMBER);
    }

    @Test
    void 실패_공개된플래너조회_정렬정보잘못됨() {
        // given
        final String sort = "late";
        final Long pageNumber = 1L;
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        doReturn(socialList).when(socialRepository).findAllByDeleteTime();

        // when
        final SocialException result = assertThrows(SocialException.class, () -> socialService.searchPublicDailyPlanner(sort, pageNumber));

        // then
        assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.BAD_REQUEST_SORT);
    }

    @Test
    void 성공_공개된플래너조회_6개이하_기본정렬() {
        // given
        final String sort = "latest";
        final Long pageNumber = 1L;
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        doReturn(socialList).when(socialRepository).findAllByDeleteTime();

        // when
        SearchPublicDailyPlannerResponse result = socialService.searchPublicDailyPlanner(sort, pageNumber);

        // then
        assertThat(result.getSocialList()).hasSize(1);
        assertThat(result.getTotalPage()).isEqualTo(1L);
        assertThat(result.getPageNumber()).isEqualTo(pageNumber);
    }

    @Test
    void 성공_공개된플래너조회_6개초과_기본정렬() {
        // given
        final String sort = "latest";
        final Long pageNumber = 2L;
        List<Social> socialList = new ArrayList<>();
        int cnt = 7;
        while(cnt-->0) {
            socialList.add(social);
        }
        doReturn(socialList).when(socialRepository).findAllByDeleteTime();

        // when
        SearchPublicDailyPlannerResponse result = socialService.searchPublicDailyPlanner(sort, pageNumber);

        // then
        assertThat(result.getSocialList()).hasSize(1);
        assertThat(result.getTotalPage()).isEqualTo(2L);
        assertThat(result.getPageNumber()).isEqualTo(pageNumber);
    }

    @Test
    void 성공_공개된플래너조회_6개이하_인기정렬() {
        // given
        final String sort = "popularity";
        final Long pageNumber = 1L;
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        final String date2 = "2023-09-30";
        final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf(date2))
                .user(user1)
                .build();
        final Social social2 = Social.builder()
                .id(9999L)
                .dailyPlanner(dailyPlanner2)
                .socialImage(Image)
                .build();
        socialList.add(social2);
        doReturn(socialList).when(socialRepository).findAllByDeleteTime();
        doReturn(10L).when(dailyPlannerLikeRepository).countByDailyPlanner(social.getDailyPlanner());
        doReturn(20L).when(dailyPlannerLikeRepository).countByDailyPlanner(social2.getDailyPlanner());

        // when
        SearchPublicDailyPlannerResponse result = socialService.searchPublicDailyPlanner(sort, pageNumber);

        // then
        assertThat(result.getSocialList()).hasSize(2);
        assertThat(result.getTotalPage()).isEqualTo(1L);
        assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        assertThat(result.getSocialList().get(0).getDailyPlannerDay()).isEqualTo(Date.valueOf(date2));
        assertThat(result.getSocialList().get(1).getDailyPlannerDay()).isEqualTo(Date.valueOf(date));
    }

    @Test
    void 성공_공개된플래너조회_6개초과_인기정렬() {
        // given
        final String sort = "popularity";
        final Long pageNumber = 2L;
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        final String date2 = "2023-09-30";
        final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf(date2))
                .user(user1)
                .build();
        final Social social2 = Social.builder()
                .id(9999L)
                .dailyPlanner(dailyPlanner2)
                .socialImage(Image)
                .build();
        int cnt = 7;
        while(cnt-->0) {
            socialList.add(social2);
        }
        doReturn(socialList).when(socialRepository).findAllByDeleteTime();
        doReturn(10L).when(dailyPlannerLikeRepository).countByDailyPlanner(social.getDailyPlanner());
        doReturn(20L).when(dailyPlannerLikeRepository).countByDailyPlanner(social2.getDailyPlanner());

        // when
        SearchPublicDailyPlannerResponse result = socialService.searchPublicDailyPlanner(sort, pageNumber);

        // then
        assertThat(result.getSocialList()).hasSize(2);
        assertThat(result.getTotalPage()).isEqualTo(2L);
        assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        assertThat(result.getSocialList().get(0).getDailyPlannerDay()).isEqualTo(Date.valueOf(date2));
        assertThat(result.getSocialList().get(1).getDailyPlannerDay()).isEqualTo(Date.valueOf(date));
    }

    @Test
    void 실패_공개된플래너_닉네임검색_페이지넘버초과() {
        // given
        final String sort = "latest";
        final Long pageNumber = 3L;
        final SearchNicknamePublicDailyPlannerRequest request = SearchNicknamePublicDailyPlannerRequest.builder()
                .nickname(user1.getNickname())
                .sort(sort)
                .pageNumber(pageNumber)
                .build();
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        doReturn(user1).when(userRepository).findByNicknameAndPlannerAccessScope(user1.getNickname(), PlannerAccessScope.PUBLIC);
        doReturn(socialList).when(socialRepository).findAllByDailyPlannerAndSocial(user1.getId());

        // when
        final SocialException result = assertThrows(SocialException.class, () -> socialService.searchNicknamePublicDailyPlanner(request));

        // then
        assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.NOT_FOUND_PAGE_NUMBER);
    }

    @Test
    void 실패_공개된플래너_닉네임검색_정렬정보잘못됨() {
        // given
        final String sort = "late";
        final Long pageNumber = 1L;
        final SearchNicknamePublicDailyPlannerRequest request = SearchNicknamePublicDailyPlannerRequest.builder()
                .nickname(user1.getNickname())
                .sort(sort)
                .pageNumber(pageNumber)
                .build();
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        doReturn(user1).when(userRepository).findByNicknameAndPlannerAccessScope(user1.getNickname(), PlannerAccessScope.PUBLIC);
        doReturn(socialList).when(socialRepository).findAllByDailyPlannerAndSocial(user1.getId());

        // when
        final SocialException result = assertThrows(SocialException.class, () -> socialService.searchNicknamePublicDailyPlanner(request));

        // then
        assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.BAD_REQUEST_SORT);
    }

    @Test
    void 성공_공개된플래너_닉네임없음() {
        // given
        final String sort = "latest";
        final Long pageNumber = 1L;
        final SearchNicknamePublicDailyPlannerRequest request = SearchNicknamePublicDailyPlannerRequest.builder()
                .nickname(user1.getNickname())
                .sort(sort)
                .pageNumber(pageNumber)
                .build();
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        doReturn(null).when(userRepository).findByNicknameAndPlannerAccessScope(user1.getNickname(), PlannerAccessScope.PUBLIC);

        // when
        SearchPublicDailyPlannerResponse result = socialService.searchNicknamePublicDailyPlanner(request);

        // then
        assertThat(result.getSocialList()).isEmpty();
    }

    @Test
    void 성공_공개된플래너_공유된플래너없음() {
        // given
        final String sort = "latest";
        final Long pageNumber = 1L;
        final SearchNicknamePublicDailyPlannerRequest request = SearchNicknamePublicDailyPlannerRequest.builder()
                .nickname(user1.getNickname())
                .sort(sort)
                .pageNumber(pageNumber)
                .build();
        List<Social> socialList = new ArrayList<>();
        doReturn(user1).when(userRepository).findByNicknameAndPlannerAccessScope(user1.getNickname(), PlannerAccessScope.PUBLIC);
        doReturn(socialList).when(socialRepository).findAllByDailyPlannerAndSocial(user1.getId());

        // when
        SearchPublicDailyPlannerResponse result = socialService.searchNicknamePublicDailyPlanner(request);

        // then
        assertThat(result.getSocialList()).isEmpty();
    }

    @Test
    void 성공_공개된플래너_닉네임검색_6개이하_기본정렬() {
        // given
        final String sort = "latest";
        final Long pageNumber = 1L;
        final SearchNicknamePublicDailyPlannerRequest request = SearchNicknamePublicDailyPlannerRequest.builder()
                .nickname(user1.getNickname())
                .sort(sort)
                .pageNumber(pageNumber)
                .build();
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        doReturn(user1).when(userRepository).findByNicknameAndPlannerAccessScope(user1.getNickname(), PlannerAccessScope.PUBLIC);
        doReturn(socialList).when(socialRepository).findAllByDailyPlannerAndSocial(user1.getId());

        // when
        SearchPublicDailyPlannerResponse result = socialService.searchNicknamePublicDailyPlanner(request);

        // then
        assertThat(result.getSocialList().size()).isEqualTo(1);
        assertThat(result.getTotalPage()).isEqualTo(1L);
        assertThat(result.getPageNumber()).isEqualTo(pageNumber);
    }

    @Test
    void 성공_공개된플래너_닉네임검색_6개초과_기본정렬() {
        // given
        final String sort = "latest";
        final Long pageNumber = 2L;
        final SearchNicknamePublicDailyPlannerRequest request = SearchNicknamePublicDailyPlannerRequest.builder()
                .nickname(user1.getNickname())
                .sort(sort)
                .pageNumber(pageNumber)
                .build();
        List<Social> socialList = new ArrayList<>();
        int cnt = 7;
        while(cnt-->0) {
            socialList.add(social);
        }
        doReturn(user1).when(userRepository).findByNicknameAndPlannerAccessScope(user1.getNickname(), PlannerAccessScope.PUBLIC);
        doReturn(socialList).when(socialRepository).findAllByDailyPlannerAndSocial(user1.getId());

        // when
        SearchPublicDailyPlannerResponse result = socialService.searchNicknamePublicDailyPlanner(request);

        // then
        assertThat(result.getSocialList().size()).isEqualTo(1);
        assertThat(result.getTotalPage()).isEqualTo(2L);
        assertThat(result.getPageNumber()).isEqualTo(pageNumber);
    }

    @Test
    void 성공_공개된플래너_닉네임검색_6개이하_인기정렬() {
        // given
        final String sort = "popularity";
        final Long pageNumber = 1L;
        final SearchNicknamePublicDailyPlannerRequest request = SearchNicknamePublicDailyPlannerRequest.builder()
                .nickname(user1.getNickname())
                .sort(sort)
                .pageNumber(pageNumber)
                .build();
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        final String date2 = "2023-09-30";
        final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf(date2))
                .user(user1)
                .build();
        final Social social2 = Social.builder()
                .id(Long.MAX_VALUE-1L)
                .dailyPlanner(dailyPlanner2)
                .socialImage(Image)
                .build();
        socialList.add(social2);
        doReturn(user1).when(userRepository).findByNicknameAndPlannerAccessScope(user1.getNickname(), PlannerAccessScope.PUBLIC);
        doReturn(socialList).when(socialRepository).findAllByDailyPlannerAndSocial(user1.getId());
        doReturn(10L).when(dailyPlannerLikeRepository).countByDailyPlanner(social.getDailyPlanner());
        doReturn(20L).when(dailyPlannerLikeRepository).countByDailyPlanner(social2.getDailyPlanner());

        // when
        SearchPublicDailyPlannerResponse result = socialService.searchNicknamePublicDailyPlanner(request);

        // then
        assertThat(result.getSocialList().size()).isEqualTo(2);
        assertThat(result.getTotalPage()).isEqualTo(1L);
        assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        assertThat(result.getSocialList().get(0).getDailyPlannerDay()).isEqualTo(Date.valueOf(date2));
        assertThat(result.getSocialList().get(1).getDailyPlannerDay()).isEqualTo(Date.valueOf(date));
    }

    @Test
    void 성공_공개된플래너_닉네임검색_6개초과_인기정렬() {
        // given
        final String sort = "popularity";
        final Long pageNumber = 2L;
        final SearchNicknamePublicDailyPlannerRequest request = SearchNicknamePublicDailyPlannerRequest.builder()
                .nickname(user1.getNickname())
                .sort(sort)
                .pageNumber(pageNumber)
                .build();
        List<Social> socialList = new ArrayList<>();
        socialList.add(social);
        final String date2 = "2023-09-30";
        final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf(date2))
                .user(user1)
                .build();
        final Social social2 = Social.builder()
                .id(Long.MAX_VALUE-1L)
                .dailyPlanner(dailyPlanner2)
                .socialImage(Image)
                .build();
        int cnt = 7;
        while(cnt-->0) {
            socialList.add(social2);
        }
        doReturn(user1).when(userRepository).findByNicknameAndPlannerAccessScope(user1.getNickname(), PlannerAccessScope.PUBLIC);
        doReturn(socialList).when(socialRepository).findAllByDailyPlannerAndSocial(user1.getId());
        doReturn(10L).when(dailyPlannerLikeRepository).countByDailyPlanner(social.getDailyPlanner());
        doReturn(20L).when(dailyPlannerLikeRepository).countByDailyPlanner(social2.getDailyPlanner());

        // when
        SearchPublicDailyPlannerResponse result = socialService.searchNicknamePublicDailyPlanner(request);

        // then
        assertThat(result.getSocialList().size()).isEqualTo(2);
        assertThat(result.getTotalPage()).isEqualTo(2L);
        assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        assertThat(result.getSocialList().get(0).getDailyPlannerDay()).isEqualTo(Date.valueOf(date2));
        assertThat(result.getSocialList().get(1).getDailyPlannerDay()).isEqualTo(Date.valueOf(date));
    }
}
