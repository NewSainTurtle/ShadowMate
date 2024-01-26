import React, { ChangeEvent, KeyboardEvent, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import Modal from "@components/common/Modal";
import DeleteModal from "@components/common/Modal/DeleteModal";
import { WeekTodoItemConfig } from "@util/planner.interface";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectThisWeek, selectWeeklyTodos, setWeeklyTodos } from "@store/planner/weekSlice";
import { DeleteOutlined } from "@mui/icons-material";
import { selectUserId } from "@store/authSlice";
import { getThisWeek } from "@util/getThisWeek";
import { plannerApi } from "@api/Api";

interface Props {
  id: number;
  idx: number;
  item: WeekTodoItemConfig;
  isMine: boolean;
}

const WeekTodoItem = ({ id, idx, item, isMine }: Props) => {
  const dispatch = useAppDispatch();
  const weeklyTodos: WeekTodoItemConfig[] = useAppSelector(selectWeeklyTodos);
  const userId: number = useAppSelector(selectUserId);
  const thisWeek = useAppSelector(selectThisWeek);
  const dates = getThisWeek(thisWeek);
  const [todo, setTodo] = useState({
    newTodo: "",
    oldTodo: "",
  });
  const [checked, setChecked] = useState<boolean>(item.weeklyTodoStatus);
  const friend = isMine ? "" : "--friend";
  const maxLength = 50;

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
    let copyTodos = [...weeklyTodos];
    setTodo({ ...todo, oldTodo: copyTodos[idx].weeklyTodoContent });
    copyTodos[idx] = { ...copyTodos[idx], weeklyTodoUpdate: !copyTodos[idx].weeklyTodoUpdate };
    dispatch(setWeeklyTodos(copyTodos));
  };

  const handleUpdateStatus = (e: ChangeEvent<HTMLInputElement>) => {
    putUpdateWeeklyTodoStatus();
  };

  const handleUpdateSave = (e: ChangeEvent<HTMLInputElement>) => {
    let content = e.target.value;
    if (content === "") content = todo.oldTodo;

    let copyTodos = [...weeklyTodos];
    copyTodos[idx] = {
      ...copyTodos[idx],
      weeklyTodoContent: content,
      weeklyTodoUpdate: !copyTodos[idx].weeklyTodoUpdate,
    };
    dispatch(setWeeklyTodos(copyTodos));
    putUpdateWeeklyTodo(content);
  };

  const handleDelete = () => {
    let newTodos = weeklyTodos.filter((item, i) => idx != i);
    dispatch(setWeeklyTodos(newTodos));
    deleteWeeklyTodo();
  };

  const putUpdateWeeklyTodo = (content: string) => {
    plannerApi
      .editWeeklyTodos(userId, {
        startDate: dates[0],
        endDate: dates[1],
        weeklyTodoId: id,
        weeklyTodoContent: content,
      })
      .then()
      .catch((err) => console.log(err));
  };

  const putUpdateWeeklyTodoStatus = () => {
    plannerApi
      .weeklyTodosStatus(userId, {
        startDate: dates[0],
        endDate: dates[1],
        weeklyTodoId: id,
        weeklyTodoStatus: !checked,
      })
      .then((res) => {
        setChecked(!checked);
      })
      .catch((err) => console.log(err));
  };

  const deleteWeeklyTodo = () => {
    plannerApi
      .deleteWeeklyTodos(userId, { startDate: dates[0], endDate: dates[1], weeklyTodoId: id })
      .then()
      .catch((err) => console.log(err));
  };

  return (
    <>
      <div className={styles[`todo__item${friend}`]} key={item.weeklyTodoId}>
        <div className={styles["todo__checkbox"]}>
          <input
            type="checkbox"
            checked={checked}
            onChange={(e) => {
              handleUpdateStatus(e);
            }}
          />
          {item.weeklyTodoUpdate ? (
            <input
              autoFocus
              type="text"
              maxLength={maxLength}
              defaultValue={item.weeklyTodoContent}
              onChange={(e) => setTodo({ ...todo, newTodo: e.target.value })}
              onKeyDown={(e) => handleEnter(e)}
              onBlur={(e) => handleUpdateSave(e)}
            />
          ) : (
            <div className={styles["todo__name"]} onClick={handleUpdateState}>
              <Text types="small">{item.weeklyTodoContent}</Text>
            </div>
          )}
        </div>
        <DeleteOutlined onClick={handleDeleteModalOpen} />
      </div>
      <Modal
        types="twoBtn"
        open={deleteModalOpen}
        onClose={handleDeleteModalClose}
        onClick={handleDelete}
        onClickMessage="삭제"
        warning
      >
        <DeleteModal types="할 일" />
      </Modal>
    </>
  );
};

export default WeekTodoItem;
