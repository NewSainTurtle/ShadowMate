package com.newsainturtle.shadowmate.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.VisitorBook;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

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
    private VisitorBook visitorBook;

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
        visitorBook = VisitorBook.builder()
                .owner(owner)
                .visitor(visitor)
                .visitorBookContent(visitorBookContent)
                .build();
    }

    @Test
    void 방명록추가() {
        //given
        final VisitorBook saveVisitorBook = visitorBookRepository.save(visitorBook);

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
        final VisitorBook saveVisitorBook = visitorBookRepository.save(visitorBook);

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
        final VisitorBook saveVisitorBook = visitorBookRepository.save(visitorBook);

        //when
        visitorBookRepository.deleteById(saveVisitorBook.getId());
        final VisitorBook findVisitorBook = visitorBookRepository.findByIdAndOwnerId(saveVisitorBook.getId(), owner.getId());


        //then
        assertThat(findVisitorBook).isNull();
    }

    @Test
    void 방명록일괄삭제() {
        //given
        visitorBookRepository.save(visitorBook);
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());

        //when
        final List<VisitorBook> beforeVisitorBooks = visitorBookRepository.findTop10ByOwnerOrderByIdDesc(owner);
        visitorBookRepository.deleteAllByVisitorId(visitor.getId());
        final List<VisitorBook> afterVisitorBooks = visitorBookRepository.findTop10ByOwnerOrderByIdDesc(owner);


        //then
        assertThat(beforeVisitorBooks).hasSize(4);
        assertThat(afterVisitorBooks).isEmpty();
    }

    @Test
    void 방명록리스트조회_처음_0개() {
        //given

        //when
        final List<VisitorBook> visitorBooks = visitorBookRepository.findTop10ByOwnerOrderByIdDesc(owner);

        //then
        assertThat(visitorBooks).isNotNull().isEmpty();
    }

    @Test
    void 방명록리스트조회_처음_10개미만() {
        //given
        visitorBookRepository.save(visitorBook);
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());

        //when
        final List<VisitorBook> visitorBooks = visitorBookRepository.findTop10ByOwnerOrderByIdDesc(owner);

        //then
        assertThat(visitorBooks).isNotNull().hasSize(3);
    }

    @Test
    void 방명록리스트조회_처음_10개이상() {
        //given
        visitorBookRepository.save(visitorBook);
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());

        //when
        final List<VisitorBook> visitorBooks = visitorBookRepository.findTop10ByOwnerOrderByIdDesc(owner);

        //then
        assertThat(visitorBooks).isNotNull().hasSize(10);
    }

    @Test
    void 방명록리스트조회_연속() {
        //given
        visitorBookRepository.save(visitorBook);
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        final VisitorBook saveVisitorBook = visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());
        visitorBookRepository.save(VisitorBook.builder().owner(owner).visitor(visitor).visitorBookContent(visitorBookContent).build());

        //when
        final List<VisitorBook> visitorBooks = visitorBookRepository.findTop10ByOwnerAndIdLessThanOrderByIdDesc(owner, saveVisitorBook.getId());

        //then
        assertThat(visitorBooks).isNotNull().hasSize(4);
    }
}
