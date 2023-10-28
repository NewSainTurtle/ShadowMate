import React, { useEffect, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import dayjs from "dayjs";
import DoDisturbOnIcon from "@mui/icons-material/DoDisturbOn";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectDate, selectTodoItem, selectTodoList, removeTodoItem, setTimeTable } from "@store/planner/daySlice";

interface Props {
  clicked: boolean;
  setClicked: (props: boolean) => void;
}

interface tableTimeType {
  todoId: number;
  categoryColorCode: string;
  time: string;
  closeButton?: boolean;
}

const debouncing = <T extends (...args: any[]) => any>(fn: T, delay: number) => {
  let timeId: ReturnType<typeof setTimeout>;
  return (...args: Parameters<T>): ReturnType<T> => {
    let result: any;
    if (timeId) clearTimeout(timeId);
    timeId = setTimeout(() => {
      result = fn(...args);
    }, delay);
    return result;
  };
};

const TimeTable = ({ clicked, setClicked }: Props) => {
  const dispatch = useAppDispatch();
  const date = useAppSelector(selectDate);
  const { todoId, category } = useAppSelector(selectTodoItem);
  const [categoryColorCode] = [category!.categoryColorCode];
  const todoList = useAppSelector(selectTodoList);
  const makeTimeArr: tableTimeType[] = (() => {
    const plannerDate = dayjs(date).startOf("d").format("YYYY-MM-DD");
    // 오전 4시 ~ 익일 4시
    const dayStartTime = dayjs(plannerDate).set("h", 4).format("YYYY-MM-DD HH:mm");
    const dayEndTime = dayjs(plannerDate).add(1, "day").set("h", 4).format("YYYY-MM-DD HH:mm");
    const tempArr: tableTimeType[] = [];
    let tempTime = dayStartTime;
    while (tempTime != dayEndTime) {
      tempTime = dayjs(tempTime).add(10, "m").format("YYYY-MM-DD HH:mm");
      tempArr.push({ todoId: 0, categoryColorCode: "", time: tempTime });
    }
    return tempArr;
  })();

  const [timeArr, setTimeArr] = useState<tableTimeType[]>(makeTimeArr);
  const [copyTimeArr, setCopyTimeArr] = useState<tableTimeType[]>(makeTimeArr);
  const [timeClick, setTimeClicked] = useState(false);
  const [selectTime, setSelectTime] = useState({
    startTime: "",
    endTime: "",
  });

  const mouseModule = (() => {
    const mouseDown = (e: React.MouseEvent<HTMLDivElement>, startTime: string) => {
      if (e.button != 2 && todoId != 0) {
        setTimeClicked(true);
        setSelectTime({ startTime, endTime: startTime });
      }
    };
    const mouseEnter = (endTime: string) => {
      if (timeClick && selectTime.endTime != endTime) {
        setSelectTime({ ...selectTime, endTime });
      }
    };
    const mouseUp = (endTime: string) => {
      if (todoId != 0) {
        setTimeClicked(false);
        const startTime = dayjs(selectTime.startTime).subtract(10, "m").format("YYYY-MM-DD HH:mm");
        dispatch(setTimeTable({ todoId, startTime, endTime }));

        setSelectTime({ startTime: "", endTime: "" });
        dispatch(removeTodoItem());
      }
    };
    const debounceMouseEnter = debouncing(mouseEnter, 50);

    return {
      mouseDown,
      debounceMouseEnter,
      mouseUp,
    };
  })();

  const deleteTimeTable = (todoId: number) => {
    dispatch(setTimeTable({ todoId, startTime: "", endTime: "" }));
  };

  useEffect(() => {
    let tempArr = [...makeTimeArr];
    todoList
      .filter((ele) => ele.timeTable?.startTime != null && ele.timeTable?.endTime != null)
      .map((item) => {
        const { todoId, category, timeTable } = item;
        let { startTime, endTime } = timeTable!;
        const miniArr: tableTimeType[] = [];
        let tempTime = startTime;
        while (tempTime != endTime) {
          tempTime = dayjs(tempTime).add(10, "m").format("YYYY-MM-DD HH:mm");
          miniArr.push({
            todoId,
            categoryColorCode: category!.categoryColorCode,
            time: tempTime,
            closeButton: tempTime == endTime,
          });
        }
        startTime = dayjs(startTime).add(10, "m").format("YYYY-MM-DD HH:mm");
        const startIndex = tempArr.findIndex((e) => e.time == startTime);
        tempArr.splice(startIndex, miniArr.length, ...miniArr);
      });
    setTimeArr(tempArr);
    setCopyTimeArr(tempArr);
  }, [todoList]);

  useEffect(() => {
    let { startTime, endTime } = selectTime;
    if (startTime != "" && endTime != "") {
      if (startTime > endTime) [startTime, endTime] = [endTime, startTime];
      const dragArr: tableTimeType[] = copyTimeArr.map((obj) => {
        if (obj.time >= startTime && obj.time <= endTime) {
          return { todoId, categoryColorCode, time: obj.time };
        } else return obj;
      });

      setTimeArr(dragArr);
    }
  }, [selectTime]);

  const clickedStyle = clicked ? "--clicked" : "";

  return (
    <div className={styles[`timetable__container${clickedStyle}`]} onClick={() => setClicked(true)}>
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
              onMouseDown={(e) => mouseModule.mouseDown(e, item.time)}
              onMouseEnter={() => mouseModule.debounceMouseEnter(item.time)}
              onMouseUp={() => mouseModule.mouseUp(item.time)}
              style={{ backgroundColor: item.categoryColorCode }}
            >
              {item.closeButton && (
                <div onClick={() => deleteTimeTable(item.todoId)}>
                  <DoDisturbOnIcon />
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TimeTable;
