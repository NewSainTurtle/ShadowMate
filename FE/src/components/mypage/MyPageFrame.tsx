import React, { ChangeEvent, useEffect, useMemo, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import DeleteModal from "@components/common/Modal/DeleteModal";
import RoutineUpdateModal from "@components/common/Modal/RoutineUpdateModal";
import RadioButton from "@components/common/RadioButton";
import MyPageList from "@components/mypage/MyPageList";
import MyPageDetail from "@components/mypage/MyPageDetail";
import Category from "@components/mypage/details/diary/Category";
import CategoryList from "@components/mypage/list/CategoryList";
import Dday from "@components/mypage/details/diary/Dday";
import DdayList from "@components/mypage/list/DdayList";
import Routine from "@components/mypage/details/diary/Routine";
import RoutineList from "@components/mypage/list/RoutineList";
import { FormControlLabel, RadioGroup, Stack } from "@mui/material";
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
  InitRoutineItemConfig,
  RoutineItemConfig,
  selectRoutineClick,
  selectRoutineInput,
  selectRoutineIsInit,
  selectRoutineList,
  setRoutineClick,
  setRoutineInput,
  setRoutineIsInit,
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

interface RoutineUpdateSelectorConfig {
  types: "수정" | "삭제";
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
  const routineIsInit = useAppSelector(selectRoutineIsInit);
  const [routineDayError, setRoutineDayError] = useState<boolean>(false);

  const [order, setOrder] = useState<"1" | "2" | "3">("1");
  const [updateModal, setUpdateModal] = useState(false);
  const handleUpdateModalOpen = () => setUpdateModal(true);
  const handleUpdateModalClose = () => setUpdateModal(false);

  /* 공통 사용 변수 */
  const [isDisable, setIsDisable] = useState<boolean>(false);

  /* 삭제 모달 변수 및 함수 */
  const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
  const handleDeleteModalOpen = () => {
    if (isDisable) return;
    setDeleteModalOpen(true);
  };
  const handleDeleteModalClose = () => setDeleteModalOpen(false);

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
      if (routineIsInit) return;
      dispatch(setRoutineIsInit(true));
      const init: InitRoutineItemConfig = {
        routineContent: "새 루틴",
        startDay: new Date(),
        endDay: new Date(),
        category: null,
        days: [],
      };
      dispatch(setRoutineList([...routineList, init]));
      dispatch(setRoutineClick(routineList.length));
      dispatch(setRoutineInput({ ...init }));
    }
  };

  const handleUpdate = async (title: string) => {
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
      if (routineIsInit) {
        settingApi
          .addRoutines(userId, input)
          .then((res) => {
            const routineId = res.data.data.routineId;
            const { categoryId, ...rest } = input;
            let copyList = [...routineList].slice(0, routineList.length - 1);
            dispatch(setRoutineList([...copyList, { ...rest, routineId, category }]));
          })
          .then(() => dispatch(setRoutineIsInit(false)))
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

  const onChangeRadio = (e: ChangeEvent<HTMLInputElement>) => {
    const order = e.target.value;
    if (order === "1" || order === "2" || order === "3") {
      setOrder(order);
    }
  };

  const RoutineUpdateSelector = ({ types }: RoutineUpdateSelectorConfig) => {
    return (
      <div className={styles["radio"]}>
        <Stack direction="row" spacing={1} alignItems="center">
          <RadioGroup value={order} onChange={onChangeRadio}>
            <FormControlLabel value="1" control={<RadioButton />} label={<Text types="small">모두 {types}하기</Text>} />
            <FormControlLabel
              value="2"
              control={<RadioButton />}
              label={<Text types="small">오늘 이후 루틴을 모두 {types}하기</Text>}
            />
            {types === "삭제" && (
              <FormControlLabel value="3" control={<RadioButton />} label={<Text types="small">삭제하지 않기</Text>} />
            )}
          </RadioGroup>
        </Stack>
      </div>
    );
  };

  const handleDeleteInit = () => {
    dispatch(
      setRoutineList(
        routineList.filter((_: RoutineItemConfig, idx: number) => {
          return idx !== routineClick;
        }),
      ),
    );
    dispatch(setRoutineClick(0));
    dispatch(setRoutineInput(routineList[0]));
    dispatch(setRoutineIsInit(false));
    setRoutineDayError(false);
  };

  const getRoutines = () => {
    settingApi
      .routines(userId)
      .then((res) => {
        const response = res.data.data.routineList;
        dispatch(setRoutineList(response));
        dispatch(setRoutineClick(0));
        dispatch(setRoutineInput(response[0]));
      })
      .catch((err) => console.log(err));
  };

  useEffect(() => {
    if (title === "카테고리" && categoryList.length < 1) setIsDisable(true);
    else if (title === "디데이" && ddayList.length < 1) setIsDisable(true);
    else if (title === "루틴" && routineList.length < 1) setIsDisable(true);
    else setIsDisable(false);
  }, [title, categoryList, ddayList, routineList]);

  useEffect(() => {
    // 임의 생성한 루틴이 있는 경우에서(routineIsInit), 카테고리/디데이 페이지로 이동하면 삭제.
    dispatch(setRoutineClick(0));
    if (routineIsInit) handleDeleteInit();
  }, [title]);

  useEffect(() => {
    getRoutines();
    return () => {
      // 임의 생성한 루틴이 있는 경우에서(routineIsInit), 다른 페이지로 이동하면 삭제.
      if (routineIsInit) handleDeleteInit();
    };
  }, []);

  return (
    <div className={styles["frame"]}>
      <MyPageList handleAdd={handleAdd} title={title}>
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
        handleUpdate={title === "루틴" ? handleUpdateModalOpen : handleUpdate}
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
        {title === "루틴" ? (
          <RoutineUpdateModal types="삭제">
            <RoutineUpdateSelector types="삭제" />
          </RoutineUpdateModal>
        ) : (
          <DeleteModal types={title} />
        )}
      </Modal>
      <Modal
        types="twoBtn"
        open={updateModal}
        onClose={handleUpdateModalClose}
        onClickMessage="저장"
        onClick={() => handleUpdate(title)}
      >
        <RoutineUpdateModal types={"수정"}>
          <RoutineUpdateSelector types="수정" />
        </RoutineUpdateModal>
      </Modal>
    </div>
  );
};

export default MyPageFrame;
