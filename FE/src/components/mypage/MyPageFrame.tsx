import React, { useEffect, useRef, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import MyPageList from "./MyPageList";
import MyPageDetail from "./MyPageDetail";
import MyPageCategory from "./details/diary/Category";
import CategoryList from "@components/mypage/list/CategoryList";
import MyPageDday from "./details/diary/Dday";
import { CategoryConfig, DdayConfig } from "@util/planner.interface";
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
} from "@store/mypageSlice";
import DdayList from "./list/DdayList";

interface Props {
  title: string;
}

export interface EditInfoConfig {
  type: string;
  info: CategoryConfig | DdayConfig | null;
  clicked: number;
}

const MyPageFrame = ({ title }: Props) => {
  /* 카테고리 관련 변수 */
  const dispatch = useAppDispatch();
  const userId: number = useAppSelector(selectUserId);
  const categoryList: CategoryConfig[] = useAppSelector(selectCategoryList);
  const categoryColors = useAppSelector(selectCategoryColors);
  const categoryClick: number = useAppSelector(selectCategoryClick);
  const categoryInput: CategoryConfig = useAppSelector(selectCategoryInput);
  const colorClick: number = useAppSelector(selectCategoryColorClick);

  /* 디데이 관련 변수 */
  const [ddayList, setDdayList] = useState<ddayType[]>(DDAY_LIST);
  const [ddayClick, setDdayClick] = useState<number>(0);
  const [ddayInput, setDdayInput] = useState<ddayType>({
    ddayId: 0,
    ddayTitle: ddayList[0].ddayTitle,
    ddayDate: ddayList[0].ddayDate,
  });
  const [ddayError, setDdayError] = useState<boolean>(false);
  const dday_nextId = useRef(ddayList.length);

  /* 공통 사용 변수 */
  const [isDisable, setIsDisable] = useState<boolean>(false);

  const handleAdd = (title: string) => {
    if (title === "카테고리") {
      const init = {
        categoryTitle: "새 카테고리",
        categoryEmoticon: null,
        categoryColorId: 12,
      };
      settingApi
        .addCategories(userId, init)
        .then((res) => {
          const returnId = res.data.data.categoryId;
          const newCategory: CategoryConfig = {
            categoryId: returnId,
            categoryTitle: "새 카테고리",
            categoryEmoticon: "",
            categoryColorCode: categoryColors[11].categoryColorCode,
          };
          dispatch(setCategoryList([...categoryList, newCategory]));
          dispatch(setCategoryClick(categoryList.length));
          dispatch(setCategoryInput(newCategory));
        })
        .catch((err) => console.log(err));
    } else {
      const newDday: ddayType = {
        ddayId: dday_nextId.current,
        ddayTitle: "새 디데이",
        ddayDate: new Date(),
      };
      setDdayList([...ddayList, newDday]);
      setDdayClick(ddayList.length);
      dday_nextId.current += 1;
    }
  };

  const handleUpdate = (title: string) => {
    if (isDisable) return;
    if (title === "카테고리") {
      const input = {
        categoryId: categoryInput.categoryId,
        categoryTitle: categoryInput.categoryTitle || "",
        categoryEmoticon: categoryInput.categoryEmoticon || "",
        categoryColorId: colorClick + 1,
      };
      if (input.categoryTitle.length < 2 || input.categoryTitle.length >= 10) return;
      settingApi
        .editCategories(userId, input)
        .then((res) => {
          let copyList: CategoryConfig[] = [...categoryList];
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
      if (ddayInput.ddayTitle === "" || ddayInput.ddayTitle.length < 2 || ddayInput.ddayTitle.length > 20) {
        setDdayError(true);
        return;
      }
      setDdayError(false);
      setDdayList(
        ddayList.map((item, idx) => {
          if (ddayInput.ddayId === item.ddayId) {
            return {
              ...item,
              ddayTitle: ddayInput.ddayTitle,
              ddayDate: ddayInput.ddayDate,
            };
          }
          return item;
        }),
      );
    }
  };

  const handleDelete = (title: string) => {
    if (categoryList.length == 0) return;
    if (title === "카테고리") {
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
          dispatch(setCategoryClick(categoryClick === categoryList.length - 1 ? categoryClick - 1 : categoryClick));
        })
        .catch((err) => {
          console.log(err);
        });
      // 삭제한 값의 위 (0인 경우 아래) 배열 항목으로 재설정
    } else {
      setDdayList(
        ddayList.filter((item, idx) => {
          return idx !== ddayClick;
        }),
      );
      setDdayClick(ddayClick === 0 ? ddayClick : ddayClick - 1);
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
      <MyPageDetail title={title} isDisable={isDisable} handleUpdate={handleUpdate} handleDelete={handleDelete}>
        {categoryList.length != 0 ? (
          <>
            {
              {
                카테고리: <MyPageCategory />,
                디데이: (
                  <MyPageDday
                    click={ddayClick}
                    ddayList={ddayList}
                    input={ddayInput}
                    setInput={setDdayInput}
                    error={ddayError}
                  />
                ),
              }[title]
            }
          </>
        ) : (
          <></>
        )}
      </MyPageDetail>
    </div>
  );
};

export default MyPageFrame;
