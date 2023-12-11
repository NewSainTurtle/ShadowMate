import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import CategoryColorItem from "@components/mypage/item/CategoryColorItem";
import { useAppSelector } from "@hooks/hook";
import { selectCategoryColors } from "@store/mypage/categorySlice";
import { CategoryColorConfig } from "@util/planner.interface";

const CategoryColorList = () => {
  const categoryColors = useAppSelector(selectCategoryColors);
  return (
    <div className={styles["color__container"]}>
      {categoryColors.map((item: CategoryColorConfig, idx: number) => (
        <CategoryColorItem key={idx} item={item} index={idx} />
      ))}
    </div>
  );
};

export default CategoryColorList;
