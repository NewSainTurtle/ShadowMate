import React, { KeyboardEvent, useEffect, useRef, useState } from "react";
import styles from "./Week.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import { TodoItemConfig } from "@util/planner.interface";
import { TODO_ITEMS } from "@util/data/WeekTodos";

interface Props {
  date: string;
}

const WeekItem = ({ date }: Props) => {
  const todoEndRef = useRef<HTMLDivElement | null>(null);
  const [newTodo, setNewTodo] = useState<string>("");
  const [todoItems, setTodoItems] = useState<TodoItemConfig[]>(TODO_ITEMS);

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (newTodo === "") return;
    if (e.key == "Enter") {
      if (e.nativeEvent.isComposing) return;
      setTodoItems([...todoItems, { todoContents: newTodo, todoStatus: false }]);
      setNewTodo("");
    }
  };

  useEffect(() => {
    todoEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [newTodo]);

  return (
    <div className={styles["item"]}>
      <div className={styles["item__title"]}>
        <Text>{date}</Text>
        {/* <Dday comparedDate={date} /> */}
      </div>
      <div className={styles["item__todo-list"]} style={{ gridTemplateRows: `repeat(${todoItems.length + 1}, 20%` }}>
        {todoItems.map((item: TodoItemConfig, key: number) => (
          <div className={styles["item__todo-item"]} key={key}>
            <div>💻</div>
            <Text types="small">{item.todoContents}</Text>
            <div>{item.todoStatus ? "O" : "X"}</div>
          </div>
        ))}
        <div ref={todoEndRef} className={styles["item__todo-item"]}>
          <div></div>
          <input
            type="text"
            value={newTodo}
            onChange={(e) => setNewTodo(e.target.value)}
            onKeyDown={(e) => handleOnKeyPress(e)}
            placeholder="💡 할 일을 입력하세요."
          />
        </div>
      </div>
      <div className={`${styles["item__memo"]} ${todoItems.length < 4 && styles["top_border"]}`}>
        <textarea placeholder="💡 오늘의 메모를 입력하세요." />
      </div>
    </div>
  );
};

export default WeekItem;
