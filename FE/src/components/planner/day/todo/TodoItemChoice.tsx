import React from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import { useAppDispatch } from "@hooks/hook";
import { BASIC_CATEGORY_ITEM, setTodoItem } from "@store/planner/daySlice";
import { TodoConfig } from "@util/planner.interface";

interface Props {
  idx?: number;
  todoItem: TodoConfig;
  possible: boolean;
  disable?: boolean;
}

const TodoItemChoice = ({ todoItem, possible, disable }: Props) => {
  const dispatch = useAppDispatch();
  const { todoContent, todoStatus } = todoItem;
  const category = (() => todoItem.category ?? BASIC_CATEGORY_ITEM)();
  const { categoryTitle, categoryColorCode } = category;

  const getTextColorByBackgroundColor = (hexColor: string) => {
    const rgb = parseInt(hexColor, 16);
    const r = (rgb >> 16) & 0xff;
    const g = (rgb >> 8) & 0xff;
    const b = (rgb >> 0) & 0xff;
    const luma = 0.2126 * r + 0.7152 * g + 0.0722 * b;

    return luma < 127.5 ? "white" : "black";
  };

  const categoryStyle = (bgColor: string) => {
    return {
      backgroundColor: `${bgColor == "#E9E9EB" ? "none" : bgColor}`,
      color: `${getTextColorByBackgroundColor(bgColor.slice(1))}`,
    };
  };

  const possibility = possible ? "--possible" : "--impossible";

  function handleClickTodo() {
    if (possible) dispatch(setTodoItem(todoItem));
  }

  const isDisableStyle = disable ? "--disable" : "";
  const todoStatusView = {
    공백: " ",
    완료: "○",
    진행중: "△",
    미완료: "⨉",
  };

  return (
    <div className={`${styles[`todo-item${isDisableStyle}`]} ${styles[possibility]}`} onClick={handleClickTodo}>
      <div className={styles["todo-item__category"]}>
        <div className={styles["todo-item__category-box"]} style={categoryStyle(categoryColorCode)}>
          <Text>{categoryTitle}</Text>
        </div>
      </div>
      <div className={styles[`todo-item__content`]}>
        <div className={styles["todo-item__content__possible"]}>
          <Text>{todoContent}</Text>
        </div>
      </div>
      <div className={styles[`todo-item__checked`]}>
        <div>
          <Text types="semi-medium">{todoStatusView[todoStatus]}</Text>
        </div>
      </div>
    </div>
  );
};

export default TodoItemChoice;
