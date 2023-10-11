import React, { useMemo } from "react";
import styles from "@styles/planner/day.module.scss";
import dayjs from "dayjs";

interface Props {
  date: string | Date | dayjs.Dayjs;
}

const TimeTable = ({ date }: Props) => {
  const plannerDate = dayjs(date).startOf("d").format("YYYY-MM-DD");
  const plannerTime = {
    start: dayjs(plannerDate).set("h", 4).format("YYYY-MM-DD HH:mm"),
    end: dayjs(plannerDate).add(1, "day").set("h", 4).format("YYYY-MM-DD HH:mm"),
  };
  const timeArr = useMemo(() => {
    const time = [];
    let tempTime = plannerTime.start;
    let id = 0;
    while (tempTime != plannerTime.end) {
      tempTime = dayjs(tempTime).add(10, "m").format("YYYY-MM-DD HH:mm");
      time.push({ id, time: dayjs(tempTime).format("YYYY-MM-DD HH:mm") });
      id++;
    }
    console.log(time);
    return time;
  }, []);

  return (
    <div className={styles["timetable__container"]}>
      <div className={styles["timetable__container-box"]}>
        <div className={styles["timetable__hour"]}>
          {Array.from({ length: 24 }).map((_, index) => (
            <div key={index}>{String((4 + index) % 24).padStart(2, "0")}</div>
          ))}
        </div>
        <div className={styles["timetable__table"]}>
          {timeArr.map((item) => (
            <div
              key={item.id}
              onClick={() => {
                console.log(item.time);
              }}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default TimeTable;
