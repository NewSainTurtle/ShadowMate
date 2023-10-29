import React, { ChangeEvent, KeyboardEvent, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import { WeekTodoItemConfig } from "@util/planner.interface";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectWeeklyTodos, setWeeklyTodos } from "@store/planner/weekSlice";
import { DeleteOutlined } from "@mui/icons-material";

interface Props {
  idx: number;
  item: WeekTodoItemConfig;
}

const WeekTodoItem = ({ idx, item }: Props) => {
  const dispatch = useAppDispatch();
  const weeklyTodos: WeekTodoItemConfig[] = useAppSelector(selectWeeklyTodos);
  const [todo, setTodo] = useState({
    newTodo: "",
    oldTodo: "",
  });

  const handleEnter = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      (document.activeElement as HTMLElement).blur();
    }
  };

  const handleUpdateState = () => {
    setTodo((prev) => {
      return { ...prev, oldTodo: weeklyTodos[idx].weeklyTodoContent };
    });
    let copyTodos = [...weeklyTodos];
    copyTodos[idx] = { ...copyTodos[idx], weeklyTodoUpdate: !copyTodos[idx].weeklyTodoUpdate };
    dispatch(setWeeklyTodos(copyTodos));
  };

  const handleUpdateSave = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.value === "") {
      e.target.value = todo.oldTodo;
      return;
    }
    let copyTodos = [...weeklyTodos];
    copyTodos[idx] = {
      ...copyTodos[idx],
      weeklyTodoContent: e.target.value,
      weeklyTodoUpdate: !copyTodos[idx].weeklyTodoUpdate,
    };
    dispatch(setWeeklyTodos(copyTodos));
  };

  const handleDelete = () => {
    let newTodos = weeklyTodos.filter((item, i) => idx != i);
    dispatch(setWeeklyTodos(newTodos));
  };

  return (
    <div className={styles["todo__item"]} key={item.weeklyTodoId}>
      <div className={styles["todo__checkbox"]}>
        <input type="checkbox" defaultChecked={item.weeklyTodoStatus} />
        {item.weeklyTodoUpdate ? (
          <input
            autoFocus
            type="text"
            defaultValue={item.weeklyTodoContent}
            onChange={(e) =>
              setTodo((prev) => {
                return { ...prev, newTodo: e.target.value };
              })
            }
            onKeyDown={(e) => handleEnter(e)}
            onBlur={(e) => handleUpdateSave(e)}
          />
        ) : (
          <div className={styles["todo__name"]} onClick={handleUpdateState}>
            <Text types="small">{item.weeklyTodoContent}</Text>
          </div>
        )}
      </div>
      <DeleteOutlined onClick={handleDelete} />
    </div>
  );
};

export default WeekTodoItem;
