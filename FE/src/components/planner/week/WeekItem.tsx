import React, { ChangeEvent, KeyboardEvent, useEffect, useRef, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import { dateFormat } from "@util/getThisWeek";
import { DeleteOutlined } from "@mui/icons-material";
import { DailyTodoConfig, DayListConfig, selectDayList } from "@store/weekSlice";

interface Props {
  dayInfo: DayListConfig;
}

const WeekItem = ({ dayInfo }: Props) => {
  const todoEndRef = useRef<HTMLDivElement | null>(null);
  const [newTodo, setNewTodo] = useState<string>("");
  const [oldTodo, setOldTodo] = useState<string>("");
  const [todoItems, setTodoItems] = useState<DailyTodoConfig[]>(dayInfo.dailyTodo ? dayInfo.dailyTodo : []);
  const nextId = useRef(todoItems.length + 1);

  const handleOnKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (newTodo === "") return;
    if (e.key == "Enter") {
      if (e.nativeEvent.isComposing) return;
      setTodoItems([
        ...todoItems,
        { todoId: nextId.current, category: null, todoContent: newTodo, todoStatus: "O", todoUpdate: false },
      ]);
      setNewTodo("");
      nextId.current += 1;
    }
  };

  const handleEditState = (idx: number) => {
    setTodoItems(
      todoItems.map((item, key) => {
        if (key === idx) {
          setOldTodo(item.todoContent);
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
    if (e.target.value === "") e.target.value = oldTodo;
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
        <Text>{dateFormat(dayInfo.date)}</Text>
        <Dday comparedDate={dateFormat(dayInfo.date)} />
      </div>
      <div className={styles["item__todo-list"]} style={{ gridTemplateRows: `repeat(${todoItems.length + 1}, 20%` }}>
        {todoItems.map((item, key) => (
          <div className={styles["item__todo-item"]} key={key}>
            <div>💻</div>
            {item.todoUpdate ? (
              <input
                className={styles["item__edit-input"]}
                autoFocus
                type="text"
                defaultValue={item.todoContent}
                onKeyDown={(e) => handleEnter(e)}
                onBlur={(e) => handleEditSave(key, e)}
              />
            ) : (
              <div onClick={() => handleEditState(key)}>
                <Text types="small">{item.todoContent}</Text>
              </div>
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
          <svg style={{ cursor: "auto", height: "0" }} />
        </div>
      </div>
      <div className={`${styles["item__memo"]} ${todoItems.length < 4 && styles["top_border"]}`}>
        <textarea value={dayInfo.retrospection} placeholder="💡 오늘의 메모를 입력하세요." readOnly />
      </div>
    </div>
  );
};

export default WeekItem;
