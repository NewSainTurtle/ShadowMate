import React, { useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import DeleteModal from "@components/common/Modal/DeleteModal";
import CategorySelector from "@components/common/CategorySelector";
import TimeTableDeleteModal from "@components/common/Modal/TimeTableDeleteModal";
import { AddOutlined, DeleteOutlined } from "@mui/icons-material";
import { BASIC_CATEGORY_ITEM } from "@store/planner/daySlice";
import { TodoConfig, CategoryItemConfig, TimeTableConfig } from "@util/planner.interface";

interface Props {
  idx?: number;
  todoItem: TodoConfig;
  addTodo?: boolean;
  disable?: boolean;
  todoModule: {
    insertTodo: (props: TodoConfig) => void;
    updateTodo: (idx: number, props: TodoConfig) => void;
    deleteTodo: (idx: number, todoId: number) => void;
    deleteTimeTable: (todoId: number, timeTables: TimeTableConfig[], todoStatus: TodoConfig["todoStatus"]) => void;
  };
}

const TodoItem = ({ idx = -1, todoItem, addTodo, disable, todoModule }: Props) => {
  const { todoContent, todoStatus } = todoItem;
  const category = (() => todoItem.category ?? BASIC_CATEGORY_ITEM)();
  const { categoryTitle, categoryColorCode } = category;
  const { insertTodo, updateTodo, deleteTodo, deleteTimeTable } = todoModule;
  const [text, setText] = useState(todoContent);
  const dropMenuRef = useRef<HTMLDivElement>(null);
  const maxLength = 50;

  const [ModalOpen, setModalOpen] = useState<boolean>(false);
  const handleOpen = () => {
    if (!addTodo && !disable) setModalOpen(true);
  };
  const handleClose = () => setModalOpen(false);

  const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
  const handleDeleteModalOpen = () => setDeleteModalOpen(true);
  const handleDeleteModalClose = () => setDeleteModalOpen(false);

  const [warnModalOpen, setWarnModalOpen] = useState<boolean>(false);
  const handleWarnModalOpen = () => setWarnModalOpen(true);
  const handleWarnModalClose = () => setWarnModalOpen(false);

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
    if (todoStatus === "완료") {
      if (todoItem.timeTables && todoItem.timeTables.length > 0) {
        handleWarnModalOpen();
      } else {
        updateTodo(idx, {
          ...todoItem,
          todoStatus: "미완료",
        });
      }
    } else {
      const changeStatus = {
        공백: "진행중",
        진행중: "완료",
        미완료: "공백",
      }[todoStatus] as "완료" | "진행중" | "공백";

      updateTodo(idx, {
        ...todoItem,
        todoStatus: changeStatus,
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

  const clicked = !addTodo && !disable;
  const isDisableStyle = disable ? "--disable" : "";
  const isClickedStyle = clicked ? "--add" : "";
  const todoStatusView = {
    공백: " ",
    완료: "O",
    진행중: "△",
    미완료: "X",
  };

  return (
    <div className={styles[`todo-item${isDisableStyle}`]}>
      <div ref={dropMenuRef} className={styles[`todo-item__category${isClickedStyle}`]} onClick={handleOpen}>
        <div className={styles["todo-item__category-box"]} style={categoryStyle(categoryColorCode)}>
          {disable ? addTodo && <AddOutlined /> : <Text>{categoryTitle}</Text>}
        </div>
      </div>

      <div className={styles[`todo-item__content${isClickedStyle}`]}>
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
              <div onClick={handleDeleteModalOpen}>
                <DeleteOutlined />
              </div>
            )}
          </div>
        )}
      </div>

      <div className={styles[`todo-item__checked${isClickedStyle}`]} onClick={handleSaveStatusTodo}>
        <Text types="semi-medium">{todoStatusView[todoStatus]}</Text>
      </div>

      <Modal types="noBtn" open={ModalOpen} onClose={handleClose}>
        <CategorySelector type="day" handleClick={handleClickCategory} addBtn />
      </Modal>
      <Modal
        types="twoBtn"
        open={deleteModalOpen}
        onClose={handleDeleteModalClose}
        onClick={clickDeleteTodo}
        onClickMessage="삭제"
        warning
      >
        <DeleteModal types="일정" />
      </Modal>
      <Modal
        types="twoBtn"
        open={warnModalOpen}
        onClose={handleWarnModalClose}
        onClick={() => {
          deleteTimeTable(todoItem.todoId, todoItem.timeTables as TimeTableConfig[], "미완료");
          handleWarnModalClose();
        }}
        onClickMessage="확인"
        warning
      >
        <TimeTableDeleteModal />
      </Modal>
    </div>
  );
};

export default TodoItem;
