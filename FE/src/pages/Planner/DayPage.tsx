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
  const friendUserId = 3;
  const date = useAppSelector(selectDate);
  const todoList = useAppSelector(selectTodoList);
  const [ment, setMent] = useState({
    todayGoal: "",
    tomorrowGoal: "",
    retrospection: "",
  });
  const [retrospectionImage, setRetrospectionImage] = useState<string | null>(null);
  const [isClickTimeTable, setIsClickTimeTable] = useState(false);
  const todoDivRef = useRef<HTMLDivElement>(null);
  const [totalTime, setTotalTime] = useState({
    studyTimeHour: 0,
    studyTimeMinute: 0,
  });

  useEffect(() => {
    const day = dayjs(date).format("YYYY-MM-DD");
    plannerApi
      .daily(friendUserId, { date: day })
      .then((res) => {
        const response = res.data.data;
        dispatch(
          setDayInfo({
            plannerAccessScope: response.plannerAccessScope,
            dday: response.dday,
            like: response.like,
            likeCount: response.likeCount,
            dailyTodos: response.dailyTodos || [],
          }),
        );
        setMent({
          retrospection: response.retrospection || "",
          todayGoal: response.todayGoal || "",
          tomorrowGoal: response.tomorrowGoal || "",
        });
        setRetrospectionImage(response.retrospectionImage);
        setTotalTime({ studyTimeHour: response.studyTimeHour, studyTimeMinute: response.studyTimeMinute });
      })
      .catch((err) => console.error(err));
  }, [date]);

  useEffect(() => {
    const sumMinute = todoList
      .filter((ele: TodoConfig) => ele.timeTable && ele.timeTable.startTime != "" && ele.timeTable.endTime != "")
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

  useEffect(() => {
    const handleOutsideClose = (e: { target: any }) => {
      if (isClickTimeTable && !todoDivRef.current?.contains(e.target)) handleClickTimeTable(false);
    };
    document.addEventListener("click", handleOutsideClose);

    return () => document.removeEventListener("click", handleOutsideClose);
  }, [isClickTimeTable]);

  const handleSaveMent = (() => {
    const saveTodayGoals = () => plannerApi.todayGoals(userId, { date, todayGoal }).catch((err) => console.error(err));
    const saveRetrospections = () =>
      plannerApi.retrospections(userId, { date, retrospection }).catch((err) => console.error(err));
    const saveTomorrowGoals = () =>
      plannerApi.tomorrowGoals(userId, { date, tomorrowGoal }).catch((err) => console.error(err));

    return {
      saveTodayGoals,
      saveRetrospections,
      saveTomorrowGoals,
    };
  })();

  const handleInput = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setMent({
      ...ment,
      [e.target.name]: e.target.value,
    });
  };

  const handleClickTimeTable = (props: boolean) => {
    if (userId == friendUserId) setIsClickTimeTable(props);
  };

  const { saveTodayGoals, saveRetrospections, saveTomorrowGoals } = handleSaveMent;
  const { todayGoal, tomorrowGoal, retrospection } = ment;
  const { studyTimeHour, studyTimeMinute } = totalTime;
  const isFriend = userId != friendUserId;

  return (
    <div className={styles["page-container"]} key={date}>
      <Header isFriend={userId != friendUserId} />
      <div className={`${styles["page-content"]} ${isFriend ? styles["--friend"] : ""}`}>
        <Ment
          title={"오늘의 다짐"}
          name="todayGoal"
          value={todayGoal}
          onChange={handleInput}
          rows={1}
          maxLength={50}
          onBlur={saveTodayGoals}
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
          name="retrospection"
          value={retrospection}
          onChange={handleInput}
          rows={5}
          maxLength={100}
          isFile
          retrospectionImage={retrospectionImage}
          setRetrospectionImage={setRetrospectionImage}
          onBlur={saveRetrospections}
        />

        <Ment
          title={"내일 다짐"}
          name="tomorrowGoal"
          value={tomorrowGoal}
          onChange={handleInput}
          rows={5}
          maxLength={50}
          onBlur={saveTomorrowGoals}
        />
      </div>
    </div>
  );
};

export default DayPage;
