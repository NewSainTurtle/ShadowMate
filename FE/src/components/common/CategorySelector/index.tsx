import React, { RefObject, useEffect, useRef, useState } from "react";
import styles from "@styles/common/CategorySelector.module.scss";
import { categoryDefault } from "@components/planner/day/TodoItem";
import { todoData_category } from "@util/data/DayTodos";
import { categoryType } from "@util/planner.interface";
import Text from "@components/common/Text";
import { createPortal } from "react-dom";

interface Props {
  target?: RefObject<HTMLDivElement>;
  position?: PositionConfig;
  handleClick: (title: string, color: string) => void;
}

export interface PositionConfig {
  top: number | undefined;
  left: number;
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

const CategorySelector = ({ target, position, handleClick }: Props) => {
  const categoryList: categoryType[] = todoData_category;

  return target?.current
    ? createPortal(
        <div className={styles["selector"]}>
          <span onClick={() => handleClick("", categoryDefault.categoryColorCode)}></span>
          {categoryList.map((item, idx) => {
            const { categoryId, categoryEmoticon, categoryColorCode, categoryTitle } = item;
            return (
              <div
                className={styles["selector__item"]}
                key={categoryId}
                onClick={() => handleClick(categoryTitle, categoryColorCode)}
              >
                <div style={categoryStyle(categoryColorCode)}></div>
                <Text types="small">{categoryEmoticon}</Text>
                <Text types="small">{categoryTitle}</Text>
              </div>
            );
          })}
        </div>,
        target.current,
      )
    : null;
};

export default CategorySelector;
