import React, { useEffect, useMemo, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Modal from "@components/common/Modal";
import DeleteModal from "@components/common/Modal/DeleteModal";
import MyPageList from "@components/mypage/MyPageList";
import MyPageDetail from "@components/mypage/MyPageDetail";
import Category from "@components/mypage/details/diary/Category";
import Dday from "@components/mypage/details/diary/Dday";
import Routine from "@components/mypage/details/diary/Routine";
import CategoryList from "@components/mypage/list/CategoryList";
import DdayList from "@components/mypage/list/DdayList";
import RoutineList from "@components/mypage/list/RoutineList";
import { settingApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { BASIC_CATEGORY_ITEM } from "@store/planner/daySlice";
import { CategoryItemConfig, DdayItemConfig } from "@util/planner.interface";
import {
  selectCategoryClick,
  selectCategoryColorClick,
  selectCategoryColors,
  selectCategoryInput,
  selectCategoryList,
  setCategoryClick,
  setCategoryInput,
  setCategoryList,
} from "@store/mypage/categorySlice";
import {
  selectDdayClick,
  selectDdayInput,
  selectDdayList,
  setDdayClick,
  setDdayInput,
  setDdayList,
} from "@store/mypage/ddaySlice";
import {
  BASIC_ROUTINE_INPUT,
  InitRoutineItemConfig,
  RoutineItemConfig,
  selectRoutineClick,
  selectRoutineInput,
  selectRoutineList,
  setRoutineClick,
  setRoutineInput,
  setRoutineList,
} from "@store/mypage/routineSlice";
import dayjs from "dayjs";

interface Props {
  title: string;
}

export interface EditInfoConfig {
  type: string;
  info: CategoryItemConfig | DdayItemConfig | null;
  clicked: number;
}

const MyPageFrame = ({ title }: Props) => {
  /* 카테고리 관련 변수 */
  const dispatch = useAppDispatch();
  const userId: number = useAppSelector(selectUserId);
  const categoryList: CategoryItemConfig[] = useAppSelector(selectCategoryList);
  const categoryColors = useAppSelector(selectCategoryColors);
  const categoryClick: number = useAppSelector(selectCategoryClick);
  const categoryInput: CategoryItemConfig = useAppSelector(selectCategoryInput);
  const colorClick: number = useAppSelector(selectCategoryColorClick);

  /* 디데이 관련 변수 */
  const ddayList = useAppSelector(selectDdayList);
  const ddayClick = useAppSelector(selectDdayClick);
  const ddayInput = useAppSelector(selectDdayInput);
  const copyDdays = useMemo(() => JSON.parse(JSON.stringify(ddayList)), [ddayList]);

  /* 루틴 관련 변수 */
  const routineList = useAppSelector(selectRoutineList);
  const routineClick = useAppSelector(selectRoutineClick);
  const routineInput: RoutineItemConfig = useAppSelector(selectRoutineInput);
  const [routineDayError, setRoutineDayError] = useState<boolean>(false);

  /* 공통 사용 변수 */
  const [isDisable, setIsDisable] = useState<boolean>(false);
  const [isInit, setIsInit] = useState<boolean>(false);

  /* 삭제 모달 변수 및 함수 */
  const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
  const handleDeleteModalOpen = () => {
    if (isDisable) return;
    setDeleteModalOpen(true);
  };
  const handleDeleteModalClose = () => setDeleteModalOpen(false);

  const getCategoryItem = (id: number) => {
    categoryList.map((item: CategoryItemConfig) => {
      if (item.categoryId == id) return item;
    });
    return BASIC_CATEGORY_ITEM;
  };

  const handleAdd = (title: string) => {
    if (title === "카테고리") {
      const init = {
        categoryTitle: "새 카테고리",
        categoryEmoticon: null,
        categoryColorId: 1,
      };
      settingApi
        .addCategories(userId, init)
        .then((res) => {
          const returnId = res.data.data.categoryId;
          const newCategory: CategoryItemConfig = {
            categoryId: returnId,
            categoryTitle: "새 카테고리",
            categoryEmoticon: "",
            categoryColorCode: categoryColors[0].categoryColorCode,
          };
          dispatch(setCategoryList([...categoryList, newCategory]));
          dispatch(setCategoryClick(categoryList.length));
          dispatch(setCategoryInput(newCategory));
        })
        .catch((err) => console.log(err));
    } else if (title === "디데이") {
      const init = {
        ddayDate: dayjs(new Date()).format("YYYY-MM-DD"),
        ddayTitle: "새 디데이",
      };
      settingApi
        .addDdays(userId, init)
        .then((res) => {
          const returnId = res.data.data.ddayId;
          dispatch(setDdayList([...ddayList, { ...init, ddayId: returnId }]));
          dispatch(setDdayClick(ddayList.length));
          dispatch(setDdayInput({ ...init, ddayId: returnId }));
        })
        .catch((err) => console.log(err));
    } else if (title === "루틴") {
      if (isInit) return;
      const init: InitRoutineItemConfig = {
        routineContent: "새 루틴",
        startDay: new Date(),
        endDay: new Date(),
        category: null,
        days: [],
      };
      dispatch(setRoutineList([...routineList, { ...init }]));
      dispatch(setRoutineClick(routineList.length));
      dispatch(setRoutineInput({ ...init }));
      setIsInit(true);
    }
  };

  const handleUpdate = (title: string) => {
    if (isDisable) return;
    if (title === "카테고리") {
      const input = {
        categoryId: categoryInput.categoryId,
        categoryTitle: categoryInput.categoryTitle ?? "",
        categoryEmoticon: categoryInput.categoryEmoticon ?? "",
        categoryColorId: colorClick + 1,
      };
      if (input.categoryTitle.length < 1 || input.categoryTitle.length > 10) return;
      settingApi
        .editCategories(userId, input)
        .then(() => {
          let copyList: CategoryItemConfig[] = [...categoryList];
          copyList[categoryClick] = { ...input, categoryColorCode: categoryColors[colorClick].categoryColorCode };
          dispatch(setCategoryList(copyList));
        })
        .catch((err) => console.log(err));
    } else if (title === "디데이") {
      const input = {
        ddayId: ddayInput.ddayId,
        ddayTitle: ddayInput.ddayTitle,
        ddayDate: dayjs(ddayInput.ddayDate).format("YYYY-MM-DD"),
      };
      if (input.ddayTitle.length < 1 || input.ddayTitle.length > 20) return;
      settingApi
        .editDdays(userId, input)
        .then(() => {
          copyDdays[ddayClick] = { ...input };
          dispatch(setDdayList(copyDdays));
        })
        .catch((err) => console.log(err));
    } else if (title === "루틴") {
      const { routineContent, category, startDay, endDay, days } = routineInput;
      if (days.length < 1) {
        setRoutineDayError(true);
        return;
      } else setRoutineDayError(false);
      if (routineContent.length < 1 || routineContent.length > 50) return;

      const input = {
        startDay: dayjs(startDay).format("YYYY-MM-DD"),
        endDay: dayjs(endDay).format("YYYY-MM-DD"),
        categoryId: category ? category.categoryId : BASIC_CATEGORY_ITEM.categoryId,
        routineContent,
        days,
      };

      // 등록과 수정 구분
      if (isInit) {
        settingApi
          .addRoutines(userId, input)
          .then((res) => {
            const routineId = res.data.data.routineId;
            const category = input.categoryId === 0 ? BASIC_CATEGORY_ITEM : getCategoryItem(input.categoryId);
            const newRoutine: RoutineItemConfig = { ...input, category, routineId };
            let copyList = [...routineList];
            copyList[routineClick] = newRoutine;
            dispatch(setRoutineList(copyList));
          })
          .then(() => {
            setIsInit(false);
          })
          .catch((err) => console.log(err));
      }
    }
  };

  const handleDelete = (title: string) => {
    if (title === "카테고리") {
      if (categoryList.length == 0) return;
      settingApi
        .deleteCategories(userId, { categoryId: categoryList[categoryClick].categoryId })
        .then((res) => {
          dispatch(
            setCategoryList(
              categoryList.filter((item, idx) => {
                return idx !== categoryClick;
              }),
            ),
          );
          // 삭제한 값의 위 (0인 경우 아래) 배열 항목으로 재설정
          dispatch(setCategoryClick(categoryClick === 0 ? categoryClick : categoryClick - 1));
        })
        .catch((err) => {
          console.log(err);
        })
        .finally(() => handleDeleteModalClose());
    } else if (title === "디데이") {
      if (ddayList.length == 0) return;
      settingApi
        .deleteDdays(userId, ddayList[ddayClick].ddayId)
        .then(() => {
          dispatch(
            setDdayList(
              ddayList.filter((_: DdayItemConfig, idx: number) => {
                return idx !== ddayClick;
              }),
            ),
          );
          // 삭제한 값의 위 (0인 경우 아래) 배열 항목으로 재설정
          dispatch(setDdayClick(ddayClick === 0 ? ddayClick : ddayClick - 1));
        })
        .catch((err) => console.log(err))
        .finally(() => handleDeleteModalClose());
    } else if (title === "루틴") {
      // 작성 예정
    }
  };

  useEffect(() => {
    if (title === "카테고리" && categoryList.length < 1) setIsDisable(true);
    else if (title === "디데이" && ddayList.length < 1) setIsDisable(true);
    else if (title === "루틴" && routineList.length < 1) setIsDisable(true);
    else setIsDisable(false);
  }, [title, categoryList, ddayList, routineList]);

  return (
    <div className={styles["frame"]}>
      <MyPageList handleAdd={handleAdd} title={title} isInit={isInit}>
        {
          {
            카테고리: <CategoryList />,
            디데이: <DdayList />,
            루틴: <RoutineList />,
          }[title]
        }
      </MyPageList>
      <MyPageDetail
        title={title}
        isDisable={isDisable}
        isInit={isInit}
        handleUpdate={handleUpdate}
        handleDelete={handleDeleteModalOpen}
      >
        <>
          {
            {
              카테고리: categoryList.length != 0 && <Category />,
              디데이: ddayList.length != 0 && <Dday />,
              루틴: routineList.length != 0 && <Routine dayError={routineDayError} />,
            }[title]
          }
        </>
      </MyPageDetail>
      <Modal
        types="twoBtn"
        open={deleteModalOpen}
        onClose={handleDeleteModalClose}
        onClick={() => handleDelete(title)}
        onClickMessage="삭제"
        warning
      >
        <DeleteModal types={title} />
      </Modal>
    </div>
  );
};

export default MyPageFrame;
