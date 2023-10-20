import React, { useEffect, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import dayjs from "dayjs";
import DoDisturbOnIcon from "@mui/icons-material/DoDisturbOn";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { selectDate, selectTodoItem, selectTodoList, removeTodoItem } from "@store/planner/daySlice";

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

const TimeTable = ({ clicked, setClicked }: Props) => {
  const dispatch = useAppDispatch();
  const date = useAppSelector(selectDate);
  const { todoId, category } = useAppSelector(selectTodoItem);
  const todoList = useAppSelector(selectTodoList);
  const { categoryColorCode } = category;
  const plannerDate = dayjs(date).startOf("d").format("YYYY-MM-DD");
  const makeTimeArr: tableTimeType[] = (() => {
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
  const [timeClick, setTimeClicked] = useState(false);
  const [selectTime, setSelectTime] = useState({
    startTime: "",
    endTime: "",
  });
  const [closeTime, setCloseTime] = useState({
    todoId: 0,
    endTime: "",
  });

  const mouseModule = (() => {
    const mouseDown = (e: React.MouseEvent<HTMLDivElement>, startTime: string) => {
      if (e.button != 2 && todoId != 0) {
        setTimeClicked(true);
        setSelectTime({ ...selectTime, startTime, endTime: startTime });
      }
    };
    const mouseEnter = (endTime: string) => {
      if (timeClick) {
        setSelectTime({ ...selectTime, endTime });
      }
    };
    const mouseUp = (endTime: string) => {
      setTimeClicked(false);
      setCloseTime({ todoId, endTime });
      setSelectTime({ startTime: "", endTime: "" });
      dispatch(removeTodoItem());
    };
    return {
      mouseDown,
      mouseEnter,
      mouseUp,
    };
  })();

  const timeTableUpdate = (todoId: number, categoryColorCode: string, startTime: string, endTime: string) => {
    if (startTime != "" && endTime != "") {
      if (startTime > endTime) {
        [startTime, endTime] = [endTime, startTime];
      }

      const dragTime: tableTimeType[] = timeArr.reduce((updateArr: tableTimeType[], obj) => {
        if (obj.time >= startTime && obj.time <= endTime) {
          updateArr.push({ time: obj.time, todoId, categoryColorCode });
        }
        return updateArr;
      }, []);

      const startIndex = timeArr.findIndex((e) => e.time == startTime);
      const copyTimeArr = [...timeArr];
      copyTimeArr.splice(startIndex, dragTime.length, ...dragTime);
      setTimeArr(copyTimeArr);
    }
  };

  const closeButtonUpdate = () => {
    let { todoId, endTime } = closeTime;
    if (todoId != 0 && endTime != "") {
      const closeIndex = timeArr.findIndex((e) => e.time == endTime);
      const copyTimeArr = [...timeArr];
      copyTimeArr[closeIndex].closeButton = true;
      setTimeArr(copyTimeArr);
    }
  };

  const deleteTimeTable = (todoId: number) => {
    const copyTimeArr: tableTimeType[] = timeArr.map((item) =>
      item.todoId == todoId ? { todoId: 0, categoryColorCode: "", time: item.time } : item,
    );
    setTimeArr(copyTimeArr);
  };

  useEffect(() => {
    todoList.map((item) => {
      const startTime = dayjs(item.timeTable.startTime).add(10, "m").format("YYYY-MM-DD HH:mm");
      timeTableUpdate(item.todoId, item.category.categoryColorCode, startTime, item.timeTable.endTime);
    });
  }, [todoList]);

  useEffect(() => {
    timeTableUpdate(todoId, categoryColorCode, selectTime.startTime, selectTime.endTime);
  }, [selectTime]);

  useEffect(() => {
    closeButtonUpdate();
  }, [closeTime]);

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
              onMouseEnter={() => mouseModule.mouseEnter(item.time)}
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
