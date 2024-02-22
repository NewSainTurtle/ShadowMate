import React, { Dispatch, KeyboardEvent, SetStateAction, useEffect, useRef, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import todoModule from "@util/TodoModule";
import dayjs from "dayjs";
import { TodoConfig } from "@util/planner.interface";
import { useAppSelector } from "@hooks/hook";
import { selectUserId } from "@store/authSlice";
import { plannerApi } from "@api/Api";

interface Props {
  date: string;
  dailyTodos: TodoConfig[];
  setDailyTodos: Dispatch<SetStateAction<TodoConfig[]>>;
}

const WeekItemInput = ({ date, dailyTodos, setDailyTodos }: Props) => {
  const userId = useAppSelector(selectUserId);
  const todoEndRef = useRef<HTMLDivElement | null>(null);
  const [newTodo, setNewTodo] = useState<string>("");
  const { insertTodo } = todoModule(dailyTodos, setDailyTodos);
  const maxLength = 50;

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (newTodo === "") return;
    if (e.key == "Enter") {
      if (e.nativeEvent.isComposing) return;
      const init = {
        date: dayjs(date).format("YYYY-MM-DD"),
        todoContent: newTodo,
        categoryId: 0,
      };
      handleSave(init);
    }
  };

  const handleOnBlur = () => {
    if (newTodo === "") return;
    const init = {
      date: dayjs(date).format("YYYY-MM-DD"),
      todoContent: newTodo,
      categoryId: 0,
    };
    handleSave(init);
  };

  const handleSave = (init: { date: string; todoContent: string; categoryId: number }) => {
    plannerApi
      .addDailyTodos(userId, init)
      .then((res) => {
        const returnId = res.data.data.todoId;
        insertTodo({ todoId: returnId, todoContent: newTodo, todoStatus: "공백" });
        setNewTodo("");
      })
      .catch((err) => console.error(err));
  };

  useEffect(() => {
    todoEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [newTodo]);

  return (
    <div ref={todoEndRef} className={styles["item__todo-item"]}>
      <div style={{ cursor: "default" }}>
        <span style={{ visibility: "hidden" }}>💻</span>
      </div>
      <input
        type="text"
        value={newTodo}
        maxLength={maxLength}
        onChange={(e) => setNewTodo(e.target.value)}
        onKeyDown={(e) => handleOnKeyPress(e)}
        onBlur={handleOnBlur}
        placeholder="💡 할 일을 입력하세요."
      />
      <svg style={{ cursor: "auto", height: "0" }} />
    </div>
  );
};

export default WeekItemInput;
