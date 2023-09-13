package com.newsainturtle.shadowmate.yn.planner_setting.repository;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 플래너공개여부설정() {
        //given
        final User user = userRepository.save(User.builder()
                .email("test@test.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());

        final User findUser = userRepository.findById(user.getId()).orElse(null);
        final User changeUser = User.builder()
                .id(findUser.getId())
                .email(findUser.getEmail())
                .password(findUser.getPassword())
                .socialLogin(findUser.getSocialLogin())
                .nickname(findUser.getNickname())
                .plannerAccessScope(PlannerAccessScope.FOLLOW)
                .withdrawal(findUser.getWithdrawal())
                .createTime(findUser.getCreateTime())
                .build();
        userRepository.save(changeUser);

        //when
        final User result = userRepository.findById(user.getId()).orElse(null);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.FOLLOW);

    }

}
