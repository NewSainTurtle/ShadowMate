import React, { useEffect, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import DoDisturbOnIcon from "@mui/icons-material/DoDisturbOn";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import {
  selectDayDate,
  selectTodoItem,
  selectTodoList,
  setTimeTable,
  BASIC_CATEGORY_ITEM,
} from "@store/planner/daySlice";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import isSameOrAfter from "dayjs/plugin/isSameOrAfter";
import { TodoConfig } from "@util/planner.interface";
import { plannerApi } from "@api/Api";
import { debouncing } from "@util/EventControlModule";
import { selectUserId } from "@store/authSlice";

dayjs.extend(isSameOrBefore);
dayjs.extend(isSameOrAfter);

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
  const userId = useAppSelector(selectUserId);
  const date = useAppSelector(selectDayDate);
  const selectItem: TodoConfig = useAppSelector(selectTodoItem);
  const [todoId, categoryColorCode] = [
    selectItem.todoId,
    selectItem.category?.categoryColorCode || BASIC_CATEGORY_ITEM.categoryColorCode,
  ];
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

        if (selectItem.timeTable && selectItem.timeTable.startTime != "")
          deleteTimeTable(todoId).then(() => saveTimeTable(endTime));
        else saveTimeTable(endTime);
      }
    };
    const debounceMouseEnter = debouncing(mouseEnter, 50);

    return {
      mouseDown,
      debounceMouseEnter,
      mouseUp,
    };
  })();

  const saveTimeTable = async (endTime: string) => {
    let { startTime } = selectTime;
    if (startTime > endTime) [startTime, endTime] = [endTime, startTime];
    startTime = dayjs(startTime).subtract(10, "m").format("YYYY-MM-DD HH:mm");

    await todoList.map((item: TodoConfig) => {
      if (!!startTime && item.timeTable && !!item.timeTable.startTime) {
        if (
          !(
            dayjs(item.timeTable.endTime).isSameOrBefore(startTime) ||
            dayjs(item.timeTable.startTime).isSameOrAfter(endTime)
          )
        )
          deleteTimeTable(item.todoId);
      }
    });

    await plannerApi
      .timetables(userId, { date, todoId, startTime, endTime })
      .then(() => {
        dispatch(setTimeTable({ todoId: todoId, startTime, endTime }));
        setSelectTime({ startTime: "", endTime: "" });
      })
      .catch((err) => console.error(err));
  };

  const deleteTimeTable = async (todoId: number) => {
    await plannerApi
      .deleteTimetable(userId, { date, todoId: todoId })
      .then(() => dispatch(setTimeTable({ todoId, startTime: "", endTime: "" })));
  };

  useEffect(() => {
    let tempArr = [...makeTimeArr];
    todoList
      .filter((ele: TodoConfig) => ele.timeTable && ele.timeTable.startTime != "" && ele.timeTable.endTime != "")
      .map((item: TodoConfig) => {
        const { todoId, category } = item;
        const timeTable = item.timeTable!;
        let { startTime, endTime } = timeTable;
        const miniArr: tableTimeType[] = [];
        let tempTime = startTime;
        while (tempTime != endTime) {
          tempTime = dayjs(tempTime).add(10, "m").format("YYYY-MM-DD HH:mm");
          miniArr.push({
            todoId,
            categoryColorCode: category?.categoryColorCode || BASIC_CATEGORY_ITEM.categoryColorCode,
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
        if (dayjs(obj.time).isSameOrAfter(startTime) && dayjs(obj.time).isSameOrBefore(endTime)) {
          return { todoId, categoryColorCode, time: obj.time };
        } else return obj;
      });
      setTimeArr(dragArr);
    }
  }, [selectTime]);

  const clickedStyle = clicked ? styles["--clicked"] : "";
  const todoListNoneStyle = !todoList.length ? styles["--none"] : "";

  return (
    <div className={styles["timetable__container"]} onClick={() => setClicked(true)}>
      <div className={`${styles["timetable__container-box"]} ${todoListNoneStyle} ${clickedStyle} `}>
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
