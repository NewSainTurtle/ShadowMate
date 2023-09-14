package com.newsainturtle.shadowmate.yn.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DdayRepositoryTest {

    @Autowired
    private DdayRepository ddayRepository;

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
    public void 디데이등록() {
        //given
        final Dday dday = Dday.builder()
                .ddayTitle("생일")
                .ddayDate(Date.valueOf("2023-02-09"))
                .user(user)
                .build();

        //when
        final Dday saveDday = ddayRepository.save(dday);

        //then
        assertThat(saveDday).isNotNull();
        assertThat(saveDday.getDdayDate()).isEqualTo("2023-02-09");
        assertThat(saveDday.getDdayTitle()).isEqualTo("생일");
        assertThat(saveDday.getUser()).isEqualTo(user);
    }
}
