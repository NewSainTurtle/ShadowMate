import React, { Dispatch, SetStateAction, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import DeleteModal from "@components/common/Modal/DeleteModal";
import CategorySelector from "@components/common/CategorySelector";
import TimeTableDeleteModal from "@components/common/Modal/TimeTableDeleteModal";
import { AddOutlined, DeleteOutlined, DragHandle } from "@mui/icons-material";
import { BASIC_CATEGORY_ITEM, selectDayDate, setTimeTable } from "@store/planner/daySlice";
import { TodoConfig, CategoryItemConfig, TimeTableConfig } from "@util/planner.interface";
import todoModule from "@util/TodoModule";
import { plannerApi } from "@api/Api";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";

interface Props {
  idx?: number;
  item: TodoConfig;
  dailyTodos: TodoConfig[];
  setDailyTodos: Dispatch<SetStateAction<TodoConfig[]>>;
  addTodo?: boolean;
  disable?: boolean;
  dragModule?: {
    childMouseEnter: () => void;
    childMouseLeave: () => void;
  };
}

const TodoItem = ({ idx = -1, item, addTodo, disable, dragModule, dailyTodos, setDailyTodos }: Props) => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const date = useAppSelector(selectDayDate);
  const { todoContent, todoStatus } = item;
  const category = (() => item.category ?? BASIC_CATEGORY_ITEM)();
  const { categoryTitle, categoryColorCode } = category;
  const [text, setText] = useState(todoContent);
  const [isEdit, setIsEdit] = useState(false);
  const dropMenuRef = useRef<HTMLDivElement>(null);
  const { insertTodo, updateTodo, deleteTodo } = todoModule(dailyTodos, setDailyTodos);
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

  const onClickCategory = (props: CategoryItemConfig) => {
    const newCategory = { ...item, category: props };
    if (addTodo) handleSaveInsert(newCategory);
    else handleSaveUpdate(idx, newCategory);
    setModalOpen(false);
  };

  const handleOnKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      if (addTodo) {
        if (text === "") return;
        handleSaveInsert({ ...item, todoContent: text });
        setText("");
      } else (document.activeElement as HTMLElement).blur();
    }
  };

  const onBlurTextTodo = () => {
    if (text === "") return;
    if (addTodo) {
      handleSaveInsert({ ...item, todoContent: text });
      setText("");
    } else handleSaveUpdate(idx, { ...item, todoContent: text });
    setIsEdit(false);
  };

  const onClickStatusTodo = () => {
    if (text === "") return;
    if (todoStatus === "완료") {
      if (item.timeTables && item.timeTables.length > 0) handleWarnModalOpen();
      else {
        handleSaveUpdate(idx, {
          ...item,
          todoStatus: "미완료",
        });
      }
      return;
    }

    const changeStatus = {
      공백: "진행중",
      진행중: "완료",
      미완료: "공백",
    }[todoStatus] as "완료" | "진행중" | "공백";

    handleSaveUpdate(idx, {
      ...item,
      todoStatus: changeStatus,
    });
  };

  const handleSaveInsert = async (init: TodoConfig) => {
    await plannerApi
      .addDailyTodos(userId, {
        date,
        todoContent: init.todoContent,
        categoryId: init.category!.categoryId,
      })
      .then((res) => {
        const returnId = res.data.data["todoId"];
        insertTodo({ ...init, todoId: returnId });
      })
      .catch((err) => console.error(err));
  };

  const handleSaveUpdate = async (idx: number, init: TodoConfig) => {
    await plannerApi
      .editDailyTodos(userId, {
        date,
        todoId: init.todoId,
        categoryId: init.category?.categoryId ?? 0,
        todoContent: init.todoContent,
        todoStatus: init.todoStatus,
      })
      .then(() => {
        updateTodo(idx, init);
      });
  };

  const handleSaveDelete = async () => {
    await plannerApi
      .deleteDailyTodos(userId, {
        date,
        todoId: item.todoId,
      })
      .then(() => {
        deleteTodo(idx);
      });
  };

  /**
   * todo 상태를 "미완료"로 변경시 등록된 타임테이블 모두 삭제
   */
  const deleteTimeTable = (todoId: number, timeTables: TimeTableConfig[]) => {
    timeTables.map(async (item) => {
      const { timeTableId } = item;
      await plannerApi
        .deleteTimetable(userId, { date, todoId, timeTableId })
        .then(() => dispatch(setTimeTable({ todoId, timeTableId, startTime: "", endTime: "", todoStatus: "미완료" })));
    });
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
    완료: "○",
    진행중: "△",
    미완료: "⨉",
  };

  return (
    <div className={styles[`todo-item${isDisableStyle}`]}>
      <div ref={dropMenuRef} className={styles[`todo-item__category${isClickedStyle}`]} onClick={handleOpen}>
        <div className={styles["todo-item__category-box"]} style={categoryStyle(categoryColorCode)}>
          {disable ? addTodo && <AddOutlined /> : <Text>{categoryTitle}</Text>}
        </div>
      </div>

      <div className={styles[`todo-item__content${isClickedStyle}`]} onClick={() => setIsEdit(true)}>
        {disable ? (
          addTodo && (
            <span>
              <AddOutlined />
            </span>
          )
        ) : (
          <div className={styles["todo-item__content__possible"]}>
            {addTodo || isEdit ? (
              <input
                value={text}
                placeholder={"할 일을 입력하세요"}
                minLength={2}
                maxLength={maxLength}
                onChange={editText}
                onKeyDown={handleOnKeyPress}
                onBlur={() => onBlurTextTodo()}
                autoFocus
              />
            ) : (
              <div className={styles["todo-item__content--read"]}>{text}</div>
            )}

            {!addTodo && (
              <div className={styles["todo-item__content__icons"]}>
                <div
                  onMouseEnter={() => dragModule?.childMouseEnter()}
                  onMouseLeave={() => dragModule?.childMouseLeave()}
                >
                  <DragHandle />
                </div>
                <div
                  onClick={(e) => {
                    e.stopPropagation();
                    handleDeleteModalOpen();
                  }}
                >
                  <DeleteOutlined />
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      <div className={styles[`todo-item__checked${isClickedStyle}`]} onClick={onClickStatusTodo}>
        <Text types="semi-medium">{todoStatusView[todoStatus]}</Text>
      </div>

      <Modal types="noBtn" open={ModalOpen} onClose={handleClose}>
        <CategorySelector type="day" handleClick={onClickCategory} addBtn />
      </Modal>
      <Modal
        types="twoBtn"
        open={deleteModalOpen}
        onClose={handleDeleteModalClose}
        onClick={handleSaveDelete}
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
          deleteTimeTable(item.todoId, item.timeTables as TimeTableConfig[]);
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
