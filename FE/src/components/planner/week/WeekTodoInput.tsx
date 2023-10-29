import React, { KeyboardEvent, useEffect, useRef, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectWeeklyTodos, setWeeklyTodos } from "@store/planner/weekSlice";
import { WeekTodoItemConfig } from "@util/planner.interface";

const WeekTodoInput = () => {
  const dispatch = useAppDispatch();
  const weeklyTodos: WeekTodoItemConfig[] = useAppSelector(selectWeeklyTodos);
  const [todo, setTodo] = useState<string>("");
  const todoEndRef = useRef<HTMLDivElement | null>(null);
  const nextId = useRef<number>(weeklyTodos.length);

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (todo === "") return;
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      dispatch(
        setWeeklyTodos([
          ...weeklyTodos,
          {
            weeklyTodoId: nextId.current,
            weeklyTodoContent: todo,
            weeklyTodoStatus: false,
            weeklyTodoUpdate: false,
          },
        ]),
      );
      setTodo("");
      nextId.current += 1;
    }
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
          placeholder="이번 주에 해야할 일을 입력하세요."
        />
      </div>
    </div>
  );
};

export default WeekTodoInput;
