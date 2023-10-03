import React, { ChangeEvent, FocusEvent, KeyboardEvent, useCallback, useEffect, useRef, useState } from "react";
import styles from "../week/Week.module.scss";
import Text from "@components/common/Text";
import { WeekTodoItemConfig } from "@util/planner.interface";
import { WEEK_TODO_ITEMS } from "@util/data/WeekTodos";
import { DeleteOutlined } from "@mui/icons-material";

const WeekTodo = () => {
  const [todoItems, setTodoItems] = useState<WeekTodoItemConfig[]>(WEEK_TODO_ITEMS);
  const [newTodo, setNewTodo] = useState<string>("");
  const [oldTodo, setOldTodo] = useState<string>("");
  const todoEndRef = useRef<HTMLDivElement | null>(null);

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (newTodo === "") return;
    if (e.key === "Enter") {
      if (e.nativeEvent.isComposing) return;
      setTodoItems([...todoItems, { weeklyTodoContent: newTodo, weeklyTodoStatus: false, weeklyTodoUpdate: false }]);
      setNewTodo("");
    }
  };

  const handleEditState = (idx: number) => {
    setTodoItems(
      todoItems.map((item, key) => {
        if (key === idx) {
          setOldTodo(item.weeklyTodoContent);
          return { ...item, weeklyTodoUpdate: !item.weeklyTodoUpdate };
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
    if (e.target.value === "") e.target.value = oldTodo;
    setTodoItems(
      todoItems.map((item, key) => {
        if (key === idx) {
          return { ...item, weeklyTodoContent: e.target.value, weeklyTodoUpdate: !item.weeklyTodoUpdate };
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
    <div className={styles["todo"]}>
      <div className={styles["item__title"]}>
        <Text>주차별 할 일</Text>
      </div>
      <div
        className={styles["item__todo-list"]}
        style={{ gridTemplateRows: `repeat(${todoItems.length + 1}, calc(100% / 7)` }}
      >
        {todoItems.map((item: WeekTodoItemConfig, idx: number) => {
          return (
            <div className={styles["todo__item"]} key={idx}>
              <div className={styles["todo__checkbox"]}>
                <input type="checkbox" id={idx.toString()} defaultChecked={item.weeklyTodoStatus} />
                {item.weeklyTodoUpdate ? (
                  <input
                    autoFocus
                    type="text"
                    defaultValue={item.weeklyTodoContent}
                    onKeyDown={(e) => handleEnter(e)}
                    onBlur={(e) => handleEditSave(idx, e)}
                  />
                ) : (
                  <div className={styles["todo__name"]} onClick={() => handleEditState(idx)}>
                    <Text types="small">{item.weeklyTodoContent}</Text>
                  </div>
                )}
              </div>
              <DeleteOutlined onClick={() => handleDelete(idx)} />
            </div>
          );
        })}
        <div ref={todoEndRef} className={styles["todo__item"]}>
          <div className={styles["todo__checkbox"]}>
            <input type="checkbox" style={{ cursor: "auto" }} disabled />
            <input
              autoFocus
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
