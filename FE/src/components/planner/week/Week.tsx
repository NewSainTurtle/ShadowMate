import React, { useEffect, useState } from "react";
import styles from "@styles/planner/Week.module.scss";
import WeekItem from "./WeekItem";
import Text from "@components/common/Text";
import FriendProfile from "@components/common/FriendProfile";
import { profileInfo } from "@pages/commonPage";
import WeekTodo from "./WeekTodo";
import { getThisWeekCnt } from "@util/getThisWeek";
import { useAppSelector } from "@hooks/hook";
import { selectDayList } from "@store/weekSlice";

const Week = () => {
  const today = new Date();
  const [week, setWeek] = useState(today);
  const thisWeekCnt = getThisWeekCnt({ date: week });
  const [isMine, setIsMine] = useState<boolean>(true);
  const dayList = useAppSelector(selectDayList);

  const handleButton = (to: string) => {
    const date = week.getDate();
    if (to === "forward") setWeek(new Date(week.setDate(date - 7)));
    else if (to === "backward") setWeek(new Date(week.setDate(date + 7)));
  };

  return (
    <div className={styles["week"]}>
      <div className={styles["week__title"]}>
        <div>
          <Text types="semi-large" bold>
            {thisWeekCnt.weekYear}년 {thisWeekCnt.weekMonth + 1}월 {thisWeekCnt.weekCnt}주차
          </Text>
          <div>
            <div className={styles["week__button"]} onClick={() => handleButton("forward")}>
              <svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" fillRule="evenodd" clipRule="evenodd">
                <path d="M20 .755l-14.374 11.245 14.374 11.219-.619.781-15.381-12 15.391-12 .609.755z" />
              </svg>
            </div>
            <div className={styles["week__today"]} onClick={() => setWeek(new Date())}>
              <Text>TODAY</Text>
            </div>
            <div className={styles["week__button"]} onClick={() => handleButton("backward")}>
              <svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" fillRule="evenodd" clipRule="evenodd">
                <path d="M4 .755l14.374 11.245-14.374 11.219.619.781 15.381-12-15.391-12-.609.755z" />
              </svg>
            </div>
          </div>
        </div>
        {!isMine && (
          <div>
            <FriendProfile types="아이콘" profile={profileInfo} />
          </div>
        )}
      </div>
      <div className={styles["week__list"]}>
        <WeekTodo />
        {dayList?.map((today, key) => <WeekItem dayInfo={today} key={key} />)}
      </div>
    </div>
  );
};

export default Week;
