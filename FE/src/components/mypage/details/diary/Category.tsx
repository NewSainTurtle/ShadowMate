import React, { ChangeEvent, ChangeEventHandler, Dispatch, SetStateAction, useEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import Input from "@components/common/Input";
import CategoryColorList from "@components/mypage/item/CategoryColorList";
import { CategoryConfig } from "@util/planner.interface";
import { CATEGORY_COLORS } from "@util/data/CategoryData";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import {
  selectCategoryClick,
  selectCategoryInput,
  selectCategoryList,
  setCategoryColorClick,
  setCategoryInput,
} from "@store/mypageSlice";

const MyPageCategory = () => {
  const dispatch = useAppDispatch();
  const click = useAppSelector(selectCategoryClick);
  const categoryList = useAppSelector(selectCategoryList);
  const categoryInput: CategoryConfig = useAppSelector(selectCategoryInput);

  const { categoryId, categoryTitle, categoryEmoticon, categoryColorCode } = categoryInput;

  const onChangeInput = (e: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = e.target;
    if (name === "categoryEmoticon") {
      if (value.length > 2) return;
      if (value !== "" && !value.match("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+")) {
        return;
      }
    }
    dispatch(setCategoryInput({ ...categoryInput, [name]: value }));
  };

  useEffect(() => {
    let currentColor: number = 0;
    CATEGORY_COLORS.map((item, idx) => {
      if (item === categoryList[click].categoryColorCode) currentColor = idx;
    });
    dispatch(setCategoryColorClick(currentColor));
    dispatch(setCategoryInput(categoryList[click]));
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
