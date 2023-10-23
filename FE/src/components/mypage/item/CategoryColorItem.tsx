import React, { Dispatch, SetStateAction } from "react";
import styles from "@styles/mypage/MyPage.module.scss";

interface Props {
  item: string;
  index: number;
  click: number;
  setClick: Dispatch<SetStateAction<number>>;
}

const CategoryColorItem = ({ item, click, index, setClick }: Props) => {
  const clicked = click === index ? "--clicked" : "";
  return (
    <div
      className={styles[`color__item${clicked}`]}
      style={{ backgroundColor: item }}
      onClick={() => {
        setClick(index);
      }}
    ></div>
  );
};

export default CategoryColorItem;
