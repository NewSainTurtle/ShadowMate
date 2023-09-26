import React, { Dispatch, SetStateAction } from "react";
import styles from "../MyPage.module.scss";
import { CategoryConfig } from "../MyPageFrame";

interface Props {
  item: CategoryConfig;
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
      <div>{item.emoticon}</div>
      <div>{item.title}</div>
      <div style={{ backgroundColor: item.colorCode }}></div>
    </div>
  );
};

export default MyPageCategoryItem;
