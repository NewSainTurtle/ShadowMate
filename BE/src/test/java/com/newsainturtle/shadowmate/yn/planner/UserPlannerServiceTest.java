package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.response.SearchVisitorBookResponse;
import com.newsainturtle.shadowmate.planner.dto.response.VisitorBookResponse;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.service.MonthlyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.UserPlannerServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.service.UserServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPlannerServiceTest {

    @InjectMocks
    private UserPlannerServiceImpl userPlannerService;

    @Mock
    private MonthlyPlannerServiceImpl monthlyPlannerService;

    @Mock
    private UserServiceImpl userService;

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
        private final long ownerId = owner.getId();

        @Nested
        class 방명록추가 {
            private final String visitorBookContent = "왔다가유 @--";
            final AddVisitorBookRequest addVisitorBookRequest = AddVisitorBookRequest.builder()
                    .visitorBookContent(visitorBookContent)
                    .build();

            @Test
            void 실패_유효하지않은사용자() {
                //given
                doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(ownerId);

                //when
                final UserException result = assertThrows(UserException.class, () -> userPlannerService.addVisitorBook(visitor, ownerId, addVisitorBookRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
            }

            @Test
            void 실패_자신플래너에방명록추가() {
                //given
                final long visitorId = visitor.getId();
                doReturn(visitor).when(userService).getUserById(visitorId);
                doThrow(new PlannerException(PlannerErrorResult.FAILED_SELF_VISITOR_BOOK_WRITING)).when(monthlyPlannerService).addVisitorBook(visitor, visitor, addVisitorBookRequest);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.addVisitorBook(visitor, visitorId, addVisitorBookRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.FAILED_SELF_VISITOR_BOOK_WRITING);
            }

            @Test
            void 성공() {
                //given
                final VisitorBookResponse visitorBookResponse = VisitorBookResponse.builder()
                        .visitorBookId(1L)
                        .visitorId(visitor.getId())
                        .visitorNickname(visitor.getNickname())
                        .visitorProfileImage(visitor.getProfileImage())
                        .visitorBookContent("안녕하세요")
                        .build();
                doReturn(owner).when(userService).getUserById(ownerId);
                doReturn(visitorBookResponse).when(monthlyPlannerService).addVisitorBook(visitor, owner, addVisitorBookRequest);

                //when
                final VisitorBookResponse addVisitorBookResponse = userPlannerService.addVisitorBook(visitor, ownerId, addVisitorBookRequest);

                //then
                assertThat(addVisitorBookResponse).isNotNull();
                assertThat(addVisitorBookResponse.getVisitorBookId()).isEqualTo(1L);

                //verify
                verify(userService, times(1)).getUserById(any(Long.class));
                verify(monthlyPlannerService, times(1)).addVisitorBook(any(User.class), any(User.class), any(AddVisitorBookRequest.class));
            }
        }

        @Nested
        class 방명록조회 {

            @Test
            void 실패_유효하지않은사용자() {
                //given
                doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(ownerId);

                //when
                final UserException result = assertThrows(UserException.class, () -> userPlannerService.searchVisitorBook(visitor, ownerId, 0L));

                //then
                assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
            }

            @Test
            void 성공() {
                //given
                final long visitorId = visitor.getId();
                doReturn(visitor).when(userService).getUserById(visitorId);
                doReturn(SearchVisitorBookResponse.builder().visitorBookResponses(new ArrayList<>()).build()).when(monthlyPlannerService).searchVisitorBook(visitor, visitor, 0L);

                //when
                final SearchVisitorBookResponse searchVisitorBookResponse = userPlannerService.searchVisitorBook(visitor, visitorId, 0L);

                //then
                assertThat(searchVisitorBookResponse).isNotNull();

                //verify
                verify(userService, times(1)).getUserById(any(Long.class));
                verify(monthlyPlannerService, times(1)).searchVisitorBook(any(User.class), any(User.class), any(Long.class));
            }
        }
    }

}
