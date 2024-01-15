import React, { ChangeEvent, Dispatch, KeyboardEvent, SetStateAction, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import DeleteModal from "@components/common/Modal/DeleteModal";
import CategorySelector from "@components/common/CategorySelector";
import todoModule from "@util/TodoModule";
import dayjs from "dayjs";
import { useAppSelector } from "@hooks/hook";
import { CategoryItemConfig, TodoConfig } from "@util/planner.interface";
import { selectUserId } from "@store/authSlice";
import { plannerApi } from "@api/Api";
import { DeleteOutlined } from "@mui/icons-material";

interface Props {
  idx: number;
  item: TodoConfig;
  isMine: boolean;
  date: string;
  dailyTodos: TodoConfig[];
  setDailyTodos: Dispatch<SetStateAction<TodoConfig[]>>;
}

const WeekItem = ({ idx, item, isMine, date, dailyTodos, setDailyTodos }: Props) => {
  const userId = useAppSelector(selectUserId);
  const [Modalopen, setModalOpen] = useState(false);
  const handleClose = () => setModalOpen(false);
  const [todo, setTodo] = useState({
    newTodo: "",
    oldTodo: "",
  });
  const { updateTodo, deleteTodo } = todoModule(dailyTodos, setDailyTodos);
  const friend = isMine ? "" : "--friend";

  const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
  const handleDeleteModalOpen = () => setDeleteModalOpen(true);
  const handleDeleteModalClose = () => setDeleteModalOpen(false);

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
      categoryId: item.category?.categoryId ?? 0,
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
    const data = {
      date: dayjs(date).format("YYYY-MM-DD"),
      todoId: item.todoId,
    };
    plannerApi
      .deleteDailyTodos(userId, data)
      .then(() => {
        deleteTodo(idx);
        handleDeleteModalClose();
      })
      .catch((err) => console.log(err));
  };

  const handleClickCategory = (props: CategoryItemConfig) => {
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
    if (status === "공백") return " ";
    if (status === "완료") return "O";
    return status === "진행중" ? "△" : "X";
  };

  const setNewTodoStatus = (status: string) => {
    if (status === "공백") return "진행중";
    if (status === "진행중") return "완료";
    return status === "완료" ? "미완료" : "공백";
  };

  const handleStatusSave = () => {
    const newStatus: TodoConfig["todoStatus"] = setNewTodoStatus(item.todoStatus);
    const data = {
      date: dayjs(date).format("YYYY-MM-DD"),
      todoId: item.todoId,
      todoContent: item.todoContent,
      categoryId: item.category?.categoryId ?? 0,
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
      <div className={styles[`item__todo-item${friend}`]}>
        <div onClick={() => setModalOpen(!Modalopen)}>
          <span>{item.category?.categoryEmoticon ?? ""}</span>
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
        <DeleteOutlined onClick={handleDeleteModalOpen} />
        <div onClick={handleStatusSave}>
          <Text>{setStatus(item.todoStatus)}</Text>
        </div>
      </div>
      <Modal types="noBtn" open={Modalopen} onClose={handleClose}>
        <CategorySelector type="week" handleClick={handleClickCategory} />
      </Modal>
      <Modal
        types="twoBtn"
        open={deleteModalOpen}
        onClose={handleDeleteModalClose}
        onClick={handleDelete}
        onClickMessage="삭제"
        warning
      >
        <DeleteModal types="일정" />
      </Modal>
    </>
  );
};

export default WeekItem;
