import React, { useEffect, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import { AddOutlined, DeleteOutlined } from "@mui/icons-material";
import { todoType, categoryType } from "@util/planner.interface";
import { todoData_category } from "@util/data/DayTodos";

const categoryDefault: categoryType = {
  categoryId: 0,
  categoryTitle: "",
  categoryColorCode: "#E9E9EB",
  categoryEmoticon: "",
};

interface Props {
  idx?: number;
  item: todoType;
  addTodo?: boolean;
  disable?: boolean;
  todoModule: {
    insertTodo: (props: todoType) => void;
    updateTodo: (idx: number, props: todoType) => void;
    deleteTodo: (idx: number) => void;
  };
}

const TodoItem = ({ idx = -1, item, addTodo, disable, todoModule }: Props) => {
  const { categoryTitle, categoryColorCode, todoContent, todoStatus } = item;
  const { insertTodo, updateTodo, deleteTodo } = todoModule;
  const categoryList: categoryType[] = todoData_category;
  const [text, setText] = useState(todoContent);
  const dropMenuRef = useRef<HTMLDivElement>(null);
  const [isDropdownView, setDropdownView] = useState(false);
  const maxLength = 50;

  useEffect(() => {
    const handleOutsideClose = (e: { target: any }) => {
      if (isDropdownView && !dropMenuRef.current?.contains(e.target)) setDropdownView(false);
    };
    document.addEventListener("click", handleOutsideClose);
    return () => document.removeEventListener("click", handleOutsideClose);
  }, [isDropdownView]);

  const editText = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.value.length > maxLength) {
      e.target.value = e.target.value.slice(0, maxLength);
    }
    setText(e.target.value);
  };

  const handleOnKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      if (addTodo) {
        if (text === "") return;
        insertTodo({ ...item, todoContent: text });
        setText("");
      } else (document.activeElement as HTMLElement).blur();
    }
  };

  const handleClickCategory = (title: string, bgColor: string) => {
    if (addTodo) {
      insertTodo({ ...item, categoryTitle: title, categoryColorCode: bgColor });
    } else {
      updateTodo(idx, { ...item, categoryTitle: title, categoryColorCode: bgColor });
    }
  };

  const handleSaveTextTodo = () => {
    if (text === "") return;
    updateTodo(idx, { ...item, todoContent: text });
  };

  const handleSaveStatusTodo = () => {
    if (text === "") return;
    updateTodo(idx, { ...item, todoStatus: todoStatus == 0 ? 1 : todoStatus == 1 ? 2 : 0 });
  };

  const removeTodo = () => {
    deleteTodo(idx);
  };

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
      backgroundColor: `${bgColor == categoryDefault.categoryColorCode ? "transparent" : bgColor}`,
      color: `${getTextColorByBackgroundColor(bgColor.slice(1))}`,
    };
  };

  const clicked = addTodo || !disable;

  return (
    <div className={styles[`todo-item${disable ? "--disable" : ""}`]}>
      <div
        ref={dropMenuRef}
        className={styles[`todo-item__category${clicked ? "--add" : ""}`]}
        onClick={() => {
          if (!disable) setDropdownView(!isDropdownView);
        }}
      >
        <div className={styles["todo-item__category-box"]} style={categoryStyle(categoryColorCode)}>
          {disable ? addTodo && <AddOutlined /> : <Text>{categoryTitle}</Text>}
        </div>
        {isDropdownView && (
          <div className={styles["todo-item__category-menu"]}>
            <span onClick={() => handleClickCategory("", categoryDefault.categoryColorCode)}>&emsp;&emsp;</span>
            {categoryList.map((item, idx) => {
              const { categoryId, categoryColorCode, categoryTitle } = item;
              return (
                <span
                  key={categoryId}
                  style={categoryStyle(categoryColorCode)}
                  onClick={() => handleClickCategory(categoryTitle, categoryColorCode)}
                >
                  {categoryTitle}
                </span>
              );
            })}
          </div>
        )}
      </div>

      <div className={styles[`todo-item__content${clicked ? "--add" : ""}`]}>
        {disable ? (
          addTodo && (
            <span>
              <AddOutlined />
            </span>
          )
        ) : (
          <div className={styles["todo-item__content__possible"]}>
            <input
              value={text}
              placeholder={"할 일을 입력하세요"}
              minLength={2}
              maxLength={maxLength}
              autoFocus={addTodo}
              onChange={editText}
              onKeyDown={handleOnKeyPress}
              onBlur={handleSaveTextTodo}
            />
            {!addTodo && (
              <div onClick={removeTodo}>
                <DeleteOutlined />
              </div>
            )}
          </div>
        )}
      </div>

      <div className={styles[`todo-item__checked${!disable ? "--add" : ""}`]} onClick={handleSaveStatusTodo}>
        <Text types="semi-medium">{[" ", "O", "X"][todoStatus]}</Text>
      </div>
    </div>
  );
};

export default TodoItem;
