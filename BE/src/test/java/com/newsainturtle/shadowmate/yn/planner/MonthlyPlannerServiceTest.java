package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.response.AddVisitorBookResponse;
import com.newsainturtle.shadowmate.planner.entity.VisitorBook;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.VisitorBookRepository;
import com.newsainturtle.shadowmate.planner.service.MonthlyPlannerServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonthlyPlannerServiceTest {

    @InjectMocks
    private MonthlyPlannerServiceImpl monthlyPlannerServiceImpl;

    @Mock
    private VisitorBookRepository visitorBookRepository;

    @Mock
    private UserRepository userRepository;

    private final User owner = User.builder()
            .id(1L)
            .email("yntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    final User visitor = User.builder()
            .id(2L)
            .email("jntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("토끼")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @Nested
    class 방명록 {

        @Nested
        class 방명록추가 {
            private final String visitorBookContent = "왔다가유 @--";
            final AddVisitorBookRequest addVisitorBookRequest = AddVisitorBookRequest.builder()
                    .visitorBookContent(visitorBookContent)
                    .build();

            @Test
            void 실패_유효하지않은사용자() {
                //given
                doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> monthlyPlannerServiceImpl.addVisitorBook(visitor, owner.getId(), addVisitorBookRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
            }

            @Test
            void 실패_자신플래너에방명록추가() {
                //given
                doReturn(visitor).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> monthlyPlannerServiceImpl.addVisitorBook(visitor, owner.getId(), addVisitorBookRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.FAILED_SELF_VISITOR_BOOK_WRITING);
            }

            @Test
            void 성공() {
                //given
                final VisitorBook visitorBook = VisitorBook.builder()
                        .id(1L)
                        .owner(owner)
                        .visitor(visitor)
                        .visitorBookContent(addVisitorBookRequest.getVisitorBookContent())
                        .createTime(LocalDateTime.now())
                        .build();
                doReturn(owner).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(visitorBook).when(visitorBookRepository).save(any(VisitorBook.class));

                //when
                final AddVisitorBookResponse addVisitorBookResponse = monthlyPlannerServiceImpl.addVisitorBook(visitor, owner.getId(), addVisitorBookRequest);

                //then
                assertThat(addVisitorBookResponse).isNotNull();
                assertThat(addVisitorBookResponse.getVisitorNickname()).isEqualTo(visitor.getNickname());
                assertThat(addVisitorBookResponse.getVisitorProfileImage()).isEqualTo(visitor.getProfileImage());
                assertThat(addVisitorBookResponse.getVisitorBookContent()).isEqualTo(addVisitorBookRequest.getVisitorBookContent());

                //verify
                verify(userRepository, times(1)).findByIdAndWithdrawalIsFalse(any(Long.class));
                verify(visitorBookRepository, times(1)).save(any(VisitorBook.class));
            }
        }
    }

}
