package com.newsainturtle.shadowmate.kh.social;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.social.entity.Social;
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
    final Social social = Social.builder()
            .id(9999L)
            .dailyPlanner(dailyPlanner)
            .socialImage(Image)
            .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
            .ownerId(user1.getId())
            .build();
    final Long socialId = social.getId();

    @Test
    void 성공_공유플래너삭제() {
        // given

        // when
        socialService.deleteSocial(user1, socialId);

        // then
        verify(socialRepository, times(1)).deleteByIdAndOwnerId(any(Long.class), any(Long.class));

    }
}
