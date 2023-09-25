import React, { useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import { AddCircleOutline, DeleteOutlined } from "@mui/icons-material";
import { todoListType, categoryType } from "@util/planner.interface";
import { todoData_category } from "@util/data/DayTodos";

interface Props {
  item: todoListType;
  insertTodo: () => void;
  updateTodo: (todo: todoListType) => void;
  deleteTodo: (id: number) => void;
}

const TodoItem = ({ item, insertTodo, updateTodo, deleteTodo }: Props) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const { todoId, categoryName, todoContent, todoStatus, isPossible } = item;
  const checkedArr = [" ", "O", "X"];
  const [text, setText] = useState(todoContent);
  const modifyText = (e: React.ChangeEvent<HTMLInputElement>) => {
    setText(e.target.value);
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
    updateTodo({ ...item, todoContent: text });
  };

  return (
    <div
      className={`${styles["todo-item"]} ${!isPossible && styles["todo-item--disable"]}`}
      onClick={() => addTodo(isPossible)}
    >
      <div className={styles["todo-item__category"]}>
        {isPossible ? <Text>{categoryName}</Text> : <AddCircleOutline />}
      </div>
      <div className={styles["todo-item__context"]}>
        {isPossible ? (
          <div className={styles["todo-item__context__possiable"]}>
            <input
              ref={inputRef}
              value={text}
              onChange={modifyText}
              onBlur={handleOnBlur}
              onKeyDown={handleOnKeyPress}
            />
            <div onClick={removeTodo}>
              <DeleteOutlined />
            </div>
          </div>
        ) : (
          <AddCircleOutline />
        )}
      </div>
      <div className={styles["todo-item__checked"]}>
        <Text types="semi-medium"> {checkedArr[todoStatus]}</Text>
      </div>
    </div>
  );
};

export default TodoItem;
