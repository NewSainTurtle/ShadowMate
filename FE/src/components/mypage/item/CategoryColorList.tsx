import React, { useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import COLORS from "@util/data/CategoryColors";
import CategoryColorItem from "./CategoryColorItem";

const CategoryColorList = () => {
  const [click, setClick] = useState<number>(0);

  return (
    <div className={styles["color__container"]}>
      {COLORS.map((item, idx) => (
        <CategoryColorItem key={idx} item={item} index={idx} click={click} setClick={setClick} />
      ))}
    </div>
  );
};

export default CategoryColorList;
