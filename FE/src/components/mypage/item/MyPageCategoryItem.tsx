import React, { Dispatch, SetStateAction, useEffect } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { categoryType } from "@util/planner.interface";

interface Props {
  item: categoryType;
  index: number;
  click: number;
  setClick: Dispatch<SetStateAction<number>>;
}

const MyPageCategoryItem = ({ item, index, click, setClick }: Props) => {
  const clicked = click === index ? "--clicked" : "";

  return (
    <div className={styles[`category__item${clicked}`]} onClick={() => setClick(index)}>
      <div>{item.categoryEmoticon}</div>
      <div>{item.categoryTitle}</div>
      <div style={{ backgroundColor: item.categoryColorCode }}></div>
    </div>
  );
};

export default MyPageCategoryItem;
