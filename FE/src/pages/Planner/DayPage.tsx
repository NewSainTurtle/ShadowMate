import React, { useEffect, useMemo, useRef, useState } from "react";
import Header from "@components/planner/day/Header";
import styles from "@styles/planner/day.module.scss";
import TimeTable from "@components/planner/day/todo/TimeTable";
import TodoList from "@components/planner/day/todo/TodoList";
import Ment from "@components/planner/day/Ment";
import { useAppSelector } from "@hooks/hook";
import { selectTodoList } from "@store/planner/daySlice";
import CustomCursor from "@components/planner/day/CustomCursor";
import dayjs from "dayjs";
import { TodoConfig } from "@util/planner.interface";

const DayPage = () => {
  const [ment, setMent] = useState({
    todayGoals: "",
    tomorrowGoals: "",
    retrospections: "",
  });
  const todoListData = useAppSelector(selectTodoList);
  const totalTime = useMemo(() => {
    const sumMinute = todoListData
      .filter((ele: TodoConfig) => ele.timeTable!.startTime != "" && ele.timeTable!.endTime != "")
      .reduce(
        (accumulator: number, item: { timeTable: any }) =>
          accumulator + Number(dayjs(item.timeTable!.endTime).diff(dayjs(item.timeTable!.startTime), "m")),
        0,
      );

    return {
      studyTimeHour: Math.floor(sumMinute / 60),
      studyTimeMinute: Math.floor(sumMinute % 60),
    };
  }, [todoListData]);
  const [isClickTimeTable, setIsClickTimeTable] = useState(false);
  const todoDivRef = useRef<HTMLDivElement>(null);

  const handleInput = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setMent({
      ...ment,
      [e.target.name]: e.target.value,
    });
  };

  const handleClickTimeTable = (props: boolean) => {
    setIsClickTimeTable(props);
  };

  useEffect(() => {
    const handleOutsideClose = (e: MouseEvent) => {
      if (todoDivRef && todoDivRef.current) {
        if (e.button == 2) handleClickTimeTable(false);
      }
    };
    document.addEventListener("mouseup", handleOutsideClose);
    document.addEventListener("contextmenu", (e) => e.preventDefault());
    return () => document.addEventListener("mouseup", handleOutsideClose);
  }, []);

  useEffect(() => {
    const handleOutsideClose = (e: { target: any }) => {
      if (isClickTimeTable && !todoDivRef.current?.contains(e.target)) handleClickTimeTable(false);
    };
    document.addEventListener("click", handleOutsideClose);
    return () => document.removeEventListener("click", handleOutsideClose);
  }, [isClickTimeTable]);

  const { todayGoals, tomorrowGoals, retrospections } = ment;
  const { studyTimeHour, studyTimeMinute } = totalTime;

  return (
    <div className={styles["page-container"]}>
      <Header />
      <div className={styles["page-content"]}>
        <Ment
          title={"오늘의 다짐"}
          name="todayGoals"
          value={todayGoals}
          onChange={handleInput}
          rows={1}
          maxLength={50}
        />

        <div className={styles["item__time"]}>
          <span>{studyTimeHour}</span>
          <span>시간</span>
          <span>{studyTimeMinute}</span>
          <span>분</span>
        </div>

        <div ref={todoDivRef} className={styles["item__todo"]}>
          {isClickTimeTable && <CustomCursor />}
          <div className={styles["item__todo-list"]}>
            <TodoList clicked={isClickTimeTable} />
          </div>
          <div className={styles["item__timetable"]}>
            <TimeTable clicked={isClickTimeTable} setClicked={handleClickTimeTable} />
          </div>
        </div>

        <Ment
          title={"오늘의 회고"}
          name="retrospections"
          value={retrospections}
          onChange={handleInput}
          rows={5}
          maxLength={100}
          fileImg
        />

        <Ment
          title={"내일 다짐"}
          name="tomorrowGoals"
          value={tomorrowGoals}
          onChange={handleInput}
          rows={5}
          maxLength={50}
        />
      </div>
    </div>
  );
};

export default DayPage;
