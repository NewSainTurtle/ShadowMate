import React, { useMemo, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import { AddOutlined, DeleteOutlined } from "@mui/icons-material";
import { todoType, categoryType } from "@util/planner.interface";
import { todoData_category } from "@util/data/DayTodos";

const TodoDataDefalut: todoType = {
  todoId: -1,
  categoryTitle: "",
  categoryColorCode: "",
  todoContent: "",
  todoStatus: 0,
};

interface Props {
  idx?: number;
  item?: todoType;
  addTodo?: boolean;
  disable?: boolean;
  todoModule: {
    insertTodo: (props: todoType) => void;
    updateTodo: (idx: number, props: todoType) => void;
    deleteTodo: (idx: number) => void;
  };
}

const TodoItem = ({ idx = -1, item = TodoDataDefalut, addTodo, disable, todoModule }: Props) => {
  const { categoryTitle, categoryColorCode, todoContent, todoStatus } = item;
  const { insertTodo, updateTodo, deleteTodo } = todoModule;
  const [text, setText] = useState(todoContent);
  const [state, setState] = useState<0 | 1 | 2>(todoStatus);
  const maxLenght = 50;

  const editText = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.value.length > maxLenght) {
      e.target.value = e.target.value.slice(0, maxLenght);
    }
    setText(e.target.value);
  };

  const editState = () => {
    if (!(disable || addTodo)) {
      setState(state == 0 ? 1 : state == 1 ? 2 : 0);
      (document.activeElement as HTMLElement).blur();
    }
  };

  const handleOnKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      if (addTodo) {
        if (text === "") return;
        insertTodo({ ...TodoDataDefalut, todoContent: text });
        setText("");
      } else {
        (document.activeElement as HTMLElement).blur();
      }
    }
  };

  const handleSaveTodo = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.value === "") return;
    let todo = {
      ...item,
      todoContent: e.target.value,
      todoStatus: state,
    };
    updateTodo(idx, { ...todo });
  };

  const removeTodo = () => {
    deleteTodo(idx);
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

  const clicked = addTodo || !disable;

  return (
    <div className={styles[`todo-item${disable ? "--disable" : ""}`]}>
      <div className={styles[`todo-item__category${clicked ? "--add" : ""}`]}>
        <div className={styles["todo-item__category-box"]} style={categoryStyle}>
          {disable ? addTodo && <AddOutlined /> : <Text>{categoryTitle}</Text>}
        </div>
      </div>

      <div className={styles[`todo-item__content${clicked ? "--add" : ""}`]}>
        {disable ? (
          addTodo && (
            <span>
              <AddOutlined />
            </span>
          )
        ) : (
          <div className={styles["todo-item__content__possiable"]}>
            <input
              value={text || todoContent}
              placeholder={"할 일을 입력하세요"}
              minLength={2}
              maxLength={maxLenght}
              autoFocus={addTodo}
              onChange={editText}
              onKeyDown={handleOnKeyPress}
              onBlur={handleSaveTodo}
            />
            {!addTodo && (
              <div onClick={removeTodo}>
                <DeleteOutlined />
              </div>
            )}
          </div>
        )}
      </div>

      <div className={styles[`todo-item__checked${!disable ? "--add" : ""}`]} onClick={editState}>
        <Text types="semi-medium">{[" ", "O", "X"][state || todoStatus]}</Text>
      </div>
    </div>
  );
};

export default TodoItem;
