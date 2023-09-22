import React, { KeyboardEvent, useEffect, useRef, useState } from "react";
import styles from "../week/Week.module.scss";
import Text from "@components/common/Text";

interface WeekTodoItemConfig {
  weeklyTodoContent: string;
  weeklyTodoStatus: boolean;
}

const WeekTodo = () => {
  const [todoItems, setTodoItems] = useState<WeekTodoItemConfig[]>([
    { weeklyTodoContent: "국어 졸라게 열심히 풀기", weeklyTodoStatus: true },
    { weeklyTodoContent: "수학 갈아버리기", weeklyTodoStatus: false },
    { weeklyTodoContent: "영어 처참하게 눌러버리기", weeklyTodoStatus: false },
  ]);
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
    <div className={styles.weekTodo_container}>
      <div className={styles.weekItem_title}>
        <Text>주차별 할 일</Text>
      </div>
      <div
        className={styles.weekItem_todoList}
        style={{ gridTemplateRows: `repeat(${todoItems.length + 1}, calc(100% / 7)` }}
      >
        {todoItems.map((item: WeekTodoItemConfig, key: number) => (
          <div className={styles.weekTodo_todoItem} key={key}>
            <div className={styles.weekTodo_checkbox}>
              <input type="checkbox" id={key.toString()} checked={item.weeklyTodoStatus} />
              <label htmlFor={key.toString()}>
                <Text types="small">{item.weeklyTodoContent}</Text>
              </label>
            </div>
          </div>
        ))}
        <div ref={todoEndRef} className={styles.weekTodo_todoItem}>
          <div className={styles.weekTodo_checkbox}>
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
