package com.newsainturtle.shadowmate.yn.planner.repository;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import com.newsainturtle.shadowmate.planner.entity.VisitorBook;
import com.newsainturtle.shadowmate.planner.repository.VisitorBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VisitorBookRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VisitorBookRepository visitorBookRepository;

    private User owner;
    private User visitor;

    private final String visitorBookContent = "왔다가유 @--";

    @BeforeEach
    void init() {
        owner = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        visitor = userRepository.save(User.builder()
                .email("jntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("토끼")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Test
    void 방명록추가() {
        //given
        final VisitorBook saveVisitorBook = visitorBookRepository.save(VisitorBook.builder()
                .owner(owner)
                .visitor(visitor)
                .visitorBookContent(visitorBookContent)
                .build());

        //when
        final VisitorBook findVisitorBook = visitorBookRepository.findById(saveVisitorBook.getId()).orElse(null);

        //then
        assertThat(findVisitorBook).isNotNull();
        assertThat(findVisitorBook.getOwner()).isEqualTo(owner);
        assertThat(findVisitorBook.getVisitor()).isEqualTo(visitor);
        assertThat(findVisitorBook.getVisitorBookContent()).isEqualTo(visitorBookContent);
    }

    @Test
    void 방명록ID조회_없음() {
        //given

        //when
        final VisitorBook visitorBook = visitorBookRepository.findByIdAndOwnerId(0L, owner.getId());

        //then
        assertThat(visitorBook).isNull();
    }

    @Test
    void 방명록ID조회_있음() {
        //given
        final VisitorBook saveVisitorBook = visitorBookRepository.save(VisitorBook.builder()
                .owner(owner)
                .visitor(visitor)
                .visitorBookContent(visitorBookContent)
                .build());

        //when
        final VisitorBook findVisitorBook = visitorBookRepository.findByIdAndOwnerId(saveVisitorBook.getId(), owner.getId());

        //then
        assertThat(findVisitorBook).isNotNull();
        assertThat(findVisitorBook.getOwner()).isEqualTo(owner);
        assertThat(findVisitorBook.getVisitor()).isEqualTo(visitor);
        assertThat(findVisitorBook.getVisitorBookContent()).isEqualTo(visitorBookContent);
    }

    @Test
    void 방명록삭제() {
        //given
        final VisitorBook saveVisitorBook = visitorBookRepository.save(VisitorBook.builder()
                .owner(owner)
                .visitor(visitor)
                .visitorBookContent(visitorBookContent)
                .build());

        //when
        visitorBookRepository.deleteById(saveVisitorBook.getId());
        final VisitorBook findVisitorBook = visitorBookRepository.findByIdAndOwnerId(saveVisitorBook.getId(), owner.getId());


        //then
        assertThat(findVisitorBook).isNull();
    }



}
