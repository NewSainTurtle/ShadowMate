import React, { useEffect, useMemo, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import Modal from "@components/common/Modal";
import DeleteModal from "@components/common/Modal/DeleteModal";
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
import { TimeTableConfig, TodoConfig } from "@util/planner.interface";
import { plannerApi } from "@api/Api";
import { debouncing } from "@util/EventControlModule";
import { selectUserId } from "@store/authSlice";

dayjs.extend(isSameOrBefore);
dayjs.extend(isSameOrAfter);

interface Props {
  clicked: boolean;
  setClicked: (props: boolean) => void;
}

interface TimeTableType {
  todoId: number;
  categoryColorCode: string;
  timeTableId: number;
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
    selectItem.category?.categoryColorCode ?? BASIC_CATEGORY_ITEM.categoryColorCode,
  ];
  const todoList: TodoConfig[] = useAppSelector(selectTodoList);
  const makeTimeArr: TimeTableType[] = (() => {
    const plannerDate = dayjs(date).startOf("d").format("YYYY-MM-DD");
    // 오전 4시 ~ 익일 4시
    const dayStartTime = dayjs(plannerDate).set("h", 4).format("YYYY-MM-DD HH:mm");
    const dayEndTime = dayjs(plannerDate).add(1, "day").set("h", 4).format("YYYY-MM-DD HH:mm");
    const tempArr: TimeTableType[] = [];
    let tempTime = dayStartTime;
    while (tempTime != dayEndTime) {
      tempTime = dayjs(tempTime).add(10, "m").format("YYYY-MM-DD HH:mm");
      tempArr.push({ todoId: 0, timeTableId: 0, categoryColorCode: "", time: tempTime });
    }
    return tempArr;
  })();

  const [timeArr, setTimeArr] = useState<TimeTableType[]>(makeTimeArr);
  const [copyTimeArr, setCopyTimeArr] = useState<TimeTableType[]>(makeTimeArr);
  const [timeClick, setTimeClicked] = useState(false);
  const [selectTime, setSelectTime] = useState({
    startTime: "",
    endTime: "",
  });

  const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
  const [deleteTimeIdx, setDeleteTimeIdx] = useState({
    todoId: 0,
    timeTableId: 0,
  });
  const handleDeleteModalOpen = () => setDeleteModalOpen(true);
  const handleDeleteModalClose = () => setDeleteModalOpen(false);
  const activeTodoCount = useMemo(() => {
    const status = todoList.filter((ele) => ele.todoStatus == "완료" || ele.todoStatus == "진행중").length;
    const saveTime = todoList.filter((ele) => ele.timeTables).length;
    return { status, saveTime };
  }, [todoList]);

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
        saveTimeTable(endTime);
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
    if (!startTime) return;
    if (startTime > endTime) [startTime, endTime] = [endTime, startTime];
    startTime = dayjs(startTime).subtract(10, "m").format("YYYY-MM-DD HH:mm");

    /** 다른 todod와 겹칠 때, 다른 todo의 등록된 시간 삭제 */
    (async () => {
      await Promise.resolve(
        todoList.map((item: TodoConfig) => {
          item.timeTables?.map((time) => {
            if (!(dayjs(time.endTime).isSameOrBefore(startTime) || dayjs(time.startTime).isSameOrAfter(endTime))) {
              deleteTimeTable(item.todoId, time.timeTableId);
            }
          });
        }),
      );
    })();

    await plannerApi
      .timetables(userId, { date, todoId, startTime, endTime })
      .then((res) => {
        const { timeTableId } = res.data.data;
        dispatch(setTimeTable({ todoId, timeTableId, startTime, endTime }));
        setSelectTime({ startTime: "", endTime: "" });
      })
      .catch((err) => console.error(err));
  };

  const deleteTimeTable = async (todoId: number, timeTableId: number) => {
    await plannerApi
      .deleteTimetable(userId, { date, todoId, timeTableId })
      .then(() => dispatch(setTimeTable({ todoId, timeTableId })))
      .catch((err) => console.error(err))
      .finally(() => handleDeleteModalClose());
  };

  useEffect(() => {
    const tempArr = [...makeTimeArr];
    todoList
      .filter((ele: TodoConfig) => ele.timeTables && ele.timeTables.length > 0)
      .forEach((item: TodoConfig) => {
        const { todoId, category, timeTables } = item;
        timeTables?.forEach((time: TimeTableConfig) => {
          const miniArr: TimeTableType[] = [];
          let { timeTableId, startTime, endTime } = time;
          let tempTime = startTime;
          while (tempTime != endTime) {
            tempTime = dayjs(tempTime).add(10, "m").format("YYYY-MM-DD HH:mm");
            miniArr.push({
              todoId,
              categoryColorCode: category?.categoryColorCode ?? BASIC_CATEGORY_ITEM.categoryColorCode,
              timeTableId,
              time: tempTime,
              closeButton: tempTime == endTime,
            });
          }
          startTime = dayjs(startTime).add(10, "m").format("YYYY-MM-DD HH:mm");
          const startIndex = tempArr.findIndex((e) => e.time == startTime);
          tempArr.splice(startIndex, miniArr.length, ...miniArr);
        });
      });

    setTimeArr(tempArr);
    setCopyTimeArr(tempArr);
  }, [todoList]);

  useEffect(() => {
    let { startTime, endTime } = selectTime;
    if (startTime != "" && endTime != "") {
      if (startTime > endTime) [startTime, endTime] = [endTime, startTime];
      const dragArr: TimeTableType[] = copyTimeArr.map((obj) => {
        if (dayjs(obj.time).isSameOrAfter(startTime) && dayjs(obj.time).isSameOrBefore(endTime)) {
          return { todoId, categoryColorCode, timeTableId: 0, time: obj.time };
        } else return obj;
      });
      setTimeArr(dragArr);
    }
  }, [selectTime]);

  const timeTableClick = () => {
    if (activeTodoCount.status > 0) setClicked(true);
  };

  const timeTableStyle = (() => {
    if (todoId != 0 && activeTodoCount.saveTime == 0) return "--dragBefore";
    if (clicked && activeTodoCount.saveTime == 0) return "--clicked";
    if (timeClick || activeTodoCount.saveTime != 0) return "--drag";
    if (!todoList.length) return "--none";
    if (activeTodoCount.status == 0) return "--stateNone";
    return "--defalut";
  })();

  return (
    <>
      <div className={styles["timetable__container"]} onClick={timeTableClick}>
        <div className={`${styles["timetable__container-box"]} ${styles[timeTableStyle]}`}>
          <div className={styles["timetable__hours"]}>
            {Array.from({ length: 24 }).map((_, idx) => {
              const hour = (4 + idx) % 24;
              return <div key={hour}>{String(hour).padStart(2, "0")}</div>;
            })}
          </div>
          <div className={styles["timetable__minutes"]}>
            {timeArr.map((item) => (
              <div
                key={item.time}
                className={styles["timetable__minutes__item"]}
                onMouseDown={(e) => mouseModule.mouseDown(e, item.time)}
                onMouseEnter={() => mouseModule.debounceMouseEnter(item.time)}
                onMouseUp={() => mouseModule.mouseUp(item.time)}
                style={{ backgroundColor: item.categoryColorCode }}
              >
                {item.closeButton && (
                  <div
                    onClick={() => {
                      const { todoId, timeTableId } = item;
                      handleDeleteModalOpen();
                      setDeleteTimeIdx({ todoId, timeTableId });
                    }}
                  >
                    <DoDisturbOnIcon />
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
      <Modal
        types="twoBtn"
        open={deleteModalOpen}
        onClose={handleDeleteModalClose}
        onClick={() => deleteTimeTable(deleteTimeIdx.todoId, deleteTimeIdx.timeTableId)}
        onClickMessage="삭제"
        warning
      >
        <DeleteModal types="타임테이블" />
      </Modal>
    </>
  );
};

export default TimeTable;
