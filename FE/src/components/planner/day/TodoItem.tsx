import React, { useMemo, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import { AddOutlined, DeleteOutlined } from "@mui/icons-material";
import { todoListType, categoryType } from "@util/planner.interface";
import { todoData_category } from "@util/data/DayTodos";

interface Props {
  item: todoListType;
  insertTodo: () => void;
  updateTodo: (todo: todoListType) => void;
  deleteTodo: (id: number) => void;
}

const TodoItem = ({ item, insertTodo, updateTodo, deleteTodo }: Props) => {
  const { todoId, categoryName, categoryColorCode, todoContent, todoStatus, isPossible } = item;
  const [text, setText] = useState(todoContent);
  const [state, setState] = useState(todoStatus);
  const stateString = useMemo(() => {
    return [" ", "O", "X"][state];
  }, [state]);
  const maxLenght = 55;

  const editText = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.value.length > maxLenght) {
      e.target.value = e.target.value.slice(0, maxLenght);
    }
    setText(e.target.value);
  };

  const editState = () => {
    setState(state == 0 ? 1 : state == 1 ? 2 : 0);
    saveTodo();
  };

  const handleOnBlur = () => {
    saveTodo();
  };

  const handleOnKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      saveTodo();
    }
  };

  const addTodo = (possible: boolean | undefined) => {
    if (possible != undefined && !possible) insertTodo();
  };

  const removeTodo = () => {
    deleteTodo(todoId);
    setText("");
  };

  const saveTodo = () => {
    let todo = {
      ...item,
      todoContent: text,
      todoStatus: state,
    };
    updateTodo(todo);
  };

  const getTextColorByBackgroundColor = (hexColor: string) => {
    const rgb = parseInt(hexColor, 16); // rrggbb를 10진수로 변환
    const r = (rgb >> 16) & 0xff;
    const g = (rgb >> 8) & 0xff;
    const b = (rgb >> 0) & 0xff;
    const luma = 0.2126 * r + 0.7152 * g + 0.0722 * b;

    // 글자 색상 선택
    return luma < 127.5 ? "white" : "black";
  };

  const categoryStyle: React.CSSProperties = {
    backgroundColor: `${categoryColorCode}`,
    color: `${getTextColorByBackgroundColor(categoryColorCode)}`,
  };

  return (
    <div className={`${styles["todo-item"]} ${!isPossible && styles["todo-item--disable"]}`}>
      <div className={styles["todo-item__category"]}>
        <div className={styles["todo-item__category-box"]} style={categoryStyle}>
          {isPossible ? <Text>{categoryName}</Text> : <AddOutlined />}
        </div>
      </div>
      <div className={styles["todo-item__context"]} onClick={() => addTodo(isPossible)}>
        {isPossible ? (
          <div className={styles["todo-item__context__possiable"]}>
            <input
              value={text}
              minLength={2}
              maxLength={maxLenght}
              onChange={editText}
              onBlur={handleOnBlur}
              onKeyDown={handleOnKeyPress}
            />
            <div onClick={removeTodo}>
              <DeleteOutlined />
            </div>
          </div>
        ) : (
          <AddOutlined />
        )}
      </div>
      <div className={styles["todo-item__checked"]} onClick={editState}>
        <Text types="semi-medium"> {stateString}</Text>
      </div>
    </div>
  );
};

export default TodoItem;
