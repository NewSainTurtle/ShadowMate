package com.newsainturtle.shadowmate.yn.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DailyPlannerRepositoryTest {

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private final String date = "2023-09-25";

    private LocalDate stringToLocalDate(final String dateStr) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateStr, formatter);
    }

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
    }

    @Test
    void 일일플래너등록() {
        //given
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(stringToLocalDate(date))
                .user(user)
                .build();

        //when
        final DailyPlanner saveDailyPlanner = dailyPlannerRepository.save(dailyPlanner);

        //then
        assertThat(saveDailyPlanner).isNotNull();
        assertThat(saveDailyPlanner.getDailyPlannerDay()).isEqualTo(stringToLocalDate(date));
        assertThat(saveDailyPlanner.getUser()).isEqualTo(user);
        assertThat(saveDailyPlanner.getRetrospection()).isNull();
        assertThat(saveDailyPlanner.getRetrospectionImage()).isNull();
        assertThat(saveDailyPlanner.getTodayGoal()).isNull();
        assertThat(saveDailyPlanner.getTomorrowGoal()).isNull();
    }

    @Nested
    class 일일플래너_USER로_조회 {
        @Test
        void 개별조회_일일플래너없음_Null() {
            //given

            //when
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));

            //then
            assertThat(findDailyPlanner).isNull();
        }

        @Test
        void 개별조회_일일플래너있음() {
            //given
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .dailyPlannerDay(stringToLocalDate(date))
                    .user(user)
                    .build();
            dailyPlannerRepository.save(dailyPlanner);

            //when
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(stringToLocalDate(date));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
        }

        @Test
        void 전체조회() {
            //given
            final DailyPlanner dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(stringToLocalDate(date))
                    .user(user)
                    .build());
            final DailyPlanner dailyPlanner2 = dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(stringToLocalDate("2023-09-26"))
                    .user(user)
                    .build());

            //when
            final List<DailyPlanner> dailyPlanners = dailyPlannerRepository.findAllByUser(user);

            //then
            assertThat(dailyPlanners).isNotNull().hasSize(2);
        }
    }

    @Nested
    class 일일플래너수정 {
        @Test
        void 오늘의다짐편집() {
            //given
            final String todayGoal = "오늘의 다짐!!!";
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(stringToLocalDate(date))
                    .user(user)
                    .build());
            //when
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal(todayGoal)
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .build();
            dailyPlannerRepository.save(changeDailyPlanner);
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(stringToLocalDate(date));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getTodayGoal()).isEqualTo(todayGoal);
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getCreateTime()).isNotEqualTo(findDailyPlanner.getUpdateTime());
        }

        @Test
        void 내일의다짐편집() {
            //given
            final String tomorrowGoal = "이제는 더이상 물러나 곳이 없다.";
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(stringToLocalDate(date))
                    .user(user)
                    .build());
            //when
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal(tomorrowGoal)
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .build();
            dailyPlannerRepository.save(changeDailyPlanner);
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(stringToLocalDate(date));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isEqualTo(tomorrowGoal);
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getCreateTime()).isNotEqualTo(findDailyPlanner.getUpdateTime());
        }

        @Test
        void 오늘의회고편집() {
            //given
            final String retrospection = "오늘 계획했던 일을 모두 끝냈다!!! 신남~~";
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(stringToLocalDate(date))
                    .user(user)
                    .build());
            //when
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .retrospection(retrospection)
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .build();
            dailyPlannerRepository.save(changeDailyPlanner);
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(stringToLocalDate(date));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getRetrospection()).isEqualTo(retrospection);
            assertThat(findDailyPlanner.getCreateTime()).isNotEqualTo(findDailyPlanner.getUpdateTime());
        }

        @Test
        void 오늘의회고사진업로드_null() {
            //given
            final String retrospectionImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(stringToLocalDate(date))
                    .user(user)
                    .retrospectionImage(retrospectionImage)
                    .build());
            //when
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(null)
                    .build();
            dailyPlannerRepository.save(changeDailyPlanner);
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(stringToLocalDate(date));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getCreateTime()).isNotEqualTo(findDailyPlanner.getUpdateTime());
        }

        @Test
        void 오늘의회고사진업로드() {
            //given
            final String retrospectionImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(stringToLocalDate(date))
                    .user(user)
                    .build());
            //when
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(retrospectionImage)
                    .build();
            dailyPlannerRepository.save(changeDailyPlanner);
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, stringToLocalDate(date));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(stringToLocalDate(date));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isEqualTo(retrospectionImage);
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getCreateTime()).isNotEqualTo(findDailyPlanner.getUpdateTime());
        }
    }
}
