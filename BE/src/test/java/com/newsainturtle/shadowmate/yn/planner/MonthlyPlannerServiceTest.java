package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.response.SearchVisitorBookResponse;
import com.newsainturtle.shadowmate.planner.dto.response.VisitorBookResponse;
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
import java.util.ArrayList;
import java.util.List;

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
                final VisitorBookResponse addVisitorBookResponse = monthlyPlannerServiceImpl.addVisitorBook(visitor, owner.getId(), addVisitorBookRequest);

                //then
                assertThat(addVisitorBookResponse).isNotNull();
                assertThat(addVisitorBookResponse.getVisitorBookId()).isEqualTo(visitorBook.getId());
                assertThat(addVisitorBookResponse.getVisitorId()).isEqualTo(visitor.getId());
                assertThat(addVisitorBookResponse.getVisitorNickname()).isEqualTo(visitor.getNickname());
                assertThat(addVisitorBookResponse.getVisitorProfileImage()).isEqualTo(visitor.getProfileImage());
                assertThat(addVisitorBookResponse.getVisitorBookContent()).isEqualTo(addVisitorBookRequest.getVisitorBookContent());

                //verify
                verify(userRepository, times(1)).findByIdAndWithdrawalIsFalse(any(Long.class));
                verify(visitorBookRepository, times(1)).save(any(VisitorBook.class));
            }
        }

        @Nested
        class 방명록삭제 {
            final VisitorBook visitorBook = VisitorBook.builder()
                    .id(1L)
                    .visitorBookContent("왔다가유 @--")
                    .owner(owner)
                    .visitor(visitor)
                    .build();
            final RemoveVisitorBookRequest removeVisitorBookRequest = RemoveVisitorBookRequest.builder()
                    .visitorBookId(1L)
                    .build();

            @Test
            void 실패_유효하지않은방명록() {
                //given
                doReturn(null).when(visitorBookRepository).findByIdAndOwnerId(any(Long.class), any(Long.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> monthlyPlannerServiceImpl.removeVisitorBook(visitor, owner.getId(), removeVisitorBookRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_VISITOR_BOOK);
            }

            @Test
            void 실패_삭제권한이없는사용자() {
                //given
                final User user1 = User.builder()
                        .id(3L)
                        .email("mktest@shadowmate.com")
                        .password("yntest1234")
                        .socialLogin(SocialType.BASIC)
                        .nickname("호랑이")
                        .plannerAccessScope(PlannerAccessScope.PUBLIC)
                        .withdrawal(false)
                        .build();
                doReturn(visitorBook).when(visitorBookRepository).findByIdAndOwnerId(any(Long.class), any(Long.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> monthlyPlannerServiceImpl.removeVisitorBook(user1, owner.getId(), removeVisitorBookRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.NO_PERMISSION_TO_REMOVE_VISITOR_BOOK);
            }

            @Test
            void 성공_방명록작성자() {
                //given
                doReturn(visitorBook).when(visitorBookRepository).findByIdAndOwnerId(any(Long.class), any(Long.class));

                //when
                monthlyPlannerServiceImpl.removeVisitorBook(visitor, owner.getId(), removeVisitorBookRequest);

                //then

                //verify
                verify(visitorBookRepository, times(1)).findByIdAndOwnerId(any(Long.class), any(Long.class));
                verify(visitorBookRepository, times(1)).deleteById(any(Long.class));
            }

            @Test
            void 성공_플래너주인() {
                //given
                doReturn(visitorBook).when(visitorBookRepository).findByIdAndOwnerId(any(Long.class), any(Long.class));

                //when
                monthlyPlannerServiceImpl.removeVisitorBook(owner, owner.getId(), removeVisitorBookRequest);

                //then

                //verify
                verify(visitorBookRepository, times(1)).findByIdAndOwnerId(any(Long.class), any(Long.class));
                verify(visitorBookRepository, times(1)).deleteById(any(Long.class));
            }
        }

        @Nested
        class 방명록조회 {

            @Test
            void 실패_유효하지않은사용자() {
                //given
                doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> monthlyPlannerServiceImpl.searchVisitorBook(visitor, owner.getId(), 0L));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
            }

            @Test
            void 성공_처음() {
                //given
                final List<VisitorBook> visitorBooks = new ArrayList<>();
                visitorBooks.add(VisitorBook.builder()
                        .id(1L)
                        .owner(owner)
                        .visitor(visitor)
                        .visitorBookContent("안녕하세요~!")
                        .createTime(LocalDateTime.now())
                        .build());
                visitorBooks.add(VisitorBook.builder()
                        .id(2L)
                        .owner(owner)
                        .visitor(visitor)
                        .visitorBookContent("안녕하세요~!")
                        .createTime(LocalDateTime.now())
                        .build());
                doReturn(owner).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(visitorBooks).when(visitorBookRepository).findTop10ByOwnerOrderByIdDesc(any());

                //when
                final SearchVisitorBookResponse searchVisitorBookResponse = monthlyPlannerServiceImpl.searchVisitorBook(visitor, owner.getId(), 0L);

                //then
                assertThat(searchVisitorBookResponse).isNotNull();
                assertThat(searchVisitorBookResponse.getVisitorBookResponses()).hasSize(2);

                //verify
                verify(userRepository, times(1)).findByIdAndWithdrawalIsFalse(any(Long.class));
                verify(visitorBookRepository, times(1)).findTop10ByOwnerOrderByIdDesc(any());
            }

            @Test
            void 성공_연속() {
                //given
                final List<VisitorBook> visitorBooks = new ArrayList<>();
                visitorBooks.add(VisitorBook.builder()
                        .id(1L)
                        .owner(owner)
                        .visitor(visitor)
                        .visitorBookContent("안녕하세요~!")
                        .createTime(LocalDateTime.now())
                        .build());
                visitorBooks.add(VisitorBook.builder()
                        .id(2L)
                        .owner(owner)
                        .visitor(visitor)
                        .visitorBookContent("안녕하세요~!")
                        .createTime(LocalDateTime.now())
                        .build());
                doReturn(owner).when(userRepository).findByIdAndWithdrawalIsFalse(any(Long.class));
                doReturn(visitorBooks).when(visitorBookRepository).findTop10ByOwnerAndIdLessThanOrderByIdDesc(any(), any(Long.class));

                //when
                final SearchVisitorBookResponse searchVisitorBookResponse = monthlyPlannerServiceImpl.searchVisitorBook(visitor, owner.getId(), 100L);

                //then
                assertThat(searchVisitorBookResponse).isNotNull();
                assertThat(searchVisitorBookResponse.getVisitorBookResponses()).hasSize(2);

                //verify
                verify(userRepository, times(1)).findByIdAndWithdrawalIsFalse(any(Long.class));
                verify(visitorBookRepository, times(1)).findTop10ByOwnerAndIdLessThanOrderByIdDesc(any(), any(Long.class));
            }
        }
    }

}
