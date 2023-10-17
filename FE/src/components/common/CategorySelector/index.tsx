import React from "react";
import styles from "@styles/planner/day.module.scss";
import { categoryDefault } from "@components/planner/day/TodoItem";
import { todoData_category } from "@util/data/DayTodos";
import { categoryType } from "@util/planner.interface";

interface Props {
  handleClick: (title: string, color: string) => void;
}

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
    backgroundColor: `${bgColor == categoryDefault.categoryColorCode ? "none" : bgColor}`,
    color: `${getTextColorByBackgroundColor(bgColor.slice(1))}`,
  };
};

const CategorySelector = ({ handleClick }: Props) => {
  const categoryList: categoryType[] = todoData_category;
  return (
    <div className={styles["todo-item__category-menu"]}>
      <span onClick={() => handleClick("", categoryDefault.categoryColorCode)}>&emsp;&emsp;</span>
      {categoryList.map((item, idx) => {
        const { categoryId, categoryColorCode, categoryTitle } = item;
        return (
          <span
            key={categoryId}
            style={categoryStyle(categoryColorCode)}
            onClick={() => handleClick(categoryTitle, categoryColorCode)}
          >
            {categoryTitle}
          </span>
        );
      })}
    </div>
  );
};

export default CategorySelector;
