import React, { ChangeEvent, useEffect, useLayoutEffect, useMemo, useState } from "react";
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
import NewItemContainer from "@components/common/Modal/mypage/NewItemContainer";
import { FormControlLabel, RadioGroup, Stack } from "@mui/material";
import { settingApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { BASIC_CATEGORY_ITEM } from "@store/planner/daySlice";
import { CategoryColorConfig, CategoryItemConfig, DdayItemConfig } from "@util/planner.interface";
import {
  BASIC_CATEGORY_INPUT,
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
  BASIC_DDAY_INPUT,
  selectDdayClick,
  selectDdayInput,
  selectDdayList,
  setDdayClick,
  setDdayInput,
  setDdayList,
} from "@store/mypage/ddaySlice";
import {
  BASIC_ROUTINE_INPUT,
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

interface RoutineUpdateSelectorConfig {
  types: "수정" | "삭제";
  order: string;
  onChangeRadio: (order: string) => void;
}

export interface RoutineErrorConfig {
  lengthError: boolean;
  dayError: boolean;
}

const RoutineUpdateSelector = ({ types, order, onChangeRadio }: RoutineUpdateSelectorConfig) => {
  return (
    <div className={styles["radio"]}>
      <Stack direction="row" spacing={1} alignItems="center">
        <RadioGroup value={order} onChange={(e) => onChangeRadio(e.target.value)}>
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

const MyPageFrame = ({ title }: Props) => {
  const dispatch = useAppDispatch();
  const userId: number = useAppSelector(selectUserId);

  /* 카테고리 관련 변수 */
  const categoryList: CategoryItemConfig[] = useAppSelector(selectCategoryList);
  const categoryColors: CategoryColorConfig[] = useAppSelector(selectCategoryColors);
  const categoryClick: number = useAppSelector(selectCategoryClick);
  const categoryInput: CategoryItemConfig = useAppSelector(selectCategoryInput);
  const colorClick: number = useAppSelector(selectCategoryColorClick);

  /* 디데이 관련 변수 */
  const ddayList: DdayItemConfig[] = useAppSelector(selectDdayList);
  const ddayClick: number = useAppSelector(selectDdayClick);
  const ddayInput: DdayItemConfig = useAppSelector(selectDdayInput);
  const copyDdays: DdayItemConfig[] = useMemo(() => JSON.parse(JSON.stringify(ddayList)), [ddayList]);

  /* 루틴 관련 변수 */
  const routineList: RoutineItemConfig[] = useAppSelector(selectRoutineList);
  const routineClick: number = useAppSelector(selectRoutineClick);
  const routineInput: RoutineItemConfig = useAppSelector(selectRoutineInput);
  const [routineError, setRoutineError] = useState({
    lengthError: false,
    dayError: false,
  });

  const [order, setOrder] = useState<"1" | "2" | "3">("1");
  const [updateModal, setUpdateModal] = useState(false);
  const handleUpdateModalOpen = () => setUpdateModal(true);
  const handleUpdateModalClose = () => setUpdateModal(false);

  const [newItemModal, setNewItemModal] = useState(false);
  const handleNewItemModalOpen = () => setNewItemModal(true);
  const handleNewItemModalClose = () => setNewItemModal(false);

  /* 공통 사용 변수 */
  const [isDisable, setIsDisable] = useState<boolean>(false);

  /* 삭제 모달 변수 및 함수 */
  const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
  const handleDeleteModalOpen = () => {
    if (isDisable) return;
    setDeleteModalOpen(true);
  };
  const handleDeleteModalClose = () => setDeleteModalOpen(false);

  const handleNewItem = () => {
    if (title === "카테고리") {
      dispatch(setCategoryInput(BASIC_CATEGORY_INPUT));
    } else if (title === "디데이") {
      dispatch(setDdayInput(BASIC_DDAY_INPUT));
    } else if (title === "루틴") {
      dispatch(setRoutineInput(BASIC_ROUTINE_INPUT));
      setRoutineError({ lengthError: false, dayError: false });
    }
    handleNewItemModalOpen();
  };

  const handleAddCategory = () => {
    const { categoryTitle, categoryEmoticon } = categoryInput;
    if (categoryTitle.length < 1 || categoryTitle.length > 10) return;
    const data = {
      categoryTitle,
      categoryEmoticon: categoryEmoticon ?? "",
      categoryColorId: colorClick + 1,
    };
    settingApi
      .addCategories(userId, data)
      .then((res) => {
        const returnId = res.data.data.categoryId;
        const { categoryColorId, ...rest } = data;
        const newCategory = {
          ...rest,
          categoryId: returnId,
          categoryColorCode: categoryColors[colorClick].categoryColorCode,
        };
        dispatch(setCategoryList([...categoryList, newCategory]));
        dispatch(setCategoryClick(categoryList.length));
        dispatch(setCategoryInput(newCategory));
      })
      .then(() => handleNewItemModalClose())
      .catch((err) => console.log(err));
  };

  const handleUpdateCategory = () => {
    const { categoryId, categoryTitle, categoryEmoticon } = categoryInput;
    if (categoryTitle.length < 1 || categoryTitle.length > 10) return;
    const data = {
      categoryId,
      categoryTitle,
      categoryEmoticon: categoryEmoticon ?? "",
      categoryColorId: colorClick + 1,
    };
    settingApi
      .editCategories(userId, data)
      .then(() => {
        let copyList: CategoryItemConfig[] = [...categoryList];
        copyList[categoryClick] = { ...data, categoryColorCode: categoryColors[colorClick].categoryColorCode };
        dispatch(setCategoryList(copyList));
      })
      .catch((err) => console.log(err));
  };

  const handleDeleteCategory = () => {
    const categoryId = categoryList[categoryClick].categoryId;
    settingApi
      .deleteCategories(userId, { categoryId })
      .then(() => {
        dispatch(
          setCategoryList(
            categoryList.filter((_, idx) => {
              return idx !== categoryClick;
            }),
          ),
        );
        // 삭제한 값의 위 (0인 경우 아래) 배열 항목으로 재설정
        dispatch(setCategoryClick(categoryClick === 0 ? categoryClick : categoryClick - 1));
      })
      .then(() => handleDeleteModalClose())
      .catch((err) => {
        console.log(err);
      });
  };

  const handleAddDday = () => {
    const { ddayTitle, ddayDate } = ddayInput;
    const data = {
      ddayTitle,
      ddayDate: dayjs(ddayDate).format("YYYY-MM-DD"),
    };
    settingApi
      .addDdays(userId, data)
      .then((res) => {
        const returnId = res.data.data.ddayId;
        const newDday = { ...data, ddayId: returnId };
        dispatch(setDdayList([...ddayList, newDday]));
        dispatch(setDdayClick(ddayList.length));
        dispatch(setDdayInput(newDday));
      })
      .then(() => handleNewItemModalClose())
      .catch((err) => console.log(err));
  };

  const handleUpdateDday = () => {
    const { ddayId, ddayTitle, ddayDate } = ddayInput;
    if (ddayTitle.length < 1 || ddayTitle.length > 20) return;
    const data = {
      ddayId,
      ddayTitle,
      ddayDate: dayjs(ddayDate).format("YYYY-MM-DD"),
    };
    settingApi
      .editDdays(userId, data)
      .then(() => {
        copyDdays[ddayClick] = { ...data };
        dispatch(setDdayList(copyDdays));
      })
      .catch((err) => console.log(err));
  };

  const handleDeleteDday = () => {
    const ddayId = ddayList[ddayClick].ddayId;
    settingApi
      .deleteDdays(userId, { ddayId })
      .then(() => {
        dispatch(
          setDdayList(
            ddayList.filter((_, idx) => {
              return idx !== ddayClick;
            }),
          ),
        );
        // 삭제한 값의 위 (0인 경우 아래) 배열 항목으로 재설정
        dispatch(setDdayClick(ddayClick === 0 ? ddayClick : ddayClick - 1));
      })
      .then(() => handleDeleteModalClose())
      .catch((err) => console.log(err));
  };

  const handleAddRoutine = () => {
    const { routineContent, category, startDay, endDay, days } = routineInput;
    if (routineContent.length < 1 || routineContent.length > 50) return;
    const input = {
      startDay: dayjs(startDay).format("YYYY-MM-DD"),
      endDay: dayjs(endDay).format("YYYY-MM-DD"),
      categoryId: category ? category.categoryId : BASIC_CATEGORY_ITEM.categoryId,
      routineContent,
      days,
    };
    settingApi
      .addRoutines(userId, input)
      .then((res) => {
        const routineId = res.data.data.routineId;
        const { categoryId, ...rest } = input;

        const newItem = { ...rest, routineId, category };
        dispatch(setRoutineList([...routineList, newItem]));
        dispatch(setRoutineClick(routineList.length));
        dispatch(setRoutineInput(newItem));
      })
      .then(() => {
        handleNewItemModalClose();
        setRoutineError({ lengthError: false, dayError: false });
      })
      .catch((err) => console.log(err));
  };

  const handleUpdateRoutine = () => {
    const { routineId, routineContent, category, startDay, endDay, days } = routineInput;
    const updateInput = {
      routineId,
      order: parseInt(order),
      startDay: dayjs(startDay).format("YYYY-MM-DD"),
      endDay: dayjs(endDay).format("YYYY-MM-DD"),
      categoryId: category ? category.categoryId : BASIC_CATEGORY_ITEM.categoryId,
      routineContent,
      days,
    };
    settingApi
      .editRoutines(userId, updateInput)
      .then(() => {
        let copyList: RoutineItemConfig[] = [...routineList];
        copyList[routineClick] = { ...routineInput };
        dispatch(setRoutineList(copyList));
      })
      .then(() => handleUpdateModalClose())
      .catch((err) => console.log(err));
  };

  const handleDeleteRoutine = () => {
    const routineId = routineList[routineClick].routineId;
    settingApi
      .deleteRoutines(userId, { routineId, order: parseInt(order) })
      .then(() => {
        dispatch(
          setRoutineList(
            routineList.filter((_: RoutineItemConfig, idx: number) => {
              return idx !== routineClick;
            }),
          ),
        );
        const nextClick = routineClick === 0 ? routineClick : routineClick - 1;
        dispatch(setRoutineClick(nextClick));
        dispatch(setRoutineInput(routineList[nextClick]));
      })
      .then(() => handleDeleteModalClose())
      .catch((err) => console.log(err));
  };

  const handleAdd = () => {
    switch (title) {
      case "카테고리":
        return handleAddCategory();
      case "디데이":
        return handleAddDday();
      case "루틴":
        if (routineInput.routineContent.length < 1 || routineInput.routineContent.length > 50) {
          setRoutineError({ ...routineError, lengthError: true });
          return;
        }
        if (routineInput.days.length < 1) {
          setRoutineError({ ...routineError, dayError: true });
          return;
        }
        return handleAddRoutine();
    }
  };

  const handleUpdate = () => {
    switch (title) {
      case "카테고리":
        return handleUpdateCategory();
      case "디데이":
        return handleUpdateDday();
      case "루틴":
        if (routineInput.routineContent.length < 1 || routineInput.routineContent.length > 50) {
          setRoutineError({ ...routineError, lengthError: true });
          return;
        }
        if (routineInput.days.length < 1) {
          setRoutineError({ ...routineError, dayError: true });
          return;
        }
        return handleUpdateModalOpen();
    }
  };

  const handleDelete = () => {
    switch (title) {
      case "카테고리":
        return handleDeleteCategory();
      case "디데이":
        return handleDeleteDday();
      case "루틴":
        return handleDeleteRoutine();
    }
  };

  const onChangeRadio = (order: string) => {
    if (order === "1" || order === "2" || order === "3") {
      setOrder(order);
    }
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

  useLayoutEffect(() => {
    getRoutines();
  }, []);

  return (
    <div className={styles["frame"]}>
      <MyPageList handleAdd={handleNewItem} title={title}>
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
        handleUpdate={handleUpdate}
        handleDelete={handleDeleteModalOpen}
      >
        {!newItemModal && (
          <>
            {
              {
                카테고리: categoryList.length != 0 && <Category newItem={false} />,
                디데이: ddayList.length != 0 && <Dday newItem={false} />,
                루틴: routineList.length != 0 && (
                  <Routine newItem={false} routineError={routineError} setRoutineError={setRoutineError} />
                ),
              }[title]
            }
          </>
        )}
      </MyPageDetail>
      <Modal
        types="twoBtn"
        open={deleteModalOpen}
        onClose={handleDeleteModalClose}
        onClick={handleDelete}
        onClickMessage="삭제"
        warning
      >
        {title === "루틴" ? (
          <RoutineUpdateModal types="삭제">
            <RoutineUpdateSelector types="삭제" order={order} onChangeRadio={onChangeRadio} />
          </RoutineUpdateModal>
        ) : (
          <DeleteModal types={title} />
        )}
      </Modal>
      <Modal
        types="twoBtn"
        open={updateModal}
        onClose={handleUpdateModalClose}
        onClick={handleUpdateRoutine}
        onClickMessage="수정"
      >
        <RoutineUpdateModal types="수정">
          <RoutineUpdateSelector types="수정" order={order} onChangeRadio={onChangeRadio} />
        </RoutineUpdateModal>
      </Modal>
      <Modal
        types="twoBtn"
        open={newItemModal}
        onClose={handleNewItemModalClose}
        onClickMessage="저장"
        onClick={handleAdd}
      >
        <NewItemContainer title={title} routineError={routineError} setRoutineError={setRoutineError} />
      </Modal>
    </div>
  );
};

export default MyPageFrame;
