import React, { useEffect, useState, useMemo } from "react";
import styles from "@styles/planner/Week.module.scss";
import Text from "@components/common/Text";
import Dday from "@components/common/Dday";
import WeekItem from "@components/planner/week/list/WeekItem";
import WeekItemInput from "@components/planner/week/list/WeekItemInput";
import { dateFormat } from "@util/getThisWeek";
import { TodoConfig } from "@util/planner.interface";
import { useAppDispatch, useAppSelector } from "@hooks/hook";
import { DayListConfig, selectDayList, selectWeekDday, setDayList } from "@store/planner/weekSlice";
import { plannerApi } from "@api/Api";
import { selectUserId } from "@store/authSlice";
import dayjs from "dayjs";

interface Props {
  idx: number;
  isMine: boolean;
}

const WeekList = ({ idx, isMine }: Props) => {
  const dispatch = useAppDispatch();
  const userId = useAppSelector(selectUserId);
  const nearDate = useAppSelector(selectWeekDday);
  const dayList: DayListConfig[] = useAppSelector(selectDayList);
  const date = dayList[idx].date;
  const [dailyTodos, setDailyTodos] = useState<TodoConfig[]>(dayList[idx].dailyTodos || []);
  const [retrospection, setRetrospection] = useState<string>(dayList[idx].retrospection || "");
  const copyDayList = useMemo(() => JSON.parse(JSON.stringify(dayList)), [dayList]);
  const itemMaxLength = isMine ? 4 : 5;
  const rowMaxLength = isMine ? dailyTodos.length + 1 : dailyTodos.length;

  const handleSaveRetrospection = () => {
    if (dayList[idx].retrospection === null && retrospection === "") return;
    if (dayList[idx].retrospection === retrospection) return;
    plannerApi
      .retrospections(userId, {
        date: dayjs(date).format("YYYY-MM-DD"),
        retrospection: retrospection,
      })
      .then(() => {
        copyDayList[idx].retrospection = retrospection;
        dispatch(setDayList(copyDayList));
      })
      .catch((err) => console.log(err));
  };

  useEffect(() => {
    setDailyTodos(dayList[idx].dailyTodos || []);
  }, [dayList]);

  return (
    <div className={styles[`item`]}>
      <div className={styles["item__title"]}>
        <Text>{dateFormat(date)}</Text>
        <Dday nearDate={nearDate} comparedDate={dateFormat(date)} />
      </div>
      <div className={styles["item__todo-list"]} style={{ gridTemplateRows: `repeat(${rowMaxLength}, 20%` }}>
        {dailyTodos.map((item: TodoConfig, key: number) => (
          <WeekItem
            key={key}
            idx={key}
            item={item}
            isMine={isMine}
            date={date}
            dailyTodos={dailyTodos}
            setDailyTodos={setDailyTodos}
          />
        ))}
        {isMine && <WeekItemInput date={date} dailyTodos={dailyTodos} setDailyTodos={setDailyTodos} />}
      </div>
      <div className={`${styles["item__memo"]} ${dailyTodos?.length < itemMaxLength && styles["top_border"]}`}>
        <textarea
          value={retrospection}
          placeholder="💡 오늘의 회고를 입력하세요."
          onChange={(e) => setRetrospection(e.target.value)}
          onBlur={handleSaveRetrospection}
        />
      </div>
    </div>
  );
};

export default WeekList;
