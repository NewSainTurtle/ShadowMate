package com.newsainturtle.shadowmate.kh.social;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.exception.SocialErrorResult;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.social.service.SocialServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialServiceTest extends DateCommonService {

    @InjectMocks
    private SocialServiceImpl socialService;

    @Mock
    private SocialRepository socialRepository;

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
            .dailyPlannerDay(date)
            .user(user1)
            .build();

    @Test
    void 실패_공유플래너삭제_공유플래너없음() {
        // given
        final String date = "2023-10-30";
        final String Image = "testImage";
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(date)
                .user(user1)
                .build();
        final Social social = Social.builder()
                .id(9999L)
                .dailyPlanner(dailyPlanner)
                .socialImage(Image)
                .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                .ownerId(user1.getId())
                .build();
        final Long socialId = social.getId();

        // when
        final SocialException result = assertThrows(SocialException.class, () -> socialService.deleteSocial(socialId));

        // then
        assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.NOT_FOUND_SOCIAL);

    }

    @Test
    void 성공_공유플래너삭제() {
        // given
        final String date = "2023-10-30";
        final String Image = "testImage";
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(date)
                .user(user1)
                .build();
        final Social social = Social.builder()
                .id(9999L)
                .dailyPlanner(dailyPlanner)
                .socialImage(Image)
                .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                .ownerId(user1.getId())
                .build();
        doReturn(Optional.of(social)).when(socialRepository).findById(social.getId());

        // when
        socialService.deleteSocial(social.getId());

        // then
        verify(socialRepository, times(1)).delete(any());

    }
}
