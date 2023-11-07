import React, { useEffect, useLayoutEffect, useRef, useState } from "react";
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
import { ref, uploadString, getDownloadURL } from "firebase/storage";
import { firebaseStorage } from "@api/firebaseConfig";
import html2canvas from "html2canvas";
import Alert from "@components/common/Alert";

const DayPage = () => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const friendUserId = userId;
  const date = useAppSelector(selectDate);
  const todoList = useAppSelector(selectTodoList);
  const [ment, setMent] = useState({
    todayGoal: "",
    tomorrowGoal: "",
    retrospection: "",
  });
  const [retrospectionImage, setRetrospectionImage] = useState<string | null>(null);
  const [isClickTimeTable, setIsClickTimeTable] = useState(false);
  const [totalTime, setTotalTime] = useState({
    studyTimeHour: 0,
    studyTimeMinute: 0,
  });
  const [alertSuccess, setAlertSuccess] = useState<boolean>(false);
  const todoDivRef = useRef<HTMLDivElement>(null);
  const screenDivRef = useRef<HTMLDivElement>(null);

  const handleSuccessClose = () => setAlertSuccess(false);

  useLayoutEffect(() => {
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
            shareSocial: response.shareSocial,
            likeCount: response.likeCount,
            dailyTodos: response.dailyTodos || [],
          }),
        );
        setMent({
          retrospection: response.retrospection || "",
          todayGoal: response.todayGoal || "",
          tomorrowGoal: response.tomorrowGoal || "",
        });
        setRetrospectionImage(response.retrospectionImage.toString());
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

  useEffect(() => {
    const handleOutsideClose = (e: MouseEvent) => {
      if (todoDivRef && todoDivRef.current && e.button == 2) {
        setIsClickTimeTable(false);
      }
    };
    document.addEventListener("mouseup", handleOutsideClose);
    document.addEventListener("contextmenu", (e) => e.preventDefault());
    return () => document.addEventListener("mouseup", handleOutsideClose);
  }, []);

  useEffect(() => {
    const handleOutsideClose = (e: { target: any }) => {
      if (isClickTimeTable && !todoDivRef.current?.contains(e.target)) setIsClickTimeTable(false);
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
    if (userId == friendUserId && todoList.length > 0) setIsClickTimeTable(props);
  };

  const handleDownload = async () => {
    if (!screenDivRef.current) return;
    await html2canvas(screenDivRef.current, {
      logging: false,
      allowTaint: true,
      useCORS: true,
    }).then(async (canvas) => {
      const imageURL = canvas.toDataURL("image/png");
      const storageRef = ref(firebaseStorage, `/social/${userId + "_" + date}`);
      uploadString(storageRef, imageURL, "data_url").then((snapshot) =>
        getDownloadURL(snapshot.ref).then((downloadURL) =>
          plannerApi.social(userId, { date, socialImage: downloadURL }).catch((err) => console.error(err)),
        ),
      );
      setAlertSuccess(true);
    });
  };

  const { saveTodayGoals, saveRetrospections, saveTomorrowGoals } = handleSaveMent;
  const { todayGoal, tomorrowGoal, retrospection } = ment;
  const { studyTimeHour, studyTimeMinute } = totalTime;
  const isFriend = userId != friendUserId;

  return (
    <div ref={screenDivRef} className={styles["page-container"]} key={date}>
      <Alert types="other" open={alertSuccess} onClose={handleSuccessClose} message="공유 되었습니다." />
      <Header isFriend={userId != friendUserId} socialClick={handleDownload} />
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
