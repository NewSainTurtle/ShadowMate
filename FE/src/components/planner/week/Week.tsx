import React, { useState } from "react";
import styles from "@components/planner/week/Week.module.scss";
import WeekItem from "./WeekItem";
import Text from "@components/common/Text";
import FriendProfile from "@components/common/FriendProfile";
import { profileInfo } from "@pages/commonPage";
import WeekTodo from "./WeekTodo";
import getThisWeek from "@util/getThisWeek";

const Week = () => {
  const thisWeek = getThisWeek();
  const [isMine, setIsMine] = useState<boolean>(true);
  return (
    <div className={styles.week_container}>
      <div className={styles.week_title}>
        <div>
          <Text types="semi-large" bold>
            2023년 7월 2주차
          </Text>
          <div>
            <svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" fillRule="evenodd" clipRule="evenodd">
              <path d="M20 .755l-14.374 11.245 14.374 11.219-.619.781-15.381-12 15.391-12 .609.755z" />
            </svg>
            <svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" fillRule="evenodd" clipRule="evenodd">
              <path d="M4 .755l14.374 11.245-14.374 11.219.619.781 15.381-12-15.391-12-.609.755z" />
            </svg>
          </div>
        </div>
        {!isMine && (
          <div>
            <FriendProfile types="아이콘" profile={profileInfo} />
          </div>
        )}
      </div>
      <div className={styles.week_weekList}>
        <WeekTodo />
        {thisWeek.map((today, key) => (
          <WeekItem date={today} key={key} />
        ))}
      </div>
    </div>
  );
};

export default Week;
