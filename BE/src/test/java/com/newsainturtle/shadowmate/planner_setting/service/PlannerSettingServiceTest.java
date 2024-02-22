package com.newsainturtle.shadowmate.planner_setting.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
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
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            @Test
            void 성공() {
                //given

                //when
                plannerSettingService.removeCategory(user, category, 0);

                //then

                //verify
                verify(categoryRepository, times(1)).deleteByUserAndId(any(), any(Long.class));
            }
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
        class 디데이목록조회 {
            @Test
            void 등록된디데이목록이없음() {
                //given
                doReturn(new ArrayList<>()).when(ddayRepository).findByUserOrderByDdayDateDesc(user);

                //when
                final GetDdayListResponse ddayListResponse = plannerSettingService.getDdayList(user);

                //then
                assertThat(ddayListResponse.getDdayList()).isNotNull().isEmpty();
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
                assertThat(ddayListResponse.getDdayList()).isNotNull().hasSize(2);
            }
        }

        @Nested
        class 디데이조회 {
            final String today = LocalDate.now().toString();

            @Test
            void 디데이없음() {
                //given
                doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(user, today);
                doReturn(null).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(user, today);

                //when
                final Dday dday = plannerSettingService.getDday(user);

                //then
                assertThat(dday).isNotNull();
                assertThat(dday.getDdayTitle()).isNull();
                assertThat(dday.getDdayDate()).isNull();
            }

            @Test
            void 디데이있음_현재미래() {
                //given
                final String future = String.valueOf(LocalDate.now().plusDays(1));
                doReturn(Dday.builder()
                        .ddayTitle("시험")
                        .ddayDate(future)
                        .user(user)
                        .build()).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(user, today);

                //when
                final Dday dday = plannerSettingService.getDday(user);

                //then
                assertThat(dday).isNotNull();
                assertThat(dday.getDdayTitle()).isEqualTo("시험");
                assertThat(dday.getDdayDate()).isEqualTo(future);
            }

            @Test
            void 디데이있음_과거() {
                //given
                doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(user, today);
                doReturn(Dday.builder()
                        .ddayTitle("크리스마스")
                        .ddayDate("2023-12-25")
                        .user(user)
                        .build()).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(user, today);

                //when
                final Dday dday = plannerSettingService.getDday(user);

                //then
                assertThat(dday).isNotNull();
                assertThat(dday.getDdayTitle()).isEqualTo("크리스마스");
                assertThat(dday.getDdayDate()).isEqualTo("2023-12-25");
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
}