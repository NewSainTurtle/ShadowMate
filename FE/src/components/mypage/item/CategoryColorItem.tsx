import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectCategoryColorClick, setCategoryColorClick } from "@store/mypage/categorySlice";
import { CategoryColorConfig } from "@util/planner.interface";

interface Props {
  item: CategoryColorConfig;
  index: number;
}

const CategoryColorItem = ({ item, index }: Props) => {
  const dispatch = useAppDispatch();
  const click: number = useAppSelector(selectCategoryColorClick);

  const clicked = click === index ? "--clicked" : "";
  return (
    <div
      className={styles[`color__item${clicked}`]}
      style={{ backgroundColor: item.categoryColorCode }}
      onClick={() => {
        dispatch(setCategoryColorClick(index));
      }}
    ></div>
  );
};

export default CategoryColorItem;
