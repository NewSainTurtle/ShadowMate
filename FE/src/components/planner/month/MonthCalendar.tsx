import React, { Dispatch, SetStateAction, useEffect, useState } from "react";
import styles from "@styles/planner/Month.module.scss";
import Text from "@components/common/Text";
import dayjs from "dayjs";
import { MonthType } from "@util/planner.interface";
import CheckRoundedIcon from "@mui/icons-material/CheckRounded";

interface Props {
  selectedDay: string;
  setSelectedDay: Dispatch<SetStateAction<string>>;
}

const dayOfWeek = ["", "월", "화", "수", "목", "금", "토", "일"];
const statusColor = [
  "var(--color-calendar-0)",
  "var(--color-calendar-10)",
  "var(--color-calendar-60)",
  "var(--color-calendar-100)",
];

const MonthCalendar = ({ selectedDay }: Props) => {
  const [isNotFriend, setIsNotFriend] = useState<boolean>(false);
  const [ItemList, setItemList] = useState<(MonthType | null)[]>([]);

  const initArr = (firstDay: number, daysInMonth: number) => {
    return Array.from({ length: firstDay + daysInMonth }, (v, i) =>
      i < firstDay
        ? null
        : {
            date: dayjs(selectedDay)
              .startOf("month")
              .set("date", i - firstDay + 1)
              .format("YYYY-MM-DD"),
            todoCount: i,
            dayStatus: i % 4,
          },
    );
  };

  useEffect(() => {
    const firstDay = dayjs(selectedDay).startOf("month").day();
    const daysInMonth = dayjs(selectedDay).daysInMonth();
    setItemList(initArr(firstDay, daysInMonth));
  }, [selectedDay]);

  return (
    <>
      <div className={styles["calendar"]}>
        {dayOfWeek.map((item, idx) => (
          <div className={styles["calendar__week"]} key={idx}>
            <Text types="small">{item}</Text>
          </div>
        ))}
        {ItemList?.map((item, idx) => {
          return (
            <>
              {idx % 7 === 0 && (
                <div className={styles["calendar__container"]}>
                  <Text types="small">{(idx % 6) + 1}주차</Text>
                </div>
              )}
              <div className={styles["calendar__container"]} key={item ? item.toString() : `${item}${idx}`}>
                {item ? (
                  <div className={styles["calendar__item"]}>
                    <div style={{ backgroundColor: statusColor[item.dayStatus] }}>
                      <Text types="small">{item.dayStatus === 3 ? <CheckRoundedIcon /> : item.todoCount}</Text>
                    </div>
                    <Text types="small">{dayjs(item.date).date()}</Text>
                  </div>
                ) : (
                  <></>
                )}
              </div>
            </>
          );
        })}
        {isNotFriend && (
          <div className={styles["calendar__overlay"]}>
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24">
              <path d="M18 10v-4c0-3.313-2.687-6-6-6s-6 2.687-6 6v4h-3v14h18v-14h-3zm-5 7.723v2.277h-2v-2.277c-.595-.347-1-.984-1-1.723 0-1.104.896-2 2-2s2 .896 2 2c0 .738-.404 1.376-1 1.723zm-5-7.723v-4c0-2.206 1.794-4 4-4 2.205 0 4 1.794 4 4v4h-8z" />
            </svg>
            <Text>비공개 계정은 확인할 수 없습니다.</Text>
          </div>
        )}
      </div>
    </>
  );
};

export default MonthCalendar;
