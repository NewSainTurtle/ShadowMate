import React, { useEffect, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import Header from "@components/planner/day/Header";
import styles from "@styles/planner/day.module.scss";
import TimeTable from "@components/planner/day/TimeTable";
import TodoList from "@components/planner/day/TodoList";
import Ment from "@components/planner/day/Ment";
import dayjs from "dayjs";
import CustomCursor from "@components/planner/day/CustomCursor";

const DayPage = () => {
  const { state } = useLocation();
  const [date, setDate] = useState<string | Date | dayjs.Dayjs>(state?.date || new Date());
  const [ment, setMent] = useState({
    todayGoals: "",
    tomorrowGoals: "",
    retrospections: "",
  });
  const [isClickTimeTable, setIsClickTimeTable] = useState(false);
  const todoDivRef = useRef<HTMLDivElement>(null);

  const moveDate = (n: -1 | 0 | 1) => {
    const newDate = n == 0 ? dayjs() : dayjs(date).add(n, "day");
    setDate(newDate);
  };

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
    const handleOutsideClose = (e: { target: any }) => {
      if (isClickTimeTable && !todoDivRef.current?.contains(e.target)) handleClickTimeTable(false);
    };
    document.addEventListener("click", handleOutsideClose);
    return () => document.removeEventListener("click", handleOutsideClose);
  }, [isClickTimeTable]);

  const { todayGoals, tomorrowGoals, retrospections } = ment;

  return (
    <div className={styles["page-container"]}>
      <Header date={date} moveDate={moveDate} />
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
          <span>{4}</span>
          <span>시간</span>
          <span>{10}</span>
          <span>분</span>
        </div>

        <div ref={todoDivRef} className={styles["item__todo"]}>
          {isClickTimeTable && <CustomCursor />}
          <div className={styles["item__todo-list"]}>
            <TodoList clicked={isClickTimeTable} />
          </div>
          <div className={styles["item__timetable"]}>
            <TimeTable date={date} clicked={isClickTimeTable} setClicked={handleClickTimeTable} />
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
