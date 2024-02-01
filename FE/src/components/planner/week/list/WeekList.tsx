import React, { useState, Dispatch, SetStateAction, useRef, useMemo, useLayoutEffect } from "react";
import styles from "@styles/planner/Week.module.scss";
import dayStyles from "@styles/planner/day.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import WeekItem from "@components/planner/week/list/WeekItem";
import WeekItemInput from "@components/planner/week/list/WeekItemInput";
import { dateFormat } from "@util/getThisWeek";
import { TodoConfig } from "@util/planner.interface";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { useNavigate } from "react-router-dom";
import { DayListConfig, selectDayList, selectWeekDday, setDayList } from "@store/planner/weekSlice";
import { selectUserId } from "@store/authSlice";
import { BASIC_TODO_ITEM, setDayDate } from "@store/planner/daySlice";
import { plannerApi } from "@api/Api";
import dragModule from "@util/DragModule";
import dayjs from "dayjs";

interface Props {
  idx: number;
  isMine: boolean;
  today: DayListConfig;
  retroClick: number;
  setRetroClick: Dispatch<SetStateAction<number>>;
}

const WeekList = ({ idx, isMine, today, retroClick, setRetroClick }: Props) => {
  const dispatch = useAppDispatch();
  const navigator = useNavigate();
  const userId = useAppSelector(selectUserId);
  const nearDate = useAppSelector(selectWeekDday);
  const dayList: DayListConfig[] = useAppSelector(selectDayList);
  const date = dayList[idx].date;
  const [dailyTodos, setDailyTodos] = useState<TodoConfig[]>(dayList[idx].dailyTodos ?? []);
  const [retrospection, setRetrospection] = useState<string>(dayList[idx].retrospection ?? "");
  const listMaxLength = 5;
  const retroMaxLength = 100;
  const friend = isMine ? "" : "--friend";

  const draggablesRef = useRef<HTMLDivElement[]>([]);
  const copyDailyTodos: TodoConfig[] = useMemo(() => JSON.parse(JSON.stringify(dailyTodos)), [dailyTodos]);
  const [clicked, setClicked] = useState<number>(-1);

  const handleSaveRetrospection = () => {
    if (dayList[idx].retrospection === null && retrospection === "") return;
    if (dayList[idx].retrospection === retrospection) return;
    plannerApi
      .retrospections(userId, {
        date: dayjs(date).format("YYYY-MM-DD"),
        retrospection: retrospection,
      })
      .then(() => {
        const copyDayList = [...dayList];
        copyDayList[idx].retrospection = retrospection;
        dispatch(setDayList(copyDayList));
      })
      .catch((err) => console.log(err));
  };

  const handleMoveToDay = () => {
    dispatch(setDayDate(today.date));
    navigator("/day");
  };

  useLayoutEffect(() => {
    setDailyTodos(dayList[idx].dailyTodos ?? []);
    setRetrospection(dayList[idx].retrospection ?? "");
  }, [dayList]);

  const dragHoverStyle = dayStyles["todo-draggable"];
  const { containerDragOver, dragStart, dragEnter, dragEnd, ...childMouseEvent } = dragModule({
    date,
    todos: copyDailyTodos,
    setTodos: setDailyTodos,
    dragClassName: dragHoverStyle,
    draggablesRef,
  });

  return (
    <div className={styles["item"]}>
      <div className={styles["item__title"]}>
        <div onClick={handleMoveToDay}>
          <Text>{dateFormat(date)}</Text>
        </div>
        <Dday nearDate={nearDate} comparedDate={date} />
      </div>
      <div className={styles["item__todo-list"]}>
        <div
          onDragOver={(e) => {
            if (clicked !== idx) return false;
            containerDragOver(e);
          }}
        >
          {dailyTodos.map((item: TodoConfig, key: number) => (
            <div
              key={item.todoId}
              ref={(el: HTMLDivElement) => (draggablesRef.current[key] = el)}
              style={{ height: dailyTodos.length < 1 ? 0 : `calc(100% / ${listMaxLength})` }}
              draggable
              onDragStart={(e) => {
                setClicked(idx);
                dragStart(e, key);
              }}
              onDragEnter={(e) => dragEnter(e, key)}
              onDragOver={(e) => e.preventDefault()}
              onDragLeave={(e) => e.preventDefault()}
              onDragEnd={(e) => {
                setClicked(-1);
                dragEnd(e, item.todoId);
              }}
            >
              <WeekItem
                idx={key}
                item={item}
                isMine={isMine}
                date={date}
                dailyTodos={dailyTodos}
                setDailyTodos={setDailyTodos}
                dragModule={childMouseEvent}
              />
            </div>
          ))}
        </div>
        {isMine && (
          <div style={{ height: `calc((100% / ${listMaxLength}))` }}>
            <WeekItemInput date={date} dailyTodos={dailyTodos} setDailyTodos={setDailyTodos} />
          </div>
        )}
        {dailyTodos.length < listMaxLength && (
          <>
            {Array.from({
              length: isMine ? listMaxLength - dailyTodos.length - 1 : listMaxLength - dailyTodos.length,
            }).map((_, idx) => (
              <div key={idx.toString()} style={{ height: `calc(100% / ${listMaxLength})` }}>
                <WeekItem
                  idx={idx}
                  item={BASIC_TODO_ITEM}
                  disable
                  date={date}
                  dailyTodos={dailyTodos}
                  setDailyTodos={setDailyTodos}
                />
              </div>
            ))}
          </>
        )}
      </div>
      <div className={styles[`item__memo${friend}`]}>
        <textarea
          disabled={!isMine}
          value={retrospection}
          maxLength={retroMaxLength}
          placeholder="💡 오늘의 회고를 입력하세요."
          onClick={() => setRetroClick(idx)}
          onChange={(e) => setRetrospection(e.target.value)}
          onBlur={handleSaveRetrospection}
        />
        {isMine && idx === retroClick && (
          <Text types="small">
            ({retrospection.length}/{retroMaxLength}자)
          </Text>
        )}
      </div>
    </div>
  );
};

export default WeekList;
