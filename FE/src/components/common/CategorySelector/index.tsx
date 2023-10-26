import React from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import { BASIC_CATEGORY_ITEM } from "@store/planner/daySlice";
import { todoData_category } from "@util/data/DayTodos";
import { CategoryConfig } from "@util/planner.interface";

interface Props {
  handleClick: (title: string, color: string, emoticon?: string) => void;
}

// 삭제 예정 start
export const getTextColorByBackgroundColor = (hexColor: string) => {
  const rgb = parseInt(hexColor, 16);
  const r = (rgb >> 16) & 0xff;
  const g = (rgb >> 8) & 0xff;
  const b = (rgb >> 0) & 0xff;
  const luma = 0.2126 * r + 0.7152 * g + 0.0722 * b;

  return luma < 127.5 ? "white" : "black";
};

export const categoryStyle = (bgColor: string) => {
  return {
    backgroundColor: `${bgColor == BASIC_CATEGORY_ITEM.categoryColorCode ? "none" : bgColor}`,
    color: `${getTextColorByBackgroundColor(bgColor.slice(1))}`,
  };
};
// 삭제 예정 end

const CategorySelector = ({ handleClick }: Props) => {
  const categoryList: CategoryConfig[] = todoData_category;
  return (
    <div className={styles["category__selector"]}>
      <div>
        <Text>카테고리 선택</Text>
      </div>
      <div
        className={styles["category__item--hover"]}
        onClick={() => handleClick("", BASIC_CATEGORY_ITEM.categoryColorCode, BASIC_CATEGORY_ITEM.categoryEmoticon)}
      >
        <div>{BASIC_CATEGORY_ITEM.categoryEmoticon}</div>
        <div>카테고리 없음</div>
        <div style={{ backgroundColor: BASIC_CATEGORY_ITEM.categoryColorCode }}></div>
      </div>
      {categoryList.map((item, idx) => (
        <div
          key={idx}
          className={styles["category__item--hover"]}
          onClick={() => handleClick(item.categoryTitle, item.categoryColorCode, item.categoryEmoticon)}
        >
          <div>{item.categoryEmoticon}</div>
          <div>{item.categoryTitle}</div>
          <div style={{ backgroundColor: item.categoryColorCode }}></div>
        </div>
      ))}
    </div>
  );
};

export default CategorySelector;
