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

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DailyPlannerRepositoryTest {

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void init() {
        user = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Test
    public void 일일플래너등록() {
        //given
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf("2023-09-25"))
                .user(user)
                .build();

        //when
        final DailyPlanner saveDailyPlanner = dailyPlannerRepository.save(dailyPlanner);

        //then
        assertThat(saveDailyPlanner).isNotNull();
        assertThat(saveDailyPlanner.getDailyPlannerDay()).isEqualTo(Date.valueOf("2023-09-25"));
        assertThat(saveDailyPlanner.getUser()).isEqualTo(user);
        assertThat(saveDailyPlanner.getRetrospection()).isNull();
        assertThat(saveDailyPlanner.getRetrospectionImage()).isNull();
        assertThat(saveDailyPlanner.getTodayGoal()).isNull();
        assertThat(saveDailyPlanner.getTomorrowGoal()).isNull();
    }

    @Nested
    class 일일플래너_USER로_조회 {
        @Test
        public void 일일플래너없음_Null() {
            //given

            //when
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));

            //then
            assertThat(findDailyPlanner).isNull();
        }

        @Test
        public void 일일플래너있음() {
            //given
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .dailyPlannerDay(Date.valueOf("2023-09-25"))
                    .user(user)
                    .build();
            dailyPlannerRepository.save(dailyPlanner);

            //when
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(Date.valueOf("2023-09-25"));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
        }
    }

    @Nested
    class 일일플래너수정 {
        @Test
        public void 오늘의다짐편집() {
            //given
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(Date.valueOf("2023-09-25"))
                    .user(user)
                    .build());
            //when
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal("오늘의 다짐!!!")
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .build();
            dailyPlannerRepository.save(changeDailyPlanner);
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(Date.valueOf("2023-09-25"));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getTodayGoal()).isEqualTo("오늘의 다짐!!!");
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getCreateTime()).isNotEqualTo(findDailyPlanner.getUpdateTime());
        }

        @Test
        public void 내일의다짐편집() {
            //given
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(Date.valueOf("2023-09-25"))
                    .user(user)
                    .build());
            //when
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal("이제는 더이상 물러나 곳이 없다.")
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .build();
            dailyPlannerRepository.save(changeDailyPlanner);
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(Date.valueOf("2023-09-25"));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isEqualTo("이제는 더이상 물러나 곳이 없다.");
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getCreateTime()).isNotEqualTo(findDailyPlanner.getUpdateTime());
        }

        @Test
        public void 오늘의회고편집() {
            //given
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(Date.valueOf("2023-09-25"))
                    .user(user)
                    .build());
            //when
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .retrospection("오늘 계획했던 일을 모두 끝냈다!!! 신남~~")
                    .retrospectionImage(dailyPlanner.getRetrospectionImage())
                    .build();
            dailyPlannerRepository.save(changeDailyPlanner);
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(Date.valueOf("2023-09-25"));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getRetrospection()).isEqualTo("오늘 계획했던 일을 모두 끝냈다!!! 신남~~");
            assertThat(findDailyPlanner.getCreateTime()).isNotEqualTo(findDailyPlanner.getUpdateTime());
        }

        @Test
        public void 오늘의회고사진업로드_null() {
            //given
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(Date.valueOf("2023-09-25"))
                    .user(user)
                    .retrospectionImage("https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg")
                    .build());
            //when
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));
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
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(Date.valueOf("2023-09-25"));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getCreateTime()).isNotEqualTo(findDailyPlanner.getUpdateTime());
        }

        @Test
        public void 오늘의회고사진업로드() {
            //given
            dailyPlannerRepository.save(DailyPlanner.builder()
                    .dailyPlannerDay(Date.valueOf("2023-09-25"))
                    .user(user)
                    .build());
            //when
            final DailyPlanner dailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));
            final DailyPlanner changeDailyPlanner = DailyPlanner.builder()
                    .id(dailyPlanner.getId())
                    .createTime(dailyPlanner.getCreateTime())
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .user(dailyPlanner.getUser())
                    .todayGoal(dailyPlanner.getTodayGoal())
                    .tomorrowGoal(dailyPlanner.getTomorrowGoal())
                    .retrospection(dailyPlanner.getRetrospection())
                    .retrospectionImage("https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg")
                    .build();
            dailyPlannerRepository.save(changeDailyPlanner);
            final DailyPlanner findDailyPlanner = dailyPlannerRepository.findByUserAndDailyPlannerDay(user, Date.valueOf("2023-09-25"));

            //then
            assertThat(findDailyPlanner).isNotNull();
            assertThat(findDailyPlanner.getDailyPlannerDay()).isEqualTo(Date.valueOf("2023-09-25"));
            assertThat(findDailyPlanner.getUser()).isEqualTo(user);
            assertThat(findDailyPlanner.getTodayGoal()).isNull();
            assertThat(findDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(findDailyPlanner.getRetrospectionImage()).isEqualTo("https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg");
            assertThat(findDailyPlanner.getRetrospection()).isNull();
            assertThat(findDailyPlanner.getCreateTime()).isNotEqualTo(findDailyPlanner.getUpdateTime());
        }
    }
}
