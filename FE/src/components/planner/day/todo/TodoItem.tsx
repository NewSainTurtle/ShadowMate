import React, { useEffect, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import CategorySelector from "@components/common/CategorySelector";
import { AddOutlined, DeleteOutlined } from "@mui/icons-material";
import { BASIC_CATEGORY_ITEM } from "@store/planner/daySlice";
import { TodoConfig } from "@util/planner.interface";
import { CategoryConfig } from "@util/planner.interface";
import { todoData_category } from "@util/data/DayTodos";

interface Props {
  idx?: number;
  todoItem: TodoConfig;
  addTodo?: boolean;
  disable?: boolean;
  todoModule: {
    insertTodo: (props: TodoConfig) => void;
    updateTodo: (idx: number, props: TodoConfig) => void;
    deleteTodo: (idx: number) => void;
  };
}

const TodoItem = ({ idx = -1, todoItem, addTodo, disable, todoModule }: Props) => {
  const { category, todoContent, todoStatus } = todoItem;
  const [categoryTitle, categoryColorCode] = [category!.categoryTitle, category!.categoryColorCode];
  const { insertTodo, updateTodo, deleteTodo } = todoModule;
  const categoryList: CategoryConfig[] = todoData_category;
  const [text, setText] = useState(todoContent);
  const dropMenuRef = useRef<HTMLDivElement>(null);
  const [isDropdownView, setDropdownView] = useState(false);
  const maxLength = 50;

  const [ModalOpen, setModalOpen] = useState<boolean>(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

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
        insertTodo({ ...todoItem, todoContent: text });
        setText("");
      } else (document.activeElement as HTMLElement).blur();
    }
  };

  const handleClickCategory = (props: CategoryConfig) => {
    const newCategory = { ...todoItem, category: props };
    if (addTodo) {
      insertTodo(newCategory);
    } else {
      updateTodo(idx, newCategory);
    }
    setModalOpen(false);
  };

  const handleSaveTextTodo = () => {
    if (text === "") return;
    updateTodo(idx, { ...todoItem, todoContent: text });
  };

  const handleSaveStatusTodo = () => {
    if (text === "") return;
    updateTodo(idx, {
      ...todoItem,
      todoStatus: todoStatus == "공백" ? "완료" : todoStatus == "완료" ? "미완료" : "공백",
    });
  };

  const clickDeleteTodo = () => {
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
      backgroundColor: `${bgColor == BASIC_CATEGORY_ITEM.categoryColorCode ? "transparent" : bgColor}`,
      color: `${getTextColorByBackgroundColor(bgColor.slice(1))}`,
    };
  };

  const clicked = addTodo || !disable;

  return (
    <div className={styles[`todo-item${disable ? "--disable" : ""}`]}>
      <div ref={dropMenuRef} className={styles[`todo-item__category${clicked ? "--add" : ""}`]} onClick={handleOpen}>
        <div className={styles["todo-item__category-box"]} style={categoryStyle(categoryColorCode)}>
          {disable ? addTodo && <AddOutlined /> : <Text>{categoryTitle}</Text>}
        </div>
        {isDropdownView && (
          <div className={styles["todo-item__category-menu"]}>
            <span onClick={() => handleClickCategory(BASIC_CATEGORY_ITEM)}>&emsp;&emsp;</span>
            {categoryList.map((item) => {
              return (
                <span
                  key={item.categoryId}
                  style={categoryStyle(item.categoryColorCode)}
                  onClick={() => handleClickCategory(item)}
                >
                  {item.categoryTitle}
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
              <div onClick={clickDeleteTodo}>
                <DeleteOutlined />
              </div>
            )}
          </div>
        )}
      </div>

      <div className={styles[`todo-item__checked${!disable ? "--add" : ""}`]} onClick={handleSaveStatusTodo}>
        <Text types="semi-medium">{todoStatus == "공백" ? " " : todoStatus == "완료" ? "O" : "X"}</Text>
      </div>

      <Modal open={ModalOpen} onClose={handleClose}>
        <CategorySelector type="day" handleClick={handleClickCategory} />
      </Modal>
    </div>
  );
};

export default TodoItem;
