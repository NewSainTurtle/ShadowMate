import React, { ChangeEvent, Dispatch, KeyboardEvent, SetStateAction, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import CategorySelector from "@components/common/CategorySelector";
import todoModule from "@util/TodoModule";
import dayjs from "dayjs";
import { useAppSelector } from "@hooks/hook";
import { CategoryConfig, TodoConfig } from "@util/planner.interface";
import { selectUserId } from "@store/authSlice";
import { plannerApi } from "@api/Api";
import { DeleteOutlined } from "@mui/icons-material";

interface Props {
  idx: number;
  item: TodoConfig;
  date: string;
  dailyTodos: TodoConfig[];
  setDailyTodos: Dispatch<SetStateAction<TodoConfig[]>>;
}

const WeekItem = ({ idx, item, date, dailyTodos, setDailyTodos }: Props) => {
  const userId = useAppSelector(selectUserId);
  const [Modalopen, setModalOpen] = useState(false);
  const handleClose = () => setModalOpen(false);
  const [todo, setTodo] = useState({
    newTodo: "",
    oldTodo: "",
  });
  const { updateTodo, deleteTodo } = todoModule(dailyTodos, setDailyTodos);

  const handleEnter = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      (document.activeElement as HTMLElement).blur();
    }
  };

  const handleUpdateState = () => {
    setTodo((prev) => {
      return { ...prev, oldTodo: item.todoContent };
    });
    updateTodo(idx, { ...item, todoUpdate: !item.todoUpdate });
  };

  const handleUpdateSave = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.value === "") e.target.value = todo.oldTodo;
    const data = {
      date: dayjs(date).format("YYYY-MM-DD"),
      todoId: item.todoId,
      todoContent: e.target.value,
      categoryId: item.category?.categoryId || 0,
      todoStatus: item.todoStatus,
    };
    plannerApi
      .editDailyTodos(userId, data)
      .then(() => {
        updateTodo(idx, { ...item, todoContent: e.target.value, todoUpdate: !item.todoUpdate });
      })
      .catch((err) => console.log(err));
  };

  const handleDelete = () => {
    deleteTodo(idx);
  };

  const handleClickCategory = (props: CategoryConfig) => {
    const newCategory = { ...item, category: props };
    const data = {
      date: dayjs(date).format("YYYY-MM-DD"),
      todoId: item.todoId,
      todoContent: item.todoContent,
      categoryId: newCategory.category.categoryId,
      todoStatus: item.todoStatus,
    };
    plannerApi
      .editDailyTodos(userId, data)
      .then(() => {
        updateTodo(idx, newCategory);
        setModalOpen(false);
      })
      .catch((err) => console.log(err));
  };

  const setStatus = (status: string) => {
    return status === "공백" ? " " : status === "완료" ? "O" : "X";
  };

  const handleStatusSave = () => {
    const newStatus: TodoConfig["todoStatus"] =
      item.todoStatus === "공백" ? "완료" : item.todoStatus === "완료" ? "미완료" : "공백";
    const data = {
      date: dayjs(date).format("YYYY-MM-DD"),
      todoId: item.todoId,
      todoContent: item.todoContent,
      categoryId: item.category?.categoryId || 0,
      todoStatus: newStatus,
    };
    plannerApi
      .editDailyTodos(userId, data)
      .then(() => {
        updateTodo(idx, {
          ...item,
          todoStatus: newStatus,
        });
      })
      .catch((err) => console.log(err));
  };

  return (
    <>
      <div className={styles["item__todo-item"]}>
        <div onClick={() => setModalOpen(!Modalopen)}>
          <span>{item.category?.categoryEmoticon || ""}</span>
        </div>
        {item.todoUpdate ? (
          <input
            className={styles["item__edit-input"]}
            autoFocus
            type="text"
            defaultValue={item.todoContent}
            onChange={(e) =>
              setTodo((prev) => {
                return { ...prev, newTodo: e.target.value };
              })
            }
            onKeyDown={handleEnter}
            onBlur={handleUpdateSave}
          />
        ) : (
          <div onClick={handleUpdateState}>
            <Text types="small">{item?.todoContent}</Text>
          </div>
        )}
        <DeleteOutlined onClick={handleDelete} />
        <div onClick={handleStatusSave}>
          <Text>{setStatus(item.todoStatus)}</Text>
        </div>
      </div>
      <Modal open={Modalopen} onClose={handleClose}>
        <CategorySelector type="week" handleClick={handleClickCategory} />
      </Modal>
    </>
  );
};

export default WeekItem;
