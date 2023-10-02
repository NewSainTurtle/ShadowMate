import React, { Dispatch, SetStateAction } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { categoryType } from "@util/planner.interface";
import COLORS from "@util/data/CategoryColors";

interface Props {
  item: categoryType;
  index: number;
  click: number;
  setClick: Dispatch<SetStateAction<number>>;
}

const MyPageCategoryItem = ({ item, index, click, setClick }: Props) => {
  return (
    <div
      className={click === index ? styles["category__item--clicked"] : styles["category__item"]}
      onClick={() => setClick(index)}
    >
      <div>{item.categoryEmoticon}</div>
      <div>{item.categoryTitle}</div>
      <div style={{ backgroundColor: COLORS[item.categoryColorCode] }}></div>
    </div>
  );
};

export default MyPageCategoryItem;
