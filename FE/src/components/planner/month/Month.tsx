import React, { useEffect, useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import MonthCalendar from "./MonthCalendar";
import MonthFriends from "./MonthFriends";
import Text from "@components/common/Text";
import dayjs from "dayjs";

const Month = () => {
  const today = new Date();
  const [selectedDay, setSelectedDay] = useState<string>(dayjs(today).format("YYYY-MM-DD"));
  const year: number = dayjs(selectedDay).year();
  const month: number = dayjs(selectedDay).month() + 1;

  const handlePrevMonth = () => {
    const newDate = dayjs(selectedDay).subtract(1, "month").endOf("month").format("MM/DD/YY");
    setSelectedDay(newDate);
  };

  const handleNextMonth = () => {
    const newDate = dayjs(selectedDay).add(1, "month").startOf("month").format("MM/DD/YY");
    setSelectedDay(newDate);
  };

  return (
    <div className={styles["month"]}>
      <div className={styles["month__title"]}>
        <div>
          <div>
            <div>
              <Text types="semi-large" bold>
                {year}년
              </Text>
            </div>
            <div>
              <Text types="semi-large" bold>
                {month.toString().length > 1 ? month : "0" + month.toString()}월
              </Text>
            </div>
          </div>
          <div className={styles["month__button"]} onClick={handlePrevMonth}>
            <svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" fillRule="evenodd" clipRule="evenodd">
              <path d="M20 .755l-14.374 11.245 14.374 11.219-.619.781-15.381-12 15.391-12 .609.755z" />
            </svg>
          </div>
          <div className={styles["month__button"]} onClick={handleNextMonth}>
            <svg width="24" height="24" xmlns="http://www.w3.org/2000/svg" fillRule="evenodd" clipRule="evenodd">
              <path d="M4 .755l14.374 11.245-14.374 11.219.619.781 15.381-12-15.391-12-.609.755z" />
            </svg>
          </div>
        </div>
      </div>
      <div className={styles["month__calendar"]}>
        <MonthCalendar selectedDay={selectedDay} setSelectedDay={setSelectedDay} />
        <MonthFriends />
      </div>
    </div>
  );
};

export default Month;
