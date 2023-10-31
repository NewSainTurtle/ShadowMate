import React, { useEffect, useRef, useState } from "react";
import Header from "@components/planner/day/Header";
import styles from "@styles/planner/day.module.scss";
import TimeTable from "@components/planner/day/todo/TimeTable";
import TodoList from "@components/planner/day/todo/TodoList";
import Ment from "@components/planner/day/Ment";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectDate, selectTodoList, setDayInfo } from "@store/planner/daySlice";
import CustomCursor from "@components/planner/day/CustomCursor";
import dayjs from "dayjs";
import { TodoConfig } from "@util/planner.interface";
import { plannerApi } from "@api/Api";
import { selectUserId } from "@store/authSlice";

const DayPage = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const date = useAppSelector(selectDate);
  const todoList = useAppSelector(selectTodoList);
  const [ment, setMent] = useState({
    todayGoals: "",
    tomorrowGoals: "",
    retrospections: "",
  });
  const [isClickTimeTable, setIsClickTimeTable] = useState(false);
  const todoDivRef = useRef<HTMLDivElement>(null);
  const [totalTime, setTotalTime] = useState({
    studyTimeHour: 0,
    studyTimeMinute: 0,
  });

  useEffect(() => {
    const day = dayjs(date).format("YYYY-MM-DD");
    plannerApi
      .daily(userId, { date: day })
      .then((res) => {
        const response = res.data.data;
        dispatch(
          setDayInfo({
            plannerAccessScope: response.plannerAccessScope,
            dday: response.dday,
            like: response.like,
            likeCount: response.likeCount,
            retrospection: response.retrospection || "",
            retrospectionImage: response.retrospectionImage || "",
            todayGoal: response.todayGoal || "",
            tomorrowGoal: response.tomorrowGoal || "",
            dailyTodos: response.dailyTodos || [],
          }),
        );
        setTotalTime({ studyTimeHour: response.studyTimeHour, studyTimeMinute: response.studyTimeMinute });
      })
      .catch((err) => console.error(err));
  }, [date]);

  useEffect(() => {
    const sumMinute = todoList
      .filter((ele: TodoConfig) => ele.timeTable!.startTime != "" && ele.timeTable!.endTime != "")
      .reduce(
        (accumulator: number, item: { timeTable: any }) =>
          accumulator + Number(dayjs(item.timeTable!.endTime).diff(dayjs(item.timeTable!.startTime), "m")),
        0,
      );
    const studyTimeHour = Math.floor(sumMinute / 60);
    const studyTimeMinute = Math.floor(sumMinute % 60);
    setTotalTime({ studyTimeHour, studyTimeMinute });
  }, [todoList]);

  (() => {
    const handleOutsideClose = (e: MouseEvent) => {
      if (todoDivRef && todoDivRef.current) {
        if (e.button == 2) handleClickTimeTable(false);
      }
    };
    document.addEventListener("mouseup", handleOutsideClose);
    document.addEventListener("contextmenu", (e) => e.preventDefault());
    return () => document.addEventListener("mouseup", handleOutsideClose);
  })();

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

  const handleInput = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setMent({
      ...ment,
      [e.target.name]: e.target.value,
    });
  };

  const { todayGoals, tomorrowGoals, retrospections } = ment;
  const { studyTimeHour, studyTimeMinute } = totalTime;

  return (
    <div className={styles["page-container"]} key={date}>
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
