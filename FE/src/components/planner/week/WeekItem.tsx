import React, { ChangeEvent, KeyboardEvent, useEffect, useRef, useState } from "react";
import styles from "./Week.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import { TodoItemConfig } from "@util/planner.interface";
import { TODO_ITEMS } from "@util/data/WeekTodos";
import { DayInfoConfig } from "@util/getThisWeek";
import { DeleteOutlined } from "@mui/icons-material";

interface Props {
  date: DayInfoConfig;
}

const WeekItem = ({ date }: Props) => {
  const todoEndRef = useRef<HTMLDivElement | null>(null);
  const [newTodo, setNewTodo] = useState<string>("");
  const [todoItems, setTodoItems] = useState<TodoItemConfig[]>(TODO_ITEMS);

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (newTodo === "") return;
    if (e.key == "Enter") {
      if (e.nativeEvent.isComposing) return;
      setTodoItems([...todoItems, { todoContents: newTodo, todoStatus: false, todoUpdate: false }]);
      setNewTodo("");
    }
  };

  const handleEditState = (idx: number) => {
    setTodoItems(
      todoItems.map((item, key) => {
        if (key === idx) {
          return { ...item, todoUpdate: !item.todoUpdate };
        }
        return item;
      }),
    );
  };

  const handleEnter = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      (document.activeElement as HTMLElement).blur();
    }
  };

  const handleEditSave = (idx: number, e: ChangeEvent<HTMLInputElement>) => {
    setTodoItems(
      todoItems.map((item, key) => {
        if (key === idx) {
          return { ...item, todoContents: e.target.value, todoUpdate: !item.todoUpdate };
        }
        return item;
      }),
    );
  };

  const handleDelete = (idx: number) => {
    setTodoItems(todoItems.filter((item, key) => idx !== key));
  };

  useEffect(() => {
    todoEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [newTodo]);

  return (
    <div className={styles["item"]}>
      <div className={styles["item__title"]}>
        <Text>
          {date.month + 1}월 {date.day}일 ({date.dayOfWeek})
        </Text>
        <Dday comparedDate={new Date(date.year, date.month, date.day)} />
      </div>
      <div className={styles["item__todo-list"]} style={{ gridTemplateRows: `repeat(${todoItems.length + 1}, 20%` }}>
        {todoItems.map((item: TodoItemConfig, key: number) => (
          <div className={styles["item__todo-item"]} key={key}>
            <div>💻</div>
            {item.todoUpdate ? (
              <input
                autoFocus
                type="text"
                defaultValue={item.todoContents}
                onKeyDown={(e) => handleEnter(e)}
                onBlur={(e) => handleEditSave(key, e)}
              />
            ) : (
              <span onClick={() => handleEditState(key)}>
                <Text types="small">{item.todoContents}</Text>
              </span>
            )}
            <DeleteOutlined onClick={() => handleDelete(key)} />
            <div>{item.todoStatus ? "O" : "X"}</div>
          </div>
        ))}
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
          <svg style={{ cursor: "auto" }} />
        </div>
      </div>
      <div className={`${styles["item__memo"]} ${todoItems.length < 4 && styles["top_border"]}`}>
        <textarea placeholder="💡 오늘의 메모를 입력하세요." />
      </div>
    </div>
  );
};

export default WeekItem;
