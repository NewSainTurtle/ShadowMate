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
  const { todoId, categoryName, todoContent, todoStatus, isPossible } = item;
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

  return (
    <div
      className={`${styles["todo-item"]} ${!isPossible && styles["todo-item--disable"]}`}
      onClick={() => addTodo(isPossible)}
    >
      <div className={styles["todo-item__category"]}>{isPossible ? <Text>{categoryName}</Text> : <AddOutlined />}</div>
      <div className={styles["todo-item__context"]}>
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
