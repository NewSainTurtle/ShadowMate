import React, { ChangeEvent, FocusEvent, KeyboardEvent, useCallback, useEffect, useRef, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import { WeekTodoItemConfig } from "@util/planner.interface";
import { useAppSelector } from "@hooks/hook";
import { selectWeeklyTodos, setWeeklyTodos } from "@store/planner/weekSlice";
import WeekTodoInput from "./WeekTodoInput";
import WeekTodoItem from "./WeekTodoItem";

const WeekTodo = () => {
  const weeklyTodos: WeekTodoItemConfig[] = useAppSelector(selectWeeklyTodos);

  return (
    <div className={styles["todo"]}>
      <div className={styles["item__title"]}>
        <Text>주차별 할 일</Text>
      </div>
      <div
        className={styles["item__todo-list"]}
        style={{ gridTemplateRows: `repeat(${weeklyTodos.length + 1}, calc(100% / 7)` }}
      >
        {weeklyTodos.map((item: WeekTodoItemConfig, idx: number) => {
          return <WeekTodoItem key={item.weeklyTodoId} idx={idx} item={item} />;
        })}
        <WeekTodoInput />
      </div>
    </div>
  );
};

export default WeekTodo;
