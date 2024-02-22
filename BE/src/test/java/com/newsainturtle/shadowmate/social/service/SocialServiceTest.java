package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.response.ShareSocialResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.social.dto.response.SearchSocialPlannerResponse;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.exception.SocialErrorResult;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialServiceTest extends DateCommonService {

    @InjectMocks
    private SocialServiceImpl socialService;

    @Mock
    private SocialRepository socialRepository;

    private final String date = "2023-09-25";
    private final String socialImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";
    private final User user = User.builder()
            .id(1L)
            .email("yntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    private final DailyPlanner dailyPlanner = DailyPlanner.builder()
            .id(1L)
            .dailyPlannerDay(date)
            .user(user)
            .build();
    private final Social social = Social.builder()
            .id(1L)
            .dailyPlanner(dailyPlanner)
            .socialImage(socialImage)
            .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
            .ownerId(user.getId())
            .build();

    @Test
    void 실패_소셜조회공통_정렬입력값이잘못됨() {
        // given
        final String sort = "late";
        final int pageNumber = 3;

        // when
        final SocialException result = assertThrows(SocialException.class, () -> socialService.getSocial(sort, pageNumber, "", ""));

        // then
        assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.BAD_REQUEST_SORT);
    }

    @Nested
    class 일반_소셜조회 {
        final String sort = "latest";
        final int pageNumber = 3;

        @Test
        void 실패_잘못된날짜형식() {
            // given

            // when
            final SocialException result = assertThrows(SocialException.class, () -> socialService.getSocial(sort, pageNumber, "2023.12.25", "2023.12.26"));

            // then
            assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_잘못된날짜기간() {
            // given

            // when
            final SocialException result = assertThrows(SocialException.class, () -> socialService.getSocial(sort, pageNumber, "2023-12-25", "2023-12-24"));

            // then
            assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.INVALID_DATE_PERIOD);
        }

        @Test
        void 성공_페이지넘버초과() {
            // given
            doReturn(3).when(socialRepository).countByDeleteTimeIsNull();
            doReturn(new ArrayList<>()).when(socialRepository).findAllByDeleteTimeIsNullSortLatest(any(Pageable.class));

            // when
            final SearchSocialPlannerResponse searchPublicDailyPlannerResponse = socialService.getSocial(sort, pageNumber, "", "");

            // then
            assertThat(searchPublicDailyPlannerResponse.getSocialList()).isEmpty();
        }

        @Test
        void 성공_최신순() {
            // given
            final int pageNumber = 1;
            final List<Social> socialList = new ArrayList<>();
            socialList.add(social);
            socialList.add(social);

            doReturn(2).when(socialRepository).countByDeleteTimeIsNull();
            doReturn(socialList).when(socialRepository).findAllByDeleteTimeIsNullSortLatest(any(Pageable.class));

            // when
            final SearchSocialPlannerResponse result = socialService.getSocial(sort, pageNumber, "", "");

            // then
            assertThat(result.getSocialList()).hasSize(2);
            assertThat(result.getTotalPage()).isEqualTo(1L);
            assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        }

        @Test
        void 성공_인기순() {
            // given
            final String sort = "popularity";
            final int pageNumber = 1;
            final List<Social> socialList = new ArrayList<>();
            socialList.add(social);
            socialList.add(social);

            doReturn(2).when(socialRepository).countByDeleteTimeIsNull();
            doReturn(socialList).when(socialRepository).findAllByDeleteTimeIsNullSortPopularity(any(Pageable.class));

            // when
            final SearchSocialPlannerResponse result = socialService.getSocial(sort, pageNumber, "", "");

            // then
            assertThat(result.getSocialList()).hasSize(2);
            assertThat(result.getTotalPage()).isEqualTo(1L);
            assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        }

        @Test
        void 성공_최신순_기간() {
            // given
            final String sort = "latest";
            final int pageNumber = 1;
            final List<Social> socialList = new ArrayList<>();
            socialList.add(social);
            socialList.add(social);

            doReturn(2).when(socialRepository).countByDeleteTimeIsNullAndPeriod(any(String.class), any(String.class));
            doReturn(socialList).when(socialRepository).findAllByDeleteTimeIsNullAndPeriodSortLatest(any(String.class), any(String.class), any(Pageable.class));

            // when
            final SearchSocialPlannerResponse result = socialService.getSocial(sort, pageNumber, "2023-12-25", "2023-12-26");

            // then
            assertThat(result.getSocialList()).hasSize(2);
            assertThat(result.getTotalPage()).isEqualTo(1L);
            assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        }

        @Test
        void 성공_인기순_기간() {
            // given
            final String sort = "popularity";
            final int pageNumber = 1;
            final List<Social> socialList = new ArrayList<>();
            socialList.add(social);
            socialList.add(social);

            doReturn(2).when(socialRepository).countByDeleteTimeIsNullAndPeriod(any(String.class), any(String.class));
            doReturn(socialList).when(socialRepository).findAllByDeleteTimeIsNullAndPeriodSortPopularity(any(String.class), any(String.class), any(Pageable.class));

            // when
            final SearchSocialPlannerResponse result = socialService.getSocial(sort, pageNumber, "2023-12-25", "2023-12-26");

            // then
            assertThat(result.getSocialList()).hasSize(2);
            assertThat(result.getTotalPage()).isEqualTo(1L);
            assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        }
    }

    @Nested
    class 닉네임검색_소셜조회 {

        @Test
        void 실패_잘못된날짜형식() {
            // given
            final String sort = "late";
            final int pageNumber = 3;

            // when
            final SocialException result = assertThrows(SocialException.class, () -> socialService.getSocial(sort, pageNumber, user, "2023.12.25", "2023.12.26"));

            // then
            assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.BAD_REQUEST_SORT);
        }

        @Test
        void 실패_잘못된날짜기간() {
            // given
            final String sort = "late";
            final int pageNumber = 3;

            // when
            final SocialException result = assertThrows(SocialException.class, () -> socialService.getSocial(sort, pageNumber, user, "2023-12-25", "2023-12-24"));

            // then
            assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.BAD_REQUEST_SORT);
        }

        @Test
        void 성공_없음() {
            // given
            final String sort = "latest";
            final int pageNumber = 1;

            doReturn(1).when(socialRepository).countByOwnerIdAndDeleteTimeIsNull(any(Long.class));
            doReturn(new ArrayList<>()).when(socialRepository).findAllByOwnerIdAndDeleteTimeIsNullSortLatest(any(Long.class), any(Pageable.class));

            // when
            final SearchSocialPlannerResponse searchPublicDailyPlannerResponse = socialService.getSocial(sort, pageNumber, user, "", "");

            // then
            assertThat(searchPublicDailyPlannerResponse.getSocialList()).isEmpty();
        }

        @Test
        void 성공_페이지넘버초과() {
            // given
            final String sort = "latest";
            final int pageNumber = 3;
            final int count = 3;

            doReturn(count).when(socialRepository).countByOwnerIdAndDeleteTimeIsNull(any(Long.class));
            doReturn(new ArrayList<>()).when(socialRepository).findAllByOwnerIdAndDeleteTimeIsNullSortLatest(any(Long.class), any(Pageable.class));

            // when
            final SearchSocialPlannerResponse searchPublicDailyPlannerResponse = socialService.getSocial(sort, pageNumber, user, "", "");

            // then
            assertThat(searchPublicDailyPlannerResponse.getSocialList()).isEmpty();
        }

        @Test
        void 성공_최신순() {
            // given
            final String sort = "latest";
            final int pageNumber = 1;
            final int count = 2;
            final List<Social> socialList = new ArrayList<>();
            socialList.add(social);
            socialList.add(social);

            doReturn(count).when(socialRepository).countByOwnerIdAndDeleteTimeIsNull(any(Long.class));
            doReturn(socialList).when(socialRepository).findAllByOwnerIdAndDeleteTimeIsNullSortLatest(any(Long.class), any(Pageable.class));

            // when
            final SearchSocialPlannerResponse result = socialService.getSocial(sort, pageNumber, user, "", "");

            // then
            assertThat(result.getSocialList()).hasSize(count);
            assertThat(result.getTotalPage()).isEqualTo(1L);
            assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        }

        @Test
        void 성공_인기순() {
            // given
            final String sort = "popularity";
            final int pageNumber = 1;
            final int count = 2;
            final List<Social> socialList = new ArrayList<>();
            socialList.add(social);
            socialList.add(social);

            doReturn(count).when(socialRepository).countByOwnerIdAndDeleteTimeIsNull(any(Long.class));
            doReturn(socialList).when(socialRepository).findAllByOwnerIdAndDeleteTimeIsNullSortPopularity(any(Long.class), any(Pageable.class));

            // when
            final SearchSocialPlannerResponse result = socialService.getSocial(sort, pageNumber, user, "", "");

            // then
            assertThat(result.getSocialList()).hasSize(count);
            assertThat(result.getTotalPage()).isEqualTo(1L);
            assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        }

        @Test
        void 성공_최신순_기간() {
            // given
            final String sort = "latest";
            final int pageNumber = 1;
            final int count = 2;
            final List<Social> socialList = new ArrayList<>();
            socialList.add(social);
            socialList.add(social);

            doReturn(count).when(socialRepository).countByOwnerIdAndDeleteTimeIsNullAndPeriod(any(Long.class), any(String.class), any(String.class));
            doReturn(socialList).when(socialRepository).findAllByOwnerIdAndDeleteTimeIsNullAndPeriodSortLatest(any(Long.class), any(String.class), any(String.class), any(Pageable.class));

            // when
            final SearchSocialPlannerResponse result = socialService.getSocial(sort, pageNumber, user, "2023-12-25", "2023-12-26");

            // then
            assertThat(result.getSocialList()).hasSize(count);
            assertThat(result.getTotalPage()).isEqualTo(1L);
            assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        }

        @Test
        void 성공_인기순_기간() {
            // given
            final String sort = "popularity";
            final int pageNumber = 1;
            final int count = 2;
            final List<Social> socialList = new ArrayList<>();
            socialList.add(social);
            socialList.add(social);

            doReturn(count).when(socialRepository).countByOwnerIdAndDeleteTimeIsNullAndPeriod(any(Long.class), any(String.class), any(String.class));
            doReturn(socialList).when(socialRepository).findAllByOwnerIdAndDeleteTimeIsNullAndPeriodSortPopularity(any(Long.class), any(String.class), any(String.class), any(Pageable.class));

            // when
            final SearchSocialPlannerResponse result = socialService.getSocial(sort, pageNumber, user, "2023-12-25", "2023-12-26");

            // then
            assertThat(result.getSocialList()).hasSize(count);
            assertThat(result.getTotalPage()).isEqualTo(1L);
            assertThat(result.getPageNumber()).isEqualTo(pageNumber);
        }
    }

    @Nested
    class 소셜공유 {

        @Test
        void 실패_이미공유한_소셜재공유() {
            //given
            doReturn(social).when(socialRepository).findByDailyPlanner(any(DailyPlanner.class));

            //when
            final SocialException result = assertThrows(SocialException.class, () -> socialService.shareSocial(user, dailyPlanner, socialImage));

            //then
            assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.ALREADY_SHARED_SOCIAL);
        }

        @Test
        void 성공_소셜공유() {
            //given
            doReturn(null).when(socialRepository).findByDailyPlanner(any(DailyPlanner.class));
            doReturn(social).when(socialRepository).save(any(Social.class));

            //when
            ShareSocialResponse shareSocialResponse = socialService.shareSocial(user, dailyPlanner, socialImage);

            //then
            assertThat(shareSocialResponse).isNotNull();
            assertThat(shareSocialResponse.getSocialId()).isEqualTo(1L);
        }
    }

    @Test
    void 성공_소셜_DeleteTime_전체수정() {
        // given

        // when
        socialService.updateDeleteTimeAll(LocalDateTime.now(), new ArrayList<>());

        // then
        verify(socialRepository, times(1)).updateDeleteTimeAll(any(LocalDateTime.class), any(List.class));
    }

    @Test
    void 성공_공유플래너삭제() {
        // given

        // when
        socialService.deleteSocial(user, social.getId());

        // then
        verify(socialRepository, times(1)).deleteByIdAndOwnerId(any(Long.class), any(Long.class));
    }
}
