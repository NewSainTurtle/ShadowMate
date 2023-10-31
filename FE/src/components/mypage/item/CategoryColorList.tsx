import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import { CATEGORY_COLORS } from "@util/data/CategoryData";
import CategoryColorItem from "./CategoryColorItem";

const CategoryColorList = () => {
  return (
    <div className={styles["color__container"]}>
      {CATEGORY_COLORS.map((item, idx) => (
        <CategoryColorItem key={idx} item={item} index={idx} />
      ))}
    </div>
  );
};

export default CategoryColorList;
