import React, { ChangeEvent, KeyboardEvent, useEffect, useRef, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectThisWeek, selectWeeklyTodos, setWeeklyTodos } from "@store/planner/weekSlice";
import { WeekTodoItemConfig } from "@util/planner.interface";
import { plannerApi } from "@api/Api";
import { selectUserId } from "@store/authSlice";
import { getThisWeek } from "@util/getThisWeek";

const WeekTodoInput = () => {
  const dispatch = useAppDispatch();
  const weeklyTodos: WeekTodoItemConfig[] = useAppSelector(selectWeeklyTodos);
  const userId: number = useAppSelector(selectUserId);
  const thisWeek = useAppSelector(selectThisWeek);
  const todoEndRef = useRef<HTMLDivElement | null>(null);
  const [todo, setTodo] = useState<string>("");
  const dates = getThisWeek(thisWeek);

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (todo === "") return;
    if (e.key != "Enter") return;
    if (e.nativeEvent.isComposing) return;
    setWeeklyTodo();
  };

  const handleOnBlur = (e: ChangeEvent<HTMLInputElement>) => {
    if (todo === "") return;
    setWeeklyTodo();
  };

  const setWeeklyTodo = () => {
    plannerApi
      .addWeeklyTodos(userId, {
        startDate: dates[0],
        endDate: dates[1],
        weeklyTodoContent: todo,
      })
      .then((res) => {
        const returnId = res.data.data.weeklyTodoId;
        dispatch(
          setWeeklyTodos([
            ...weeklyTodos,
            {
              weeklyTodoId: returnId,
              weeklyTodoContent: todo,
              weeklyTodoStatus: false,
              weeklyTodoUpdate: false,
            },
          ]),
        );
        setTodo("");
      })
      .catch((err) => console.log(err));
  };

  useEffect(() => {
    todoEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [todo]);

  return (
    <div ref={todoEndRef} className={styles["todo__item"]}>
      <div className={styles["todo__checkbox"]}>
        <div style={{ visibility: "hidden" }}>
          <input type="checkbox" style={{ cursor: "auto" }} disabled />
        </div>
        <input
          autoFocus
          type="text"
          width={"100%"}
          value={todo}
          onChange={(e) => setTodo(e.target.value)}
          onKeyDown={(e) => handleOnKeyPress(e)}
          onBlur={handleOnBlur}
          placeholder="이번 주에 해야할 일을 입력하세요."
        />
      </div>
    </div>
  );
};

export default WeekTodoInput;
