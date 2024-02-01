package com.newsainturtle.shadowmate.yn.planner_setting;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.entity.FollowRequest;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.follow.repository.FollowRequestRepository;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner_setting.dto.request.*;
import com.newsainturtle.shadowmate.planner_setting.dto.response.*;
import com.newsainturtle.shadowmate.planner_setting.entity.*;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingException;
import com.newsainturtle.shadowmate.planner_setting.repository.*;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingServiceImpl;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlannerSettingServiceTest extends DateCommonService {

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

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private RoutineTodoRepository routineTodoRepository;

    @Mock
    private RoutineDayRepository routineDayRepository;

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
            void 실패_없는카테고리2() {
                //given
                final UpdateCategoryRequest updateCategoryRequest = UpdateCategoryRequest.builder()
                        .categoryId(0L)
                        .categoryTitle("수학")
                        .categoryEmoticon("🌀")
                        .categoryColorId(1L)
                        .build();

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
            void 실패_없는카테고리2() {
                //given
                final RemoveCategoryRequest removeCategoryRequest = RemoveCategoryRequest.builder()
                        .categoryId(0L)
                        .build();

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.removeCategory(user, removeCategoryRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
            }


            @Test
            void 성공_카테고리_할일에없음_루틴에없음() {
                //given
                doReturn(category).when(categoryRepository).findByUserAndId(user, removeCategoryRequest.getCategoryId());
                doReturn(0L).when(todoRepository).countByCategory(category);
                doReturn(0L).when(routineRepository).countByCategory(category);

                //when
                plannerSettingService.removeCategory(user, removeCategoryRequest);

                //then

                //verify
                verify(categoryRepository, times(1)).findByUserAndId(any(), any(Long.class));
                verify(todoRepository, times(1)).countByCategory(any(Category.class));
                verify(routineRepository, times(1)).countByCategory(any(Category.class));
                verify(categoryRepository, times(1)).deleteByUserAndId(any(), any(Long.class));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("removeCategoryRequest")
    void 카테고리삭제_성공(final long countCategoryTodo, final long countCategoryRoutine) throws Exception {
        // given
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
        final RemoveCategoryRequest removeCategoryRequest = RemoveCategoryRequest.builder()
                .categoryId(1L)
                .build();
        doReturn(category).when(categoryRepository).findByUserAndId(user, removeCategoryRequest.getCategoryId());
        doReturn(countCategoryTodo).when(todoRepository).countByCategory(category);
        doReturn(countCategoryRoutine).when(routineRepository).countByCategory(category);

        //when
        plannerSettingService.removeCategory(user, removeCategoryRequest);

        //then
        verify(categoryRepository, times(1)).findByUserAndId(any(), any(Long.class));
        verify(todoRepository, times(1)).countByCategory(any(Category.class));
        verify(routineRepository, times(1)).countByCategory(any(Category.class));
    }

    private static Stream<Arguments> removeCategoryRequest() {
        return Stream.of(
                // 성공_카테고리_할일에없음_루틴에있음
                Arguments.of(0L, 1L),
                // 성공_카테고리_할일에있음_루틴에없음
                Arguments.of(1L, 0L),
                // 성공_카테고리_할일에있음_루틴에있음
                Arguments.of(1L, 1L)
        );
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
                    .receiver(user2)
                    .requester(user)
                    .build());
            followRequestList.add(FollowRequest.builder()
                    .id(2L)
                    .receiver(user2)
                    .requester(user3)
                    .build());

            doReturn(followRequestList).when(followRequestRepository).findAllByReceiver(any(User.class));
            doReturn(new ArrayList<>()).when(dailyPlannerRepository).findAllByUser(any(User.class));

            //when
            plannerSettingService.setAccessScope(user2, setAccessScopeRequest);

            //then
            verify(followRequestRepository, times(1)).findAllByReceiver(any(User.class));
            verify(followRepository, times(2)).save(any(Follow.class));
            verify(followRequestRepository, times(1)).deleteAllByReceiver(any(Long.class));
            verify(dailyPlannerRepository, times(1)).findAllByUser(any(User.class));
            verify(socialRepository, times(1)).updateDeleteTimeAll(any(), any(List.class));
            verify(userRepository, times(1)).updatePlannerAccessScope(any(PlannerAccessScope.class), any(Long.class));
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
            verify(userRepository, times(1)).updatePlannerAccessScope(any(PlannerAccessScope.class), any(Long.class));
        }
    }

    @Nested
    class 디데이 {
        final Dday dday = Dday.builder()
                .id(1L)
                .ddayTitle("생일")
                .ddayDate("2023-02-09")
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
                        .ddayDate("2024-09-14")
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
            }
        }
    }

    @Nested
    class 루틴 {
        final Routine routine = Routine.builder()
                .id(1L)
                .routineContent("아침운동하기")
                .startDay("2023-12-25")
                .endDay("2024-01-09")
                .user(user)
                .category(null)
                .routineDays(new ArrayList<>())
                .routineTodos(new ArrayList<>())
                .build();
        final List<String> days = Arrays.asList(new String[]{"월", "수"});

        @Nested
        class 루틴등록 {

            @Test
            void 실패_시작날짜보다_과거인종료날짜() {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routine.getRoutineContent())
                        .startDay("2023-12-25")
                        .endDay("2023-12-20")
                        .categoryId(0L)
                        .days(days)
                        .build();

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.addRoutine(user, addRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_DATE);
            }

            @Test
            void 실패_유효하지않은요일() {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routine.getRoutineContent())
                        .startDay("2023-12-25")
                        .endDay("2024-01-09")
                        .categoryId(0L)
                        .days(Arrays.asList(new String[]{"디", "수"}))
                        .build();
                doReturn(routine).when(routineRepository).save(any(Routine.class));


                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.addRoutine(user, addRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
            }

            @Test
            void 실패_요일중복() {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routine.getRoutineContent())
                        .startDay("2023-12-25")
                        .endDay("2024-01-09")
                        .categoryId(0L)
                        .days(Arrays.asList(new String[]{"월", "수", "월"}))
                        .build();
                final RoutineTodo routineTodo = RoutineTodo.builder()
                        .id(1L)
                        .day("월")
                        .dailyPlannerDay("2023-12-25")
                        .todo(null)
                        .build();
                final RoutineDay routineDay = RoutineDay.builder()
                        .id(1L)
                        .day("월")
                        .build();
                doReturn(routine).when(routineRepository).save(any(Routine.class));
                doReturn(routineTodo).when(routineTodoRepository).save(any(RoutineTodo.class));

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.addRoutine(user, addRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
            }

            @Test
            void 실패_유효하지않은카테고리() {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routine.getRoutineContent())
                        .startDay("2023-12-25")
                        .endDay("2024-01-09")
                        .categoryId(1L)
                        .days(Arrays.asList(new String[]{"월", "수"}))
                        .build();
                doReturn(null).when(categoryRepository).findByUserAndId(any(), any(Long.class));

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.addRoutine(user, addRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
            }

            @Test
            void 성공() {
                //given
                final AddRoutineRequest addRoutineRequest = AddRoutineRequest.builder()
                        .routineContent(routine.getRoutineContent())
                        .startDay(routine.getStartDay())
                        .endDay(routine.getEndDay())
                        .categoryId(0L)
                        .days(days)
                        .build();
                final RoutineTodo routineTodo = RoutineTodo.builder()
                        .id(1L)
                        .day("월")
                        .dailyPlannerDay("2023-12-25")
                        .todo(null)
                        .build();
                final RoutineDay routineDay = RoutineDay.builder()
                        .id(1L)
                        .day("월")
                        .build();
                doReturn(routine).when(routineRepository).save(any(Routine.class));
                doReturn(routineTodo).when(routineTodoRepository).save(any(RoutineTodo.class));
                doReturn(routineDay).when(routineDayRepository).save(any(RoutineDay.class));

                //when
                plannerSettingService.addRoutine(user, addRoutineRequest);

                //then

                //verify
                verify(routineRepository, times(1)).save(any(Routine.class));
                verify(routineTodoRepository, times(5)).save(any(RoutineTodo.class));
                verify(routineDayRepository, times(2)).save(any(RoutineDay.class));
            }
        }

        @Test
        void 루틴목록조회_성공() {
            //given
            final Routine[] routines = new Routine[]{routine};
            final RoutineDay routineDay1 = RoutineDay.builder()
                    .id(1L)
                    .routine(routine)
                    .day("월")
                    .build();
            final RoutineDay routineDay2 = RoutineDay.builder()
                    .id(1L)
                    .routine(routine)
                    .day("수")
                    .build();
            final RoutineDay[] routineDays = new RoutineDay[]{routineDay1, routineDay2};

            doReturn(routines).when(routineRepository).findAllByUser(any());
            doReturn(routineDays).when(routineDayRepository).findAllByRoutine(any(Routine.class));

            //when
            final GetRoutineListResponse getRoutineListResponse = plannerSettingService.getRoutineList(user);

            //then
            assertThat(getRoutineListResponse).isNotNull();
            assertThat(getRoutineListResponse.getRoutineList()).isNotNull().hasSize(1);

            //verify
            verify(routineRepository, times(1)).findAllByUser(any());
            verify(routineDayRepository, times(1)).findAllByRoutine(any(Routine.class));
        }

        @Nested
        class 루틴삭제 {
            final String[] days = new String[]{"월", "수"};

            @Test
            void 실패_유효하지않은루틴() {
                //given
                final RemoveRoutineRequest removeRoutineRequest = RemoveRoutineRequest.builder()
                        .routineId(0L)
                        .order(1)
                        .build();
                doReturn(null).when(routineRepository).findByIdAndUser(any(Long.class), any());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.removeRoutine(user, removeRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE);
            }

            @Test
            void 실패_올바르지않은order값() {
                //given
                final RemoveRoutineRequest removeRoutineRequest = RemoveRoutineRequest.builder()
                        .routineId(0L)
                        .order(4)
                        .build();
                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.removeRoutine(user, removeRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ORDER);
            }

            @Test
            void 성공_1() {
                //given
                final RemoveRoutineRequest removeRoutineRequest = RemoveRoutineRequest.builder()
                        .routineId(0L)
                        .order(1)
                        .build();
                final DailyPlanner dailyPlanner = DailyPlanner.builder()
                        .id(1L)
                        .dailyPlannerDay("2023-12-25")
                        .user(user)
                        .build();
                final Todo todo = Todo.builder()
                        .id(1L)
                        .category(null)
                        .todoContent(routine.getRoutineContent())
                        .todoStatus(TodoStatus.EMPTY)
                        .dailyPlanner(dailyPlanner)
                        .todoIndex(100000D)
                        .build();
                final RoutineTodo routineTodo = RoutineTodo.builder()
                        .id(1L)
                        .day("월")
                        .dailyPlannerDay("2023-12-25")
                        .todo(todo)
                        .build();
                routineTodo.setRoutine(routine);

                final RoutineTodo[] routineTodoList = new RoutineTodo[]{routineTodo};
                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
                doReturn(routineTodoList).when(routineTodoRepository).findAllByRoutineAndTodoIsNotNull(any(Routine.class));

                //when
                plannerSettingService.removeRoutine(user, removeRoutineRequest);

                //then

                //verify
                verify(routineRepository, times(1)).findByIdAndUser(any(Long.class), any());
                verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNotNull(any(Routine.class));
                verify(routineRepository, times(1)).deleteByIdAndUser(any(Long.class), any());
            }

            @Test
            void 성공_2() {
                //given
                final RemoveRoutineRequest removeRoutineRequest = RemoveRoutineRequest.builder()
                        .routineId(0L)
                        .order(2)
                        .build();
                final DailyPlanner dailyPlanner1 = DailyPlanner.builder()
                        .id(1L)
                        .dailyPlannerDay("2023-12-20")
                        .user(user)
                        .build();
                final Todo todo = Todo.builder()
                        .id(1L)
                        .category(null)
                        .todoContent("수능완성 수학 과목별 10문제")
                        .todoStatus(TodoStatus.EMPTY)
                        .dailyPlanner(dailyPlanner1)
                        .todoIndex(100000D)
                        .timeTables(new ArrayList<>())
                        .build();
                final RoutineTodo routineTodo1 = RoutineTodo.builder()
                        .id(1L)
                        .day("수")
                        .dailyPlannerDay("2023-12-20")
                        .todo(todo)
                        .build();
                routineTodo1.setRoutine(routine);
                final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                        .id(1L)
                        .dailyPlannerDay("2023-12-25")
                        .user(user)
                        .build();
                final RoutineTodo routineTodo2 = RoutineTodo.builder()
                        .id(1L)
                        .day("월")
                        .dailyPlannerDay("2023-12-25")
                        .todo(null)
                        .build();
                routineTodo2.setRoutine(routine);
                final RoutineTodo[] routineTodoList1 = new RoutineTodo[]{routineTodo1};
                final RoutineTodo[] routineTodoList2 = new RoutineTodo[]{routineTodo2};

                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
                doReturn(routineTodoList1).when(routineTodoRepository).findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqual(any(Routine.class), any(String.class));
                doReturn(routineTodoList2).when(routineTodoRepository).findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThan(any(Routine.class), any(String.class));
                doReturn(dailyPlanner2).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(null).when(todoRepository).findTopByDailyPlannerOrderByTodoIndexDesc(any(DailyPlanner.class));

                //when
                plannerSettingService.removeRoutine(user, removeRoutineRequest);

                //then

                //verify
                verify(routineRepository, times(1)).findByIdAndUser(any(Long.class), any());
                verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqual(any(Routine.class), any(String.class));
                verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThan(any(Routine.class), any(String.class));
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(String.class));
                verify(todoRepository, times(1)).findTopByDailyPlannerOrderByTodoIndexDesc(any(DailyPlanner.class));
                verify(routineRepository, times(1)).deleteByIdAndUser(any(Long.class), any());
            }

            @Test
            void 성공_3() {
                //given
                final RemoveRoutineRequest removeRoutineRequest = RemoveRoutineRequest.builder()
                        .routineId(0L)
                        .order(3)
                        .build();
                final DailyPlanner dailyPlanner = DailyPlanner.builder()
                        .id(1L)
                        .dailyPlannerDay("2023-12-25")
                        .user(user)
                        .build();
                final RoutineTodo routineTodo = RoutineTodo.builder()
                        .id(1L)
                        .day("월")
                        .dailyPlannerDay("2023-12-25")
                        .todo(null)
                        .build();
                routineTodo.setRoutine(routine);
                final RoutineTodo[] routineTodoList = new RoutineTodo[]{routineTodo};

                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
                doReturn(routineTodoList).when(routineTodoRepository).findAllByRoutineAndTodoIsNull(any(Routine.class));
                doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(String.class));
                doReturn(null).when(todoRepository).findTopByDailyPlannerOrderByTodoIndexDesc(any(DailyPlanner.class));

                //when
                plannerSettingService.removeRoutine(user, removeRoutineRequest);

                //then

                //verify
                verify(routineRepository, times(1)).findByIdAndUser(any(Long.class), any());
                verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNull(any(Routine.class));
                verify(dailyPlannerRepository, times(1)).findByUserAndDailyPlannerDay(any(), any(String.class));
                verify(todoRepository, times(1)).findTopByDailyPlannerOrderByTodoIndexDesc(any(DailyPlanner.class));
                verify(routineRepository, times(1)).deleteByIdAndUser(any(Long.class), any());
            }
        }

        @Nested
        class 루틴수정 {
            final List<String> days = Arrays.asList(new String[]{"월", "수"});
            final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                    .routineId(1L)
                    .order(1)
                    .routineContent("아침운동하기")
                    .startDay("2024-01-01")
                    .endDay("2024-01-31")
                    .days(days)
                    .categoryId(0L)
                    .build();

            @Test
            void 실패_유효하지않은루틴() {
                //given
                doReturn(null).when(routineRepository).findByIdAndUser(any(Long.class), any());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateRoutine(user, updateRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE);
            }

            @Test
            void 실패_시작날짜보다_과거인종료날짜() {
                //given
                final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                        .routineId(1L)
                        .order(1)
                        .routineContent("아침운동하기")
                        .startDay("2023-12-25")
                        .endDay("2023-12-20")
                        .days(days)
                        .categoryId(0L)
                        .build();
                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateRoutine(user, updateRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_DATE);
            }

            @Test
            void 실패_유효하지않은카테고리() {
                //given
                final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                        .routineId(1L)
                        .order(1)
                        .routineContent("아침운동하기")
                        .startDay("2024-01-01")
                        .endDay("2024-01-31")
                        .days(days)
                        .categoryId(1L)
                        .build();
                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
                doReturn(null).when(categoryRepository).findByUserAndId(any(), any(Long.class));

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateRoutine(user, updateRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
            }

            @Test
            void 실패_올바르지않은order값() {
                //given
                final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                        .routineId(1L)
                        .order(4)
                        .routineContent("아침운동하기")
                        .startDay("2024-01-01")
                        .endDay("2024-01-31")
                        .days(days)
                        .categoryId(0L)
                        .build();
                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateRoutine(user, updateRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ORDER);
            }

            @Test
            void 실패_유효하지않은요일() {
                //given
                final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                        .routineId(1L)
                        .order(1)
                        .routineContent("아침운동하기")
                        .startDay("2024-01-01")
                        .endDay("2024-01-31")
                        .days(Arrays.asList(new String[]{"디", "수"}))
                        .categoryId(0L)
                        .build();
                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateRoutine(user, updateRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
            }

            @Test
            void 실패_요일중복() {
                //given
                final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                        .routineId(1L)
                        .order(1)
                        .routineContent("아침운동하기")
                        .startDay("2024-01-01")
                        .endDay("2024-01-31")
                        .days(Arrays.asList(new String[]{"월", "월", "수"}))
                        .categoryId(0L)
                        .build();
                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateRoutine(user, updateRoutineRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_ROUTINE_DAY);
            }

            @Test
            void 성공_order1_모두수정() {
                //given
                final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                        .routineId(1L)
                        .order(1)
                        .routineContent("저녁운동하기")
                        .startDay("2023-12-25")
                        .endDay("2024-01-09")
                        .days(days)
                        .categoryId(0L)
                        .build();

                final DailyPlanner dailyPlanner = DailyPlanner.builder()
                        .id(1L)
                        .dailyPlannerDay("2023-12-25")
                        .user(user)
                        .build();
                final Todo todo = Todo.builder()
                        .id(1L)
                        .category(null)
                        .todoContent(routine.getRoutineContent())
                        .todoStatus(TodoStatus.EMPTY)
                        .dailyPlanner(dailyPlanner)
                        .todoIndex(100000D)
                        .build();
                final RoutineDay routineDay1 = RoutineDay.builder()
                        .id(1L)
                        .day("월")
                        .build();
                final RoutineDay routineDay2 = RoutineDay.builder()
                        .id(2L)
                        .day("수")
                        .build();
                routineDay1.setRoutine(routine);
                routineDay2.setRoutine(routine);
                final RoutineTodo routineTodo = RoutineTodo.builder()
                        .id(1L)
                        .day("월")
                        .dailyPlannerDay("2023-12-25")
                        .todo(todo)
                        .build();
                routineTodo.setRoutine(routine);

                final RoutineDay[] routineDayList = new RoutineDay[]{routineDay1, routineDay2};
                final RoutineTodo[] routineTodoList = new RoutineTodo[]{routineTodo};
                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
                doReturn(routineDayList).when(routineDayRepository).findAllByRoutine(any(Routine.class));
                doReturn(routineTodoList).when(routineTodoRepository).findAllByRoutineAndTodoIsNotNullAndDayIn(any(Routine.class), any(List.class));

                //when
                plannerSettingService.updateRoutine(user, updateRoutineRequest);

                //then

                //verify
                verify(routineRepository, times(1)).findByIdAndUser(any(Long.class), any());
                verify(routineDayRepository, times(1)).findAllByRoutine(any(Routine.class));
            }

            @Test
            void 성공_order2_오늘이후수정() {
                //given
                final UpdateRoutineRequest updateRoutineRequest = UpdateRoutineRequest.builder()
                        .routineId(1L)
                        .order(2)
                        .routineContent("저녁운동하기")
                        .startDay("2023-12-25")
                        .endDay("2024-01-09")
                        .days(days)
                        .categoryId(0L)
                        .build();

                final DailyPlanner dailyPlanner = DailyPlanner.builder()
                        .id(1L)
                        .dailyPlannerDay("2023-12-25")
                        .user(user)
                        .build();
                final Todo todo = Todo.builder()
                        .id(1L)
                        .category(null)
                        .todoContent(routine.getRoutineContent())
                        .todoStatus(TodoStatus.EMPTY)
                        .dailyPlanner(dailyPlanner)
                        .todoIndex(100000D)
                        .build();
                final RoutineDay routineDay1 = RoutineDay.builder()
                        .id(1L)
                        .day("월")
                        .build();
                final RoutineDay routineDay2 = RoutineDay.builder()
                        .id(2L)
                        .day("수")
                        .build();
                routineDay1.setRoutine(routine);
                routineDay2.setRoutine(routine);
                final RoutineTodo routineTodo = RoutineTodo.builder()
                        .id(1L)
                        .day("월")
                        .dailyPlannerDay("2023-12-25")
                        .todo(todo)
                        .build();
                routineTodo.setRoutine(routine);

                final RoutineDay[] routineDayList = new RoutineDay[]{routineDay1, routineDay2};
                final RoutineTodo[] routineTodoList = new RoutineTodo[]{routineTodo};
                doReturn(routine).when(routineRepository).findByIdAndUser(any(Long.class), any());
                doReturn(routineDayList).when(routineDayRepository).findAllByRoutine(any(Routine.class));
                doReturn(routineTodoList).when(routineTodoRepository).findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThanAndDayIn(any(Routine.class), any(String.class), any(List.class));
                doReturn(routineTodoList).when(routineTodoRepository).findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqualAndDayIn(any(Routine.class), any(String.class), any(List.class));

                //when
                plannerSettingService.updateRoutine(user, updateRoutineRequest);

                //then

                //verify
                verify(routineRepository, times(1)).findByIdAndUser(any(Long.class), any());
                verify(routineDayRepository, times(1)).findAllByRoutine(any(Routine.class));
                verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNullAndDailyPlannerDayLessThanAndDayIn(any(Routine.class), any(String.class), any(List.class));
                verify(routineTodoRepository, times(1)).findAllByRoutineAndTodoIsNotNullAndDailyPlannerDayGreaterThanEqualAndDayIn(any(Routine.class), any(String.class), any(List.class));
            }

        }
    }
}