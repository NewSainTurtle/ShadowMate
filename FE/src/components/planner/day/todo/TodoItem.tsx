import React, { useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import CategorySelector from "@components/common/CategorySelector";
import { AddOutlined, DeleteOutlined } from "@mui/icons-material";
import { BASIC_CATEGORY_ITEM } from "@store/planner/daySlice";
import { TodoConfig } from "@util/planner.interface";
import { CategoryItemConfig } from "@util/planner.interface";

interface Props {
  idx?: number;
  todoItem: TodoConfig;
  addTodo?: boolean;
  disable?: boolean;
  todoModule: {
    insertTodo: (props: TodoConfig) => void;
    updateTodo: (idx: number, props: TodoConfig) => void;
    deleteTodo: (idx: number, todoId: number) => void;
  };
}

const TodoItem = ({ idx = -1, todoItem, addTodo, disable, todoModule }: Props) => {
  const { todoContent, todoStatus } = todoItem;
  const category = (() => todoItem.category || BASIC_CATEGORY_ITEM)();
  const { categoryTitle, categoryColorCode } = category;
  const { insertTodo, updateTodo, deleteTodo } = todoModule;
  const [text, setText] = useState(todoContent);
  const dropMenuRef = useRef<HTMLDivElement>(null);
  const maxLength = 50;

  const [ModalOpen, setModalOpen] = useState<boolean>(false);
  const handleOpen = () => setModalOpen(true);
  const handleClose = () => setModalOpen(false);

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

  const handleClickCategory = (props: CategoryItemConfig) => {
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
    if (addTodo) {
      if (text === "") return;
      insertTodo({ ...todoItem, todoContent: text });
      setText("");
    } else {
      updateTodo(idx, { ...todoItem, todoContent: text });
    }
  };

  const handleSaveStatusTodo = () => {
    if (text === "") return;
    if (todoStatus == "완료") {
      updateTodo(idx, {
        ...todoItem,
        todoStatus: "미완료",
        timeTable: { ...todoItem.timeTable!, startTime: "", endTime: "" },
      });
    } else {
      updateTodo(idx, {
        ...todoItem,
        todoStatus: todoStatus == "공백" ? "완료" : "공백",
      });
    }
  };

  const clickDeleteTodo = () => {
    deleteTodo(idx, todoItem.todoId);
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

      <Modal types="noBtn" open={ModalOpen} onClose={handleClose}>
        <CategorySelector type="day" handleClick={handleClickCategory} />
      </Modal>
    </div>
  );
};

export default TodoItem;
