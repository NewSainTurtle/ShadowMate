import React, { useMemo, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import { AddOutlined, DeleteOutlined } from "@mui/icons-material";
import { todoListType, categoryType } from "@util/planner.interface";
import { todoData_category } from "@util/data/DayTodos";

interface Props {
  item: todoListType;
  addTodo: boolean;
  insertTodo: () => void;
  updateTodo: (todo: todoListType) => void;
  deleteTodo: (id: number) => void;
}

const TodoItem = ({ item, addTodo, insertTodo, updateTodo, deleteTodo }: Props) => {
  const { todoId, categoryTitle, categoryColorCode, todoContent, todoStatus, isPossible } = item;
  const [text, setText] = useState(todoContent);
  const [state, setState] = useState(todoStatus);
  const stateString = useMemo(() => {
    return [" ", "O", "X"][state];
  }, [state]);
  const maxLenght = 50;

  const editText = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.value.length > maxLenght) {
      e.target.value = e.target.value.slice(0, maxLenght);
    }
    setText(e.target.value);
  };

  const editState = () => {
    if (isPossible) {
      setState(state == 0 ? 1 : state == 1 ? 2 : 0);
      (document.activeElement as HTMLElement).blur();
    }
  };

  const handleOnKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      (document.activeElement as HTMLElement).blur();
    }
  };

  const handleOnAddTodo = (addTodo: boolean, possible: boolean | undefined) => {
    if (addTodo && !possible) insertTodo();
  };

  const handleSaveTodo = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.value === "") return;
    let todo = {
      ...item,
      todoContent: e.target.value,
      todoStatus: state,
    };
    updateTodo(todo);
  };

  const removeTodo = () => {
    if (text == "") return;
    deleteTodo(todoId);
    setText("");
    setState(0);
  };

  const getTextColorByBackgroundColor = (hexColor: string) => {
    const rgb = parseInt(hexColor, 16);
    const r = (rgb >> 16) & 0xff;
    const g = (rgb >> 8) & 0xff;
    const b = (rgb >> 0) & 0xff;
    const luma = 0.2126 * r + 0.7152 * g + 0.0722 * b;

    return luma < 127.5 ? "white" : "black";
  };

  const categoryStyle: React.CSSProperties = {
    backgroundColor: `${categoryColorCode}`,
    color: `${getTextColorByBackgroundColor(categoryColorCode)}`,
  };

  return (
    <div className={`${styles["todo-item"]} ${!isPossible && styles["todo-item--disable"]}`}>
      <div className={`${styles["todo-item__category"]} ${addTodo && styles["todo-item__content-add"]}`}>
        <div className={styles["todo-item__category-box"]} style={categoryStyle}>
          {isPossible ? <Text>{categoryTitle}</Text> : addTodo && <AddOutlined />}
        </div>
      </div>
      <div
        className={`${styles["todo-item__content"]} ${addTodo && styles["todo-item__content-add"]}`}
        onClick={() => handleOnAddTodo(addTodo, isPossible)}
      >
        {isPossible ? (
          <div className={styles["todo-item__content__possiable"]}>
            <input
              defaultValue={todoContent}
              placeholder={"할 일을 입력하세요"}
              minLength={2}
              maxLength={maxLenght}
              // onChange={editText}
              onBlur={handleSaveTodo}
              onKeyDown={handleOnKeyPress}
            />
            <div onClick={removeTodo}>
              <DeleteOutlined />
            </div>
          </div>
        ) : (
          addTodo && <AddOutlined />
        )}
      </div>
      <div className={styles["todo-item__checked"]} onClick={editState}>
        <Text types="semi-medium">{stateString}</Text>
      </div>
    </div>
  );
};

export default TodoItem;
