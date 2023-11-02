import React, { ChangeEvent, useEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import CategoryColorList from "@components/mypage/item/CategoryColorList";
import { CategoryColorConfig, CategoryConfig } from "@util/planner.interface";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import {
  selectCategoryClick,
  selectCategoryColors,
  selectCategoryInput,
  selectCategoryList,
  setCategoryColorClick,
  setCategoryInput,
} from "@store/mypageSlice";

const MyPageCategory = () => {
  const dispatch = useAppDispatch();
  const click = useAppSelector(selectCategoryClick);
  const categoryList = useAppSelector(selectCategoryList);
  const categoryColors = useAppSelector(selectCategoryColors);
  const categoryInput: CategoryConfig = useAppSelector(selectCategoryInput);
  const [error, setError] = useState<boolean>(false);

  const { categoryTitle, categoryEmoticon } = categoryInput || "";
  const [length, setLength] = useState<number>(categoryTitle ? categoryTitle.length : 0);

  const onChangeInput = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    if (name === "categoryEmoticon") {
      if (value.length > 2) return;
      if (value !== "" && !value.match("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+")) {
        return;
      }
    }
    if (name === "categoryTitle") {
      setLength(value.length);
      if (value.length < 2 || value.length >= 10) {
        setError(true);
      } else setError(false);
    }
    dispatch(setCategoryInput({ ...categoryInput, [name]: value }));
  };

  useEffect(() => {
    if (categoryList.length > 0 && categoryInput) {
      let currentColor: number = 0;
      categoryColors.map((item: CategoryColorConfig, idx: number) => {
        if (item.categoryColorCode === categoryList[click].categoryColorCode) currentColor = idx;
      });
      dispatch(setCategoryColorClick(currentColor));
      dispatch(setCategoryInput(categoryList[click]));
      setLength(categoryList[click].categoryTitle.length);
    }
  }, [click]);

  return (
    <div className={styles["frame__contents"]}>
      <div className={styles["frame__line"]}>
        <Text>카테고리 이름</Text>
        <Input
          name="categoryTitle"
          value={categoryTitle || ""}
          placeholder="카테고리 이름을 입력하세요."
          onChange={onChangeInput}
          error={error}
          helperText={error ? "2 ~ 10자의 이름을 입력할 수 있습니다." : `글자 수: ${length}/10`}
        />
      </div>
      <div className={styles["frame__line"]}>
        <Text>카테고리 이모지</Text>
        <Input
          name="categoryEmoticon"
          value={categoryEmoticon || ""}
          placeholder="카테고리 이모지"
          onChange={onChangeInput}
          helperText={
            // <>
            //   윈도우 사용 시 윈도우키 + . 를 눌러보세요.
            //   <br /> 맥 사용 시 Fn + E 를 눌러보세요.
            // </>
            <>이모지 외 사용이 불가능합니다.</>
          }
        />
      </div>
      <div className={styles["frame__line"]}>
        <Text>카테고리 색상</Text>
        <CategoryColorList />
      </div>
    </div>
  );
};

export default MyPageCategory;
