import React, { KeyboardEvent, useEffect, useRef, useState } from "react";
import styles from "../week/Week.module.scss";
import Text from "@components/common/Text";
import { WeekTodoItemConfig } from "@util/planner.interface";
import { WEEK_TODO_ITEMS } from "@util/data/WeekTodos";

const WeekTodo = () => {
  const [todoItems, setTodoItems] = useState<WeekTodoItemConfig[]>(WEEK_TODO_ITEMS);
  const [newTodo, setNewTodo] = useState<string>("");
  const todoEndRef = useRef<HTMLDivElement | null>(null);

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (newTodo === "") return;
    if (e.key == "Enter") {
      if (e.nativeEvent.isComposing) return;
      setTodoItems([...todoItems, { weeklyTodoContent: newTodo, weeklyTodoStatus: false }]);
      setNewTodo("");
    }
  };

  useEffect(() => {
    todoEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [newTodo]);

  return (
    <div className={styles["todo"]}>
      <div className={styles["item__title"]}>
        <Text>주차별 할 일</Text>
      </div>
      <div
        className={styles["item__todo-list"]}
        style={{ gridTemplateRows: `repeat(${todoItems.length + 1}, calc(100% / 7)` }}
      >
        {todoItems.map((item: WeekTodoItemConfig, key: number) => (
          <div className={styles["todo__item"]} key={key}>
            <div className={styles["todo__checkbox"]}>
              <input type="checkbox" id={key.toString()} defaultChecked={item.weeklyTodoStatus} />
              <label htmlFor={key.toString()}>
                <Text types="small">{item.weeklyTodoContent}</Text>
              </label>
            </div>
          </div>
        ))}
        <div ref={todoEndRef} className={styles["todo__item"]}>
          <div className={styles["todo__checkbox"]}>
            <input type="checkbox" style={{ cursor: "auto" }} disabled />
            <input
              type="text"
              width={"100%"}
              value={newTodo}
              onChange={(e) => setNewTodo(e.target.value)}
              onKeyDown={(e) => handleOnKeyPress(e)}
              placeholder="이번 주에 해야할 일을 입력하세요."
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default WeekTodo;
