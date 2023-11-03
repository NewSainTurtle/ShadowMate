package com.newsainturtle.shadowmate.yn.planner_setting;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.dto.request.*;
import com.newsainturtle.shadowmate.planner_setting.dto.response.*;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryColorRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.CategoryRepository;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingServiceImpl;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlannerSettingServiceTest {

    @InjectMocks
    private PlannerSettingServiceImpl plannerSettingService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryColorRepository categoryColorRepository;

    @Mock
    private DdayRepository ddayRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private FollowRequestRepository followRequestRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private DailyPlannerRepository dailyPlannerRepository;

    @Mock
    private SocialRepository socialRepository;

    private final User user = User.builder()
            .id(1L)
            .email("yntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @Nested
    class 카테고리 {
        final CategoryColor categoryColor = CategoryColor.builder()
                .categoryColorCode("#D9B5D9")
                .build();
        final Category category = Category.builder()
                .id(1L)
                .categoryColor(categoryColor)
                .user(user)
                .categoryTitle("국어")
                .categoryRemove(false)
                .categoryEmoticon("🍅")
                .build();

        @Nested
        class 카테고리등록 {
            final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                    .categoryTitle("국어")
                    .categoryEmoticon("🍅")
                    .categoryColorId(1L)
                    .build();

            @Test
            void 실패_없는카테고리색상() {
                //given
                doReturn(Optional.empty()).when(categoryColorRepository).findById(addCategoryRequest.getCategoryColorId());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.addCategory(user, addCategoryRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY_COLOR);
            }

            @Test
            void 성공_카테고리등록_이모티콘없음() {
                //given
                final AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
                        .categoryTitle("국어")
                        .categoryEmoticon(null)
                        .categoryColorId(1L)
                        .build();

                doReturn(Optional.of(categoryColor)).when(categoryColorRepository).findById(addCategoryRequest.getCategoryColorId());
                doReturn(category).when(categoryRepository).save(any(Category.class));

                //when
                final AddCategoryResponse saveCategory = plannerSettingService.addCategory(user, addCategoryRequest);

                //then
                assertThat(saveCategory.getCategoryId()).isNotNull();
                assertThat(saveCategory.getCategoryId()).isEqualTo(1L);

                //verify
                verify(categoryColorRepository, times(1)).findById(addCategoryRequest.getCategoryColorId());
                verify(categoryRepository, times(1)).save(any(Category.class));
            }

            @Test
            void 성공_카테고리등록_이모티콘있음() {
                //given
                doReturn(Optional.of(categoryColor)).when(categoryColorRepository).findById(addCategoryRequest.getCategoryColorId());
                doReturn(category).when(categoryRepository).save(any(Category.class));

                //when
                final AddCategoryResponse saveCategory = plannerSettingService.addCategory(user, addCategoryRequest);

                //then
                assertThat(saveCategory.getCategoryId()).isNotNull();
                assertThat(saveCategory.getCategoryId()).isEqualTo(1L);

                //verify
                verify(categoryColorRepository, times(1)).findById(addCategoryRequest.getCategoryColorId());
                verify(categoryRepository, times(1)).save(any(Category.class));
            }
        }

        @Nested
        class 카테고리수정 {
            final UpdateCategoryRequest updateCategoryRequest = UpdateCategoryRequest.builder()
                    .categoryId(1L)
                    .categoryTitle("수학")
                    .categoryEmoticon("🌀")
                    .categoryColorId(1L)
                    .build();

            @Test
            void 실패_없는카테고리() {
                //given
                doReturn(null).when(categoryRepository).findByUserAndId(user, updateCategoryRequest.getCategoryId());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateCategory(user, updateCategoryRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
            }

            @Test
            void 실패_없는카테고리색상() {
                //given
                doReturn(category).when(categoryRepository).findByUserAndId(user, updateCategoryRequest.getCategoryId());
                doReturn(Optional.empty()).when(categoryColorRepository).findById(updateCategoryRequest.getCategoryColorId());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateCategory(user, updateCategoryRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY_COLOR);
            }

            @Test
            void 성공_카테고리등록() {
                //given
                doReturn(category).when(categoryRepository).findByUserAndId(user, updateCategoryRequest.getCategoryId());
                doReturn(Optional.of(categoryColor)).when(categoryColorRepository).findById(updateCategoryRequest.getCategoryColorId());

                //when
                plannerSettingService.updateCategory(user, updateCategoryRequest);

                //then

                //verify
                verify(categoryColorRepository, times(1)).findById(updateCategoryRequest.getCategoryColorId());
                verify(categoryColorRepository, times(1)).findById(updateCategoryRequest.getCategoryColorId());
                verify(categoryRepository, times(1)).save(any(Category.class));
            }
        }

        @Nested
        class 카테고리설정_조회 {

            @Test
            void 카테고리색상목록조회() {
                //given
                final List<CategoryColor> list = new ArrayList<>();
                list.add(categoryColor);
                doReturn(list).when(categoryColorRepository).findAll();

                //when
                final GetCategoryColorListResponse result = plannerSettingService.getCategoryColorList();

                //then
                assertThat(result.getCategoryColorList()).isNotNull();
                assertThat(result.getCategoryColorList()).hasSize(1);
            }

            @Test
            void 카테고리목록조회() {
                //given
                final List<Category> list = new ArrayList<>();
                list.add(category);
                doReturn(list).when(categoryRepository).findByUserAndAndCategoryRemoveIsFalse(user);

                //when
                final GetCategoryListResponse result = plannerSettingService.getCategoryList(user);

                //then
                assertThat(result.getCategoryList()).isNotNull();
                assertThat(result.getCategoryList()).hasSize(1);
            }
        }

        @Nested
        class 카테고리삭제 {
            final RemoveCategoryRequest removeCategoryRequest = RemoveCategoryRequest.builder()
                    .categoryId(1L)
                    .build();

            @Test
            void 실패_없는카테고리() {
                //given
                doReturn(null).when(categoryRepository).findByUserAndId(user, removeCategoryRequest.getCategoryId());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.removeCategory(user, removeCategoryRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
            }


            @Test
            void 성공_카테고리_할일에없음() {
                //given
                doReturn(category).when(categoryRepository).findByUserAndId(user, removeCategoryRequest.getCategoryId());
                doReturn(0L).when(todoRepository).countByCategory(category);

                //when
                plannerSettingService.removeCategory(user, removeCategoryRequest);

                //then

                //verify
                verify(categoryRepository, times(1)).findByUserAndId(any(), any(Long.class));
                verify(todoRepository, times(1)).countByCategory(any(Category.class));
                verify(categoryRepository, times(1)).deleteByUserAndId(any(), any(Long.class));
            }

            @Test
            void 성공_카테고리_할일에있음() {
                //given
                doReturn(category).when(categoryRepository).findByUserAndId(user, removeCategoryRequest.getCategoryId());
                doReturn(1L).when(todoRepository).countByCategory(category);

                //when
                plannerSettingService.removeCategory(user, removeCategoryRequest);

                //then

                //verify
                verify(categoryRepository, times(1)).findByUserAndId(any(), any(Long.class));
                verify(todoRepository, times(1)).countByCategory(any(Category.class));
                verify(categoryRepository, times(1)).save(any(Category.class));
            }
        }
    }

    @Nested
    class 플래너공개여부설정 {
        @Test
        void 실패_잘못된범위값() {
            //given
            final SetAccessScopeRequest setAccessScopeRequest = SetAccessScopeRequest.builder()
                    .plannerAccessScope("잘못된범위값")
                    .build();

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.setAccessScope(user, setAccessScopeRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_PLANNER_ACCESS_SCOPE);
        }

        @Test
        void 성공_플래너공개여부설정_비공개에서_공개로() {
            //given
            final SetAccessScopeRequest setAccessScopeRequest = SetAccessScopeRequest.builder()
                    .plannerAccessScope("전체공개")
                    .build();
            final User user2 = User.builder()
                    .id(2L)
                    .email("jntest@shadowmate.com")
                    .password("yntest1234")
                    .socialLogin(SocialType.BASIC)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.PRIVATE)
                    .withdrawal(false)
                    .build();
            final User user3 = User.builder()
                    .id(3L)
                    .email("nctest@shadowmate.com")
                    .password("yntest1234")
                    .socialLogin(SocialType.BASIC)
                    .nickname("고양이")
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .withdrawal(false)
                    .build();
            final List<FollowRequest> followRequestList = new ArrayList<>();
            followRequestList.add(FollowRequest.builder()
                    .id(1L)
                    .receiverId(user2)
                    .requesterId(user)
                    .build());
            followRequestList.add(FollowRequest.builder()
                    .id(2L)
                    .receiverId(user2)
                    .requesterId(user3)
                    .build());

            doReturn(followRequestList).when(followRequestRepository).findAllByReceiverId(any(User.class));
            doReturn(new ArrayList<>()).when(dailyPlannerRepository).findAllByUser(any(User.class));

            //when
            plannerSettingService.setAccessScope(user2, setAccessScopeRequest);

            //then
            verify(followRequestRepository, times(1)).findAllByReceiverId(any(User.class));
            verify(followRepository, times(2)).save(any(Follow.class));
            verify(followRequestRepository, times(1)).deleteAllByReceiverId(any(User.class));
            verify(dailyPlannerRepository, times(1)).findAllByUser(any(User.class));
            verify(socialRepository, times(1)).updateDeleteTimeAll(any(), any(List.class));
            verify(userRepository, times(1)).save(any(User.class));
        }


        @Test
        void 성공_플래너공개여부설정_공개에서_비공개() {
            //given
            final SetAccessScopeRequest setAccessScopeRequest = SetAccessScopeRequest.builder()
                    .plannerAccessScope("비공개")
                    .build();

            doReturn(new ArrayList<>()).when(dailyPlannerRepository).findAllByUser(any(User.class));

            //when
            plannerSettingService.setAccessScope(user, setAccessScopeRequest);

            //then
            verify(dailyPlannerRepository, times(1)).findAllByUser(any(User.class));
            verify(socialRepository, times(1)).updateDeleteTimeAll(any(), any(List.class));
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    class 디데이 {
        final Dday dday = Dday.builder()
                .id(1L)
                .ddayTitle("생일")
                .ddayDate(Date.valueOf("2023-02-09"))
                .user(user)
                .build();

        @Test
        void 디데이등록_성공() {
            //given
            final AddDdayRequest addDdayRequest = AddDdayRequest.builder()
                    .ddayTitle("생일")
                    .ddayDate("2023-02-09")
                    .build();

            doReturn(dday).when(ddayRepository).save(any(Dday.class));

            //when
            AddDdayResponse addDdayResponse = plannerSettingService.addDday(user, addDdayRequest);

            //then
            assertThat(addDdayResponse.getDdayId()).isNotNull();
            assertThat(addDdayResponse.getDdayId()).isEqualTo(1L);

            //verity
            verify(ddayRepository, times(1)).save(any(Dday.class));
        }

        @Nested
        class 디데이조회 {
            @Test
            void 등록된디데이목록이없음() {
                //given
                final List<Dday> list = new ArrayList<>();
                doReturn(list).when(ddayRepository).findByUserOrderByDdayDateDesc(user);

                //when
                final GetDdayListResponse ddayListResponse = plannerSettingService.getDdayList(user);

                //then
                assertThat(ddayListResponse.getDdayList()).isNotNull();
                assertThat(ddayListResponse.getDdayList()).isEmpty();
            }

            @Test
            void 등록된디데이목록이있음() {
                //given
                final List<Dday> list = new ArrayList<>();
                list.add(Dday.builder()
                        .ddayTitle("시험")
                        .ddayDate(Date.valueOf("2024-09-14"))
                        .user(user)
                        .build());
                list.add(dday);
                doReturn(list).when(ddayRepository).findByUserOrderByDdayDateDesc(user);

                //when
                final GetDdayListResponse ddayListResponse = plannerSettingService.getDdayList(user);

                //then
                assertThat(ddayListResponse.getDdayList()).isNotNull();
                assertThat(ddayListResponse.getDdayList()).hasSize(2);
            }
        }

        @Test
        void 디데이삭제_성공() {
            //given
            final RemoveDdayRequest removeDdayRequest = RemoveDdayRequest.builder()
                    .ddayId(1L)
                    .build();

            //when
            plannerSettingService.removeDday(user, removeDdayRequest);

            //then

            //verity
            verify(ddayRepository, times(1)).deleteByUserAndId(any(), any(Long.class));
        }

        @Nested
        class 디데이수정 {
            final UpdateDdayRequest updateDdayRequest = UpdateDdayRequest.builder()
                    .ddayId(1L)
                    .ddayTitle("시험")
                    .ddayDate("2023-09-18")
                    .build();

            @Test
            void 실패_유효하지않은디데이_권한이없는디데이() {
                //given
                doReturn(null).when(ddayRepository).findByUserAndId(user, 1L);

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateDday(user, updateDdayRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_DDAY);
            }

            @Test
            void 성공() {
                //given
                doReturn(dday).when(ddayRepository).findByUserAndId(user, 1L);

                //when
                plannerSettingService.updateDday(user, updateDdayRequest);

                //then

                //verify
                verify(ddayRepository, times(1)).findByUserAndId(user, 1L);
                verify(ddayRepository, times(1)).save(any(Dday.class));
            }
        }
    }
}