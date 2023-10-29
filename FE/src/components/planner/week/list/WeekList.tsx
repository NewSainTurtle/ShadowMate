import React, { useRef, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import WeekItem from "@components/planner/week/list/WeekItem";
import WeekItemInput from "@components/planner/week/list/WeekItemInput";
import { dateFormat } from "@util/getThisWeek";
import { DayListConfig } from "@store/planner/weekSlice";
import { TodoConfig } from "@util/planner.interface";

interface Props {
  dayInfo: DayListConfig;
}

const WeekList = ({ dayInfo }: Props) => {
  const [todoItems, setTodoItems] = useState<TodoConfig[]>(dayInfo.dailyTodo || []);
  const nextId = useRef(todoItems.length + 1);
  const [retrospection, setRetrospection] = useState<string>(dayInfo.retrospection || "");

  return (
    <div className={styles["item"]}>
      <div className={styles["item__title"]}>
        <Text>{dateFormat(dayInfo.date)}</Text>
        <Dday comparedDate={dateFormat(dayInfo.date)} />
      </div>
      <div className={styles["item__todo-list"]} style={{ gridTemplateRows: `repeat(${todoItems.length + 1}, 20%` }}>
        {todoItems.map((item, key) => (
          <WeekItem key={key} idx={key} todoItems={todoItems} setTodoItems={setTodoItems} item={item} />
        ))}
        <WeekItemInput todoItems={todoItems} setTodoItems={setTodoItems} nextId={nextId} />
      </div>
      <div className={`${styles["item__memo"]} ${todoItems.length < 4 && styles["top_border"]}`}>
        <textarea
          defaultValue={retrospection}
          placeholder="💡 오늘의 메모를 입력하세요."
          onChange={(e) => setRetrospection(e.target.value.replace(/\n|\r|\s*/g, ""))}
        />
      </div>
    </div>
  );
};

export default WeekList;
