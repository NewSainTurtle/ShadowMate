import React, { useEffect, useMemo, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Modal from "@components/common/Modal";
import DeleteModal from "@components/common/Modal/DeleteModal";
import MyPageList from "@components/mypage/MyPageList";
import MyPageDetail from "@components/mypage/MyPageDetail";
import MyPageCategory from "@components/mypage/details/diary/Category";
import CategoryList from "@components/mypage/list/CategoryList";
import MyPageDday from "./details/diary/Dday";
import DdayList from "@components/mypage/list/DdayList";
import { CategoryItemConfig, DdayItemConfig } from "@util/planner.interface";
import { settingApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
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

  /* 공통 사용 변수 */
  const [isDisable, setIsDisable] = useState<boolean>(false);

  /* 삭제 모달 변수 및 함수 */
  const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
  const handleDeleteModalOpen = () => setDeleteModalOpen(true);
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
    } else {
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
        .then((res) => {
          let copyList: CategoryItemConfig[] = [...categoryList];
          copyList[categoryClick] = {
            categoryId: input.categoryId,
            categoryTitle: input.categoryTitle,
            categoryEmoticon: input.categoryEmoticon,
            categoryColorCode: categoryColors[colorClick].categoryColorCode,
          };
          dispatch(setCategoryList(copyList));
        })
        .catch((err) => console.log(err));
    } else {
      const input = {
        ddayId: ddayInput.ddayId,
        ddayTitle: ddayInput.ddayTitle,
        ddayDate: dayjs(ddayInput.ddayDate).format("YYYY-MM-DD"),
      };
      if (input.ddayTitle.length < 1 || input.ddayTitle.length > 20) return;
      settingApi
        .editDdays(userId, input)
        .then((res) => {
          copyDdays[ddayClick] = { ...input };
          dispatch(setDdayList(copyDdays));
        })
        .catch((err) => console.log(err));
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
    } else {
      if (ddayList.length == 0) return;
      settingApi
        .deleteDdays(userId, ddayList[ddayClick].ddayId)
        .then(() => {
          dispatch(
            setDdayList(
              ddayList.filter((item: DdayItemConfig, idx: number) => {
                return idx !== ddayClick;
              }),
            ),
          );
          // 삭제한 값의 위 (0인 경우 아래) 배열 항목으로 재설정
          dispatch(setDdayClick(ddayClick === 0 ? ddayClick : ddayClick - 1));
        })
        .catch((err) => console.log(err))
        .finally(() => handleDeleteModalClose());
    }
  };

  useEffect(() => {
    if (title === "카테고리" && categoryList.length < 1) setIsDisable(true);
    else if (title === "디데이" && ddayList.length < 1) setIsDisable(true);
    else setIsDisable(false);
  }, [title, categoryList, ddayList]);

  return (
    <div className={styles["frame"]}>
      <MyPageList handleAdd={handleAdd} title={title}>
        {
          {
            카테고리: <CategoryList />,
            디데이: <DdayList />,
          }[title]
        }
      </MyPageList>
      <MyPageDetail
        title={title}
        isDisable={isDisable}
        handleUpdate={handleUpdate}
        handleDelete={handleDeleteModalOpen}
      >
        <>
          {
            {
              카테고리: categoryList.length != 0 && <MyPageCategory />,
              디데이: ddayList.length != 0 && <MyPageDday />,
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
