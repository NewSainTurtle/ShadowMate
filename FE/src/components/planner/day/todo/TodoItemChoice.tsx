import React from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import { useAppDispatch } from "@hooks/hook";
import { todoType, setTodoItem } from "@store/planner/daySlice";

interface Props {
  idx?: number;
  todoItem: todoType;
  possible: boolean;
}

const TodoItemChoice = ({ todoItem, possible }: Props) => {
  const dispatch = useAppDispatch();
  const { category, todoContent, todoStatus } = todoItem;
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

  return (
    <div className={`${styles["todo-item"]} ${styles[possibility]}`} onClick={handleClickTodo}>
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
          <Text types="semi-medium">{todoStatus == "공백" ? " " : todoStatus == "완료" ? "O" : "X"}</Text>
        </div>
      </div>
    </div>
  );
};

export default TodoItemChoice;
