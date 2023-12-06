package com.newsainturtle.shadowmate.kh.social;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SocialRepositoryTest{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    final User user1 = User.builder()
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .profileImage("TestprofileImage")
            .withdrawal(false)
            .build();

    private LocalDate stringToLocalDate(final String dateStr) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateStr, formatter);
    }

    @BeforeEach
    public void init() {
        userRepository.save(user1);
    }

    @Test
    void 성공_공개된플래너조회() {
        // given
        final String date = "2023-10-30";
        final String Image = "testImage";
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(stringToLocalDate(date))
                .user(user1)
                .build();
        final Social social = Social.builder()
                .dailyPlanner(dailyPlanner)
                .socialImage(Image)
                .build();
        dailyPlannerRepository.save(dailyPlanner);
        socialRepository.save(social);

        // when
        final List<Social> result = socialRepository.findAllByDeleteTime();

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void 성공_공개된플래너_닉네임검색_공유하지않은경우() {
        // given
        final String date = "2023-10-30";
        final String Image = "testImage";
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(stringToLocalDate(date))
                .user(user1)
                .build();

        dailyPlannerRepository.save(dailyPlanner);

        // when
        User user = userRepository.findByNicknameAndPlannerAccessScope(user1.getNickname(), PlannerAccessScope.PUBLIC);
        List<Social> socialList = socialRepository.findAllByDailyPlannerAndSocial(user.getId());

        // then
        assertThat(socialList).isEmpty();

    }

    @Test
    void 성공_공개된플래너_닉네임검색() {
        // given
        final String date = "2023-10-30";
        final String Image = "testImage";
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(stringToLocalDate(date))
                .user(user1)
                .build();
        final Social social = Social.builder()
                .dailyPlanner(dailyPlanner)
                .socialImage(Image)
                .build();
        dailyPlannerRepository.save(dailyPlanner);
        socialRepository.save(social);

        // when
        User user = userRepository.findByNicknameAndPlannerAccessScope(user1.getNickname(), PlannerAccessScope.PUBLIC);
        List<Social> socialList = socialRepository.findAllByDailyPlannerAndSocial(user.getId());

        // then
        assertThat(socialList.get(0).getSocialImage()).isEqualTo(Image);

    }

    @Test
    void 공유한플래너_삭제() {
        // given
        final String date = "2023-10-30";
        final String Image = "testImage";
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(stringToLocalDate(date))
                .user(user1)
                .build();
        final Social social = Social.builder()
                .dailyPlanner(dailyPlanner)
                .socialImage(Image)
                .build();
        dailyPlannerRepository.save(dailyPlanner);

        // when
        final Social result1 = socialRepository.save(social);
        socialRepository.delete(social);
        final Social result2 = socialRepository.findById(result1.getId()).orElse(null);

        // then
        assertThat(result2).isNull();
    }
}
