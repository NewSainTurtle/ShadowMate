import React from "react";
import styles from "../month/Month.module.scss";
import MonthCalendar from "./MonthCalendar";
import MonthFriends from "./MonthFriends";
import Text from "@components/common/Text";

const Month = () => {
  const today = new Date();
  const year: number = today.getFullYear();
  const month: number = today.getMonth() + 1;
  return (
    <div className={styles.month_container}>
      <div className={styles.month_title}>
        <Text types="semi-large" bold>
          {year}년 {month}월
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
      <div className={styles.month_calendar}>
        <MonthCalendar />
        <MonthFriends />
      </div>
    </div>
  );
};

export default Month;
