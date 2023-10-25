import React, { Dispatch, KeyboardEvent, MutableRefObject, SetStateAction, useEffect, useRef, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import { TodoConfig } from "@util/planner.interface";
import todoModule from "@util/TodoModule";

interface Props {
  todoItems: TodoConfig[];
  setTodoItems: Dispatch<SetStateAction<TodoConfig[]>>;
  nextId: MutableRefObject<number>;
}

const WeekItemInput = ({ todoItems, setTodoItems, nextId }: Props) => {
  const todoEndRef = useRef<HTMLDivElement | null>(null);
  const { insertTodo } = todoModule(todoItems, setTodoItems);
  const [newTodo, setNewTodo] = useState<string>("");

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (newTodo === "") return;
    if (e.key == "Enter") {
      if (e.nativeEvent.isComposing) return;
      insertTodo({ todoId: nextId.current, todoContent: newTodo, todoStatus: "공백" });
      nextId.current += 1;
      setNewTodo("");
    }
  };

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
        onKeyDown={(e) => handleOnKeyPress(e)}
        placeholder="💡 할 일을 입력하세요."
      />
      <svg style={{ cursor: "auto", height: "0" }} />
    </div>
  );
};

export default WeekItemInput;
