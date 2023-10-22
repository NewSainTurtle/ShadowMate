import React, { KeyboardEvent, useRef, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import WeekItem from "./WeekItem";
import WeekItemInput from "./WeekItemInput";
import { dateFormat } from "@util/getThisWeek";
import { DailyTodoConfig, DayListConfig } from "@store/weekSlice";

interface Props {
  dayInfo: DayListConfig;
}

const WeekList = ({ dayInfo }: Props) => {
  const [todoItems, setTodoItems] = useState<DailyTodoConfig[]>(dayInfo.dailyTodo ? dayInfo.dailyTodo : []);
  const [newTodo, setNewTodo] = useState<string>("");
  const [oldTodo, setOldTodo] = useState<string>("");
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

  const handleDelete = (idx: number) => {
    setTodoItems(todoItems.filter((item, key) => idx !== key));
  };

  return (
    <div className={styles["item"]}>
      <div className={styles["item__title"]}>
        <Text>{dateFormat(dayInfo.date)}</Text>
        <Dday comparedDate={dateFormat(dayInfo.date)} />
      </div>
      <div className={styles["item__todo-list"]} style={{ gridTemplateRows: `repeat(${todoItems.length + 1}, 20%` }}>
        {todoItems.map((item, key) => (
          <WeekItem dayInfo={dayInfo} item={item} key={key} />
        ))}
        <WeekItemInput newTodo={newTodo} setNewTodo={setNewTodo} />
      </div>
      <div className={`${styles["item__memo"]} ${todoItems.length < 4 && styles["top_border"]}`}>
        <textarea
          value={dayInfo.retrospection ? dayInfo.retrospection : ""}
          placeholder="💡 오늘의 메모를 입력하세요."
          readOnly
        />
      </div>
    </div>
  );
};

export default WeekList;
