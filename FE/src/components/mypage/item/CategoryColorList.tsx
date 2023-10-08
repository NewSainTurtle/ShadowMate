import React, { Dispatch, SetStateAction, useEffect, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { CATEGORY_COLORS } from "@util/data/CategoryData";
import CategoryColorItem from "./CategoryColorItem";

interface Props {
  click: number;
  setClick: Dispatch<SetStateAction<number>>;
}

const CategoryColorList = ({ click, setClick }: Props) => {
  useEffect(() => {
    setClick(click);
  }, [click]);

  return (
    <div className={styles["color__container"]}>
      {CATEGORY_COLORS.map((item, idx) => (
        <CategoryColorItem key={idx} item={item} index={idx} click={click} setClick={setClick} />
      ))}
    </div>
  );
};

export default CategoryColorList;
