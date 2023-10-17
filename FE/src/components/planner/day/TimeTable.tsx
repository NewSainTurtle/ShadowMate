import React, { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import dayjs from "dayjs";
import { useDispatch } from "react-redux";
import { useAppSelector } from "@hooks/hook";
import { removeTodo, selectTodo } from "@store/planner/daySlice";

interface Props {
  date: string | Date | dayjs.Dayjs;
  clicked: boolean;
  setClicked: (props: boolean) => void;
}

interface tableTimeType {
  todoId: number;
  categoryColorCode: string;
  time: string;
}

const TimeTable = ({ date, clicked, setClicked }: Props) => {
  const dispatch = useDispatch();
  const { todoId, categoryColorCode } = useAppSelector(selectTodo);
  const plannerDate = dayjs(date).startOf("d").format("YYYY-MM-DD");
  const plannerTime = {
    // 오전 4시 ~ 익일 4시
    start: dayjs(plannerDate).set("h", 4).format("YYYY-MM-DD HH:mm"),
    end: dayjs(plannerDate).add(1, "day").set("h", 4).format("YYYY-MM-DD HH:mm"),
  };
  const makeTimeArr: tableTimeType[] = (() => {
    const time: tableTimeType[] = [];
    let tempTime = plannerTime.start;
    while (tempTime != plannerTime.end) {
      tempTime = dayjs(tempTime).add(10, "m").format("YYYY-MM-DD HH:mm");
      time.push({ todoId: 0, categoryColorCode: "", time: tempTime });
    }
    return time;
  })();

  const [timeArr, setTimeArr] = useState<tableTimeType[]>(makeTimeArr);
  const [timeClick, setTimeClicked] = useState(false);
  const [selectTime, setSelectTime] = useState({
    startTime: "",
    endTime: "",
  });

  useEffect(() => {
    if (selectTime.startTime != "" && selectTime.endTime != "") {
      let { startTime, endTime } = selectTime;
      if (startTime > endTime) {
        const temp = startTime;
        startTime = endTime;
        endTime = temp;
      }

      const dragTime: tableTimeType[] = timeArr.reduce((updateArr: tableTimeType[], obj) => {
        if (obj.time >= startTime && obj.time <= endTime) {
          updateArr.push({ ...obj, todoId, categoryColorCode });
        }
        return updateArr;
      }, []);

      const startIndex = timeArr.findIndex((e) => e.time == startTime);
      const copyTimeArr = [...timeArr];
      copyTimeArr.splice(startIndex, dragTime.length, ...dragTime);
      setTimeArr(copyTimeArr);
    }
  }, [selectTime]);

  const mouseModule = (() => {
    const mouseDown = (e: React.MouseEvent<HTMLDivElement>, time: string) => {
      if (e.button != 2) {
        setTimeClicked(true);
        setSelectTime({ ...selectTime, startTime: time });
      }
    };
    const mouseEnter = (time: string) => {
      if (timeClick) {
        setSelectTime({ ...selectTime, endTime: time });
      }
    };

    const mouseUp = () => {
      setTimeClicked(false);
      setSelectTime({ startTime: "", endTime: "" });
      dispatch(removeTodo());
    };

    return {
      mouseDown,
      mouseEnter,
      mouseUp,
    };
  })();

  const clickedStyle = clicked ? "--clicked" : "";

  return (
    <div
      className={styles[`timetable__container${clickedStyle}`]}
      onClick={() => {
        setClicked(true);
      }}
    >
      <div className={styles["timetable__container-box"]}>
        <div className={styles["timetable__hours"]}>
          {Array.from({ length: 24 }).map((_, idx) => (
            <div key={idx}>{String((4 + idx) % 24).padStart(2, "0")}</div>
          ))}
        </div>
        <div className={styles["timetable__minutes"]}>
          {timeArr.map((item, idx) => (
            <div
              key={idx}
              className={styles["timetable__minutes__item"]}
              onMouseDown={(e) => {
                mouseModule.mouseDown(e, item.time);
              }}
              onMouseEnter={() => mouseModule.mouseEnter(item.time)}
              onMouseUp={() => {
                mouseModule.mouseUp();
              }}
              style={{ backgroundColor: item.categoryColorCode }}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default TimeTable;
