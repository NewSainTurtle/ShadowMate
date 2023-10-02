import React, { Dispatch, SetStateAction } from "react";
import styles from "@styles/mypage/MyPage.module.scss";

interface Props {
  item: string;
  index: number;
  click: number;
  setClick: Dispatch<SetStateAction<number>>;
}

const CategoryColorItem = ({ item, click, index, setClick }: Props) => {
  return (
    <div
      className={styles[click === index ? "color__item--clicked" : "color__item"]}
      style={{ backgroundColor: item }}
      onClick={() => {
        setClick(index);
      }}
    >
      <div></div>
    </div>
  );
};

export default CategoryColorItem;
