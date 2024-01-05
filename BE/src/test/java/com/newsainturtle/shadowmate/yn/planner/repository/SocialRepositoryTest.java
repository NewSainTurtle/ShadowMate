package com.newsainturtle.shadowmate.yn.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SocialRepositoryTest {

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    private User user;
    private DailyPlanner dailyPlanner;
    private final String socialImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay("2023-09-25")
                .user(user)
                .build());
    }

    @Test
    void 소셜공유() {
        //given
        final Social social = Social.builder()
                .dailyPlanner(dailyPlanner)
                .socialImage(socialImage)
                .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                .ownerId(user.getId())
                .build();
        //when
        final Social saveSocial = socialRepository.save(social);

        //then
        assertThat(saveSocial).isNotNull();
        assertThat(saveSocial.getSocialImage()).isNotNull().isEqualTo(socialImage);
        assertThat(saveSocial.getDailyPlanner()).isEqualTo(dailyPlanner);
        assertThat(saveSocial.getDailyPlannerDay()).isEqualTo(dailyPlanner.getDailyPlannerDay());
        assertThat(saveSocial.getOwnerId()).isEqualTo(dailyPlanner.getUser().getId());
    }


    @Nested
    class 소셜공유확인 {

        @Test
        void 소셜공유안함() {
            //given

            //when
            final Social findSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner);

            //then
            assertThat(findSocial).isNull();
        }

        @Test
        void 소셜공유함() {
            //given
            socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .ownerId(user.getId())
                    .build());

            //when
            final Social findSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner);

            //then
            assertThat(findSocial).isNotNull();
            assertThat(findSocial.getSocialImage()).isNotNull().isEqualTo(socialImage);
            assertThat(findSocial.getDailyPlanner()).isEqualTo(dailyPlanner);
            assertThat(findSocial.getDailyPlannerDay()).isEqualTo(dailyPlanner.getDailyPlannerDay());
            assertThat(findSocial.getOwnerId()).isEqualTo(dailyPlanner.getUser().getId());
        }

        @Test
        void 소셜공유함_비공개상태() {
            //given
            socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .ownerId(user.getId())
                    .deleteTime(LocalDateTime.now())
                    .build());

            //when
            final Social findSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner);

            //then
            assertThat(findSocial).isNull();
        }
    }
}
