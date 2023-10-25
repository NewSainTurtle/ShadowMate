import React, { useEffect, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import CategorySelector, { categoryStyle } from "@components/common/CategorySelector";
import { AddOutlined, DeleteOutlined } from "@mui/icons-material";
import { todoType, categoryType } from "@util/planner.interface";
import { todoData_category } from "@util/data/DayTodos";

const TodoDataDefalut: todoType = {
  todoId: 0,
  categoryTitle: "",
  categoryColorCode: "#E9E9EB",
  todoContent: "",
  todoStatus: 0,
};

export const categoryDefault: categoryType = {
  categoryId: 0,
  categoryTitle: "",
  categoryColorCode: "#E9E9EB",
  categoryEmoticon: "",
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
  const categoryList: categoryType[] = todoData_category;
  const [text, setText] = useState(todoContent);
  const dropMenuRef = useRef<HTMLDivElement>(null);
  const [isDropdownView, setDropdownView] = useState(false);
  const maxLength = 50;

  // 카테고리 모달 start
  const [ModalOpen, setModalOpen] = useState<boolean>(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);
  // 카테고리 모달 end

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
      insertTodo({ ...TodoDataDefalut, categoryTitle: title, categoryColorCode: bgColor });
    } else {
      updateTodo(idx, { ...item, categoryTitle: title, categoryColorCode: bgColor });
    }
    setModalOpen(false);
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

  const clicked = addTodo || !disable;

  return (
    <>
      <div className={styles[`todo-item${disable ? "--disable" : ""}`]}>
        <div
          ref={dropMenuRef}
          className={styles[`todo-item__category${clicked ? "--add" : ""}`]}
          // onClick={() => {
          //   if (!disable) setDropdownView(!isDropdownView);
          // }}
          onClick={handleOpen}
        >
          <div className={styles["todo-item__category-box"]} style={categoryStyle(categoryColorCode)}>
            {disable ? addTodo && <AddOutlined /> : <Text>{categoryTitle}</Text>}
          </div>
          {/* {isDropdownView && <CategorySelector handleClick={handleClickCategory} />} */}
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

      {/* 카테고리 모달 start */}
      <Modal open={ModalOpen} onClose={handleClose}>
        <CategorySelector handleClick={handleClickCategory} />
      </Modal>
      {/* 카테고리 모달 end */}
    </>
  );
};

export default TodoItem;
