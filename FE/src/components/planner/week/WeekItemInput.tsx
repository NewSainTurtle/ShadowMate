import React, { Dispatch, SetStateAction, useEffect, useRef } from "react";
import styles from "@styles/planner/Week.module.scss";

interface Props {
  newTodo: string;
  setNewTodo: Dispatch<SetStateAction<string>>;
}

const WeekItemInput = ({ newTodo, setNewTodo }: Props) => {
  const todoEndRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    todoEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [newTodo]);

  return (
    <div ref={todoEndRef} className={styles["item__todo-item"]}>
      <div>
        <span style={{ visibility: "hidden" }}>💻</span>
      </div>
      <input
        type="text"
        value={newTodo}
        onChange={(e) => setNewTodo(e.target.value)}
        placeholder="💡 할 일을 입력하세요."
      />
      <svg style={{ cursor: "auto", height: "0" }} />
    </div>
  );
};

export default WeekItemInput;
