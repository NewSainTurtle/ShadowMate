package com.newsainturtle.shadowmate.yn.planner_setting;

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

    final User user = User.builder()
            .email("test@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @Nested
    class 카테고리 {
        @Nested
        class 카테고리등록 {
            final CategoryColor categoryColor = CategoryColor.builder()
                    .categoryColorCode("D9B5D9")
                    .build();
            final AddCategoryRequest request = AddCategoryRequest.builder()
                    .categoryTitle("국어")
                    .categoryEmoticon("🍅")
                    .categoryColorId(1L)
                    .build();
            final Category category = Category.builder()
                    .id(1L)
                    .categoryTitle(request.getCategoryTitle())
                    .categoryEmoticon(request.getCategoryEmoticon())
                    .categoryRemove(false)
                    .categoryColor(categoryColor)
                    .user(user)
                    .build();

            @Test
            void 실패_없는카테고리색상() {
                //given
                doReturn(Optional.empty()).when(categoryColorRepository).findById(request.getCategoryColorId());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.addCategory(user, request));

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

                doReturn(Optional.of(categoryColor)).when(categoryColorRepository).findById(request.getCategoryColorId());
                doReturn(category).when(categoryRepository).save(any(Category.class));

                //when
                final AddCategoryResponse saveCategory = plannerSettingService.addCategory(user, addCategoryRequest);

                //then
                assertThat(saveCategory.getCategoryId()).isNotNull();
                assertThat(saveCategory.getCategoryId()).isEqualTo(1L);

                //verify
                verify(categoryColorRepository, times(1)).findById(request.getCategoryColorId());
                verify(categoryRepository, times(1)).save(any(Category.class));
            }

            @Test
            void 성공_카테고리등록_이모티콘있음() {
                //given
                doReturn(Optional.of(categoryColor)).when(categoryColorRepository).findById(request.getCategoryColorId());
                doReturn(category).when(categoryRepository).save(any(Category.class));

                //when
                final AddCategoryResponse saveCategory = plannerSettingService.addCategory(user, request);

                //then
                assertThat(saveCategory.getCategoryId()).isNotNull();
                assertThat(saveCategory.getCategoryId()).isEqualTo(1L);

                //verify
                verify(categoryColorRepository, times(1)).findById(request.getCategoryColorId());
                verify(categoryRepository, times(1)).save(any(Category.class));
            }
        }

        @Nested
        class 카테고리수정 {
            final CategoryColor categoryColor = CategoryColor.builder()
                    .categoryColorCode("D9B5D9")
                    .build();
            final Category category = Category.builder()
                    .categoryTitle("수학")
                    .categoryEmoticon("🌀")
                    .categoryRemove(false)
                    .categoryColor(categoryColor)
                    .user(user)
                    .build();
            final UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                    .categoryId(1L)
                    .categoryTitle("국어")
                    .categoryEmoticon("🍅")
                    .categoryColorId(1L)
                    .build();

            @Test
            void 실패_없는카테고리() {
                //given
                doReturn(null).when(categoryRepository).findByUserAndId(user, request.getCategoryId());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateCategory(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
            }

            @Test
            void 실패_없는카테고리색상() {
                //given
                doReturn(category).when(categoryRepository).findByUserAndId(user, request.getCategoryId());
                doReturn(Optional.empty()).when(categoryColorRepository).findById(request.getCategoryColorId());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateCategory(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY_COLOR);
            }

            @Test
            void 성공_카테고리등록() {
                //given
                doReturn(category).when(categoryRepository).findByUserAndId(user, request.getCategoryId());
                doReturn(Optional.of(categoryColor)).when(categoryColorRepository).findById(request.getCategoryColorId());

                //when
                plannerSettingService.updateCategory(user, request);

                //then

                //verify
                verify(categoryColorRepository, times(1)).findById(request.getCategoryColorId());
                verify(categoryColorRepository, times(1)).findById(request.getCategoryColorId());
                verify(categoryRepository, times(1)).save(any(Category.class));
            }
        }

        @Nested
        class 카테고리설정_조회 {
            final CategoryColor categoryColor = CategoryColor.builder()
                    .categoryColorCode("D9B5D9")
                    .build();
            final Long userId = 1L;
            final Category category = Category.builder()
                    .categoryColor(categoryColor)
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build();

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
            final CategoryColor categoryColor = CategoryColor.builder()
                    .categoryColorCode("D9B5D9")
                    .build();
            final Category category = Category.builder()
                    .categoryTitle("수학")
                    .categoryEmoticon("🌀")
                    .categoryRemove(false)
                    .categoryColor(categoryColor)
                    .user(user)
                    .build();
            final RemoveCategoryRequest request = RemoveCategoryRequest.builder()
                    .categoryId(1L)
                    .build();

            @Test
            void 실패_없는카테고리() {
                //given
                doReturn(null).when(categoryRepository).findByUserAndId(user, request.getCategoryId());

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.removeCategory(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_CATEGORY);
            }


            @Test
            void 성공_카테고리_할일에없음() {
                //given
                doReturn(category).when(categoryRepository).findByUserAndId(user, request.getCategoryId());
                doReturn(0L).when(todoRepository).countByCategory(category);

                //when
                plannerSettingService.removeCategory(user, request);

                //then

                //verify
                verify(categoryRepository, times(1)).findByUserAndId(any(), any(Long.class));
                verify(todoRepository, times(1)).countByCategory(any(Category.class));
                verify(categoryRepository, times(1)).deleteByUserAndId(any(), any(Long.class));
            }

            @Test
            void 성공_카테고리_할일에있음() {
                //given
                doReturn(category).when(categoryRepository).findByUserAndId(user, request.getCategoryId());
                doReturn(1L).when(todoRepository).countByCategory(category);

                //when
                plannerSettingService.removeCategory(user, request);

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
            final Long userId = 1L;
            final SetAccessScopeRequest request = SetAccessScopeRequest.builder()
                    .plannerAccessScope("잘못된범위값")
                    .build();

            //when
            final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.setAccessScope(user, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_PLANNER_ACCESS_SCOPE);
        }

        @Test
        void 성공_플래너공개여부설정() {
            //given
            final Long userId = 1L;
            final SetAccessScopeRequest request = SetAccessScopeRequest.builder()
                    .plannerAccessScope("비공개")
                    .build();

            //when
            plannerSettingService.setAccessScope(user, request);

            //then
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    class 디데이 {
        @Test
        void 디데이등록_성공() {
            //given
            final AddDdayRequest addDdayRequest = AddDdayRequest.builder()
                    .ddayTitle("생일")
                    .ddayDate("2023-02-09")
                    .build();
            final Dday dday = Dday.builder()
                    .id(1L)
                    .ddayTitle("생일")
                    .ddayDate(Date.valueOf("2023-02-09"))
                    .user(user)
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
                assertThat(ddayListResponse.getDdayList().size()).isEqualTo(0);
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
                list.add(Dday.builder()
                        .ddayTitle("생일")
                        .ddayDate(Date.valueOf("2023-02-09"))
                        .user(user)
                        .build());
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
            final User user = User.builder()
                    .email("test@test.com")
                    .password("123456")
                    .socialLogin(SocialType.BASIC)
                    .nickname("거북이")
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .withdrawal(false)
                    .build();
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
            final UpdateDdayRequest request = UpdateDdayRequest.builder()
                    .ddayId(1L)
                    .ddayTitle("시험")
                    .ddayDate("2023-09-18")
                    .build();
            final Dday dday = Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(Date.valueOf("2023-02-09"))
                    .user(user)
                    .build();

            @Test
            void 실패_유효하지않은디데이_권한이없는디데이() {
                //given
                doReturn(null).when(ddayRepository).findByUserAndId(user, 1L);

                //when
                final PlannerSettingException result = assertThrows(PlannerSettingException.class, () -> plannerSettingService.updateDday(user, request));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerSettingErrorResult.INVALID_DDAY);
            }

            @Test
            void 성공() {
                //given
                doReturn(dday).when(ddayRepository).findByUserAndId(user, 1L);

                //when
                plannerSettingService.updateDday(user, request);

                //then

                //verify
                verify(ddayRepository, times(1)).findByUserAndId(user, 1L);
                verify(ddayRepository, times(1)).save(any(Dday.class));
            }
        }
    }
}